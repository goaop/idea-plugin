import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intellijPlatformGradle)
    alias(libs.plugins.changelog)
    alias(libs.plugins.qodana)
    alias(libs.plugins.kover)
    alias(libs.plugins.grammarkit)
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

// GrammarKit 2023.3.x has a bug in its classpath wiring (apply$1$3$2 passes null
// to classpath()), so we cannot add to the classpath — we must replace it entirely
// after task creation to include the project's compiled PSI util classes.
afterEvaluate {
    tasks.named<org.jetbrains.grammarkit.tasks.GenerateParserTask>("generateParser") {
        // Use plain file() to avoid creating a task dependency on compileJava
        // (mustRunAfter constraints would otherwise create a circular chain)
        classpath = files(
            configurations.getByName("compileClasspath"),
            file("$buildDir/classes/java/main"),
            file("$buildDir/classes/kotlin/main")
        )
    }

    // GrammarKit 2023.3.0.3 puts @Override on accept(Visitor) which doesn't
    // override any supertype method — strip the annotation after generation.
    tasks.named("generateParser") {
        doLast {
            fileTree("src/main/java/com/aopphp/go/psi/impl").matching {
                include("*.java")
            }.forEach { f ->
                f.writeText(f.readText().replace("@Override\n  public void accept", "public void accept"))
            }
        }
    }
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    // Clean regeneration: deletes old PSI then regenerates fresh.
    // Run after compiling so PointcutPsiUtil is on the classpath:
    //   ./gradlew compileJava compileKotlin regenerateParser generateLexer
    register("regenerateParser") {
        group = "grammarkit"
        description = "Clean-regenerates parser and lexer from scratch"
        doFirst {
            delete(file("src/main/java/com/aopphp/go/psi"))
        }
        finalizedBy(generateParser, generateLexer)
    }

    generateParser {
        sourceFile.set(file("src/main/java/com/aopphp/go/parser/pointcut.bnf"))
        targetRootOutputDir.set(layout.projectDirectory.dir("src/main/java"))
        pathToParser.set("/com/aopphp/go/parser/PointcutParser.java")
        pathToPsiRoot.set("/com/aopphp/go/psi")
        purgeOldFiles.set(false)
    }

    generateLexer {
        sourceFile.set(file("src/main/java/com/aopphp/go/parser/PointcutLexer.flex"))
        targetOutputDir.set(layout.projectDirectory.dir("src/main/java/com/aopphp/go/parser"))
        purgeOldFiles.set(false)
        mustRunAfter(generateParser)
    }

    // generateParser/generateLexer are NOT wired as automatic compile dependencies because
    // GrammarKit cannot locate the compiled psiImplUtilClass at generation time
    // (chicken-and-egg: the util class depends on the generated PSI files).
    // Run ./gradlew generateParser generateLexer only after manually ensuring the util class
    // is compiled, or use IntelliJ's "Generate Parser Code" action which compiles first.
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
