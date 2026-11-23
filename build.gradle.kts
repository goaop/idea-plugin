import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intellijPlatformGradle)
    alias(libs.plugins.intellijPlatformGrammarKit)
    alias(libs.plugins.changelog)
    alias(libs.plugins.qodana)
    alias(libs.plugins.kover)
}

group = properties("pluginGroup").get()

// Work around Provider resolution issue in IntelliJ Platform Gradle Plugin with Gradle 9.0:
// the plugin sets project.version to an unresolved Provider whose toString() yields
// "valueof(GradlePropertyValueSource)" instead of the actual version string.
// afterEvaluate overrides it back to a plain String so the artifact filename is correct.
val pluginVer: String = file("gradle.properties").readLines()
    .first { it.startsWith("pluginVersion") }
    .substringAfter("= ")
    .trim()
afterEvaluate {
    version = pluginVer
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        phpstorm(providers.gradleProperty("platformVersion"))
        bundledPlugin("com.jetbrains.php")
        testFramework(TestFrameworkType.Platform)
    }
    testImplementation(libs.junit5.api)
    testImplementation(libs.junit5.params)
    testRuntimeOnly(libs.junit5.engine)
    testRuntimeOnly(libs.junit5.launcher)
    testImplementation(libs.junit4)
    testRuntimeOnly(libs.junitVintageEngine)
    testImplementation(libs.mockito.kotlin)
}

kotlin {
    jvmToolchain(21)
}

intellijPlatform {
    pluginConfiguration {
        name = properties("pluginName")

        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
            untilBuild = provider { null }
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

changelog {
    groups.empty()
    repositoryUrl = properties("pluginRepositoryUrl")
}

kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    // The lexer, parser and PSI are generated into src/main/java and committed: PointcutPsiUtil (psiImplUtilClass)
    // must be compiled before GrammarKit can generate the PSI that uses it, and it depends on that PSI itself.
    // Regenerate after changing the grammar (compile first, then generate, in two separate runs):
    //   ./gradlew compileJava compileKotlin
    //   ./gradlew generateParser generateLexer
    generateParser {
        sourceFile = file("src/main/java/com/aopphp/go/parser/pointcut.bnf")
        // Shared-root mode: only the parser class and the PSI root below src/main/java are outputs (and purged)
        targetRootOutputDir = layout.projectDirectory.dir("src/main/java")
        pathToParser = "com/aopphp/go/parser/PointcutParser.java"
        pathToPsiRoot = "com/aopphp/go/psi"
        purgeOldFiles = true

        // GrammarKit resolves the psiImplUtilClass methods by reflection, so the compiled classes of the plugin
        // (and the PHP plugin they reference) must be on its classpath. Plain files: no task dependency on compileJava.
        classpath(
            configurations.compileClasspath,
            layout.buildDirectory.dir("classes/java/main"),
            layout.buildDirectory.dir("classes/kotlin/main"),
        )

        // GrammarKit puts @Override on accept(Visitor), which doesn't override any supertype method
        val psiImplDir = layout.projectDirectory.dir("src/main/java/com/aopphp/go/psi/impl")
        doLast {
            psiImplDir.asFileTree.matching { include("*.java") }.forEach { f ->
                f.writeText(f.readText().replace("@Override\n  public void accept", "public void accept"))
            }
        }
    }

    generateLexer {
        sourceFile = file("src/main/java/com/aopphp/go/parser/PointcutLexer.flex")
        // Shared-root mode: only the lexer class below src/main/java is an output (and purged)
        targetRootOutputDir = layout.projectDirectory.dir("src/main/java")
        pathToClass = "com/aopphp/go/parser/PointcutLexer.java"
        purgeOldFiles = true
    }

    compileJava {
        mustRunAfter(generateLexer, generateParser)
    }

    compileKotlin {
        mustRunAfter(generateLexer, generateParser)
    }

    test {
        useJUnitPlatform()
    }

    patchPluginXml {
        version = pluginVer
        sinceBuild = properties("pluginSinceBuild")
        untilBuild = provider { null }

        pluginDescription = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog
        changeNotes = properties("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }
    }

    signPlugin {
        certificateChain = environment("CERTIFICATE_CHAIN")
        privateKey = environment("PRIVATE_KEY")
        password = environment("PRIVATE_KEY_PASSWORD")
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token = environment("PUBLISH_TOKEN")
        channels = properties("pluginVersion").map {
            listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
        }
    }
}
