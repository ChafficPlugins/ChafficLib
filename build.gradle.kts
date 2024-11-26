plugins {
    kotlin("jvm") version "2.1.0-RC"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.jetbrains.dokka") version "1.9.20"
    id("jacoco")
}

group = "de.chafficplugins"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.3")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.0.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("ChafficLib")
        archiveVersion.set(version.toString())
    }

    jar {
        archiveBaseName.set("ChafficLib")
        archiveVersion.set(version.toString())
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    register<Jar>("javadocJar") {
        dependsOn(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc.get().destinationDir)
    }

    build {
        dependsOn(shadowJar)
        dependsOn("sourcesJar")
        dependsOn("javadocJar")
        dependsOn(jacocoTestCoverageVerification)
    }

    test {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)

        testLogging {
            events("PASSED", "SKIPPED", "FAILED", "STANDARD_OUT", "STANDARD_ERROR")

            showExceptions = true
            showCauses = true
            showStackTraces = true

            showStandardStreams = true

            displayGranularity = 2

            afterSuite(
                KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
                    if (desc.parent == null) {
                        println("\nTest result: ${result.resultType}")
                        println(
                            "Test summary: ${result.testCount} tests, " +
                                "${result.successfulTestCount} succeeded, " +
                                "${result.failedTestCount} failed, " +
                                "${result.skippedTestCount} skipped",
                        )
                    }
                }),
            )
        }
    }

    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required.set(true)
            html.required.set(true)
        }

        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) {
                        exclude("**/*\$Companion\$*.*")
                    }
                },
            ),
        )
    }

    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    minimum = "0.7".toBigDecimal()
                }
                element = "CLASS"
                excludes = listOf("*.Companion")
            }

            rule {
                element = "METHOD"
                excludes = listOf("@javax.annotation.Generated")
            }

            rule {
                element = "METHOD"
                excludes = listOf("*\$\$inlined*", "*\$Companion\$*")
            }
        }
    }

    dokkaHtml {
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
