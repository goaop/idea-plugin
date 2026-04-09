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
version = properties("pluginVersion").get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

val grammarKitExtra: Configuration by configurations.creating

dependencies {
    grammarKitExtra("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.8")
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
}

changelog {
    groups.empty()
    repositoryUrl = properties("pluginRepositoryUrl")
}

koverReport {
    defaults {
        xml {
            onCheck = true
        }
    }
}

// Add the IDE-bundled opentelemetry.jar to the generateParser classpath after all dependencies
// are resolved. The Maven opentelemetry-api artifact breaks GrammarKit's PSI environment
// due to version mismatch with PhpStorm's internal OpenTelemetry usage.
afterEvaluate {
    val otelJar = configurations.getByName("compileClasspath").files
        .firstOrNull { jar -> jar.name == "opentelemetry.jar" && !jar.absolutePath.contains("/plugins/") }
    if (otelJar != null) {
        tasks.named<org.jetbrains.grammarkit.tasks.GenerateParserTask>("generateParser") {
            classpath(otelJar)
        }
    }
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    generateParser {
        sourceFile.set(file("src/main/java/com/aopphp/go/parser/pointcut.bnf"))
        targetRoot.set(file("src/main/java").absolutePath)
        pathToParser.set("/com/aopphp/go/parser/PointcutParser.java")
        pathToPsiRoot.set("/com/aopphp/go/psi")
        purgeOldFiles.set(false)
        classpath(grammarKitExtra)
    }

    generateLexer {
        sourceFile.set(file("src/main/java/com/aopphp/go/parser/PointcutLexer.flex"))
        targetDir.set(file("src/main/java/com/aopphp/go/parser").absolutePath)
        targetClass.set("PointcutLexer")
        purgeOldFiles.set(true)
        mustRunAfter(generateParser)
    }

    // generateParser/generateLexer are NOT wired as automatic compile dependencies because
    // GrammarKit cannot locate the compiled PointcutQueryPsiUtil class at generation time
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
        version = properties("pluginVersion")
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
