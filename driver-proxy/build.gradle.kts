/*
 * AWS JDBC Proxy Driver
 * Copyright Amazon.com Inc. or affiliates.
 * See the LICENSE file in the project root for more information.
 */

import org.jdbcProxyDriver.buildtools.JavaCommentPreprocessorTask

plugins {
    checkstyle
    java
    jacoco
    id("com.diffplug.spotless")
    id("com.github.spotbugs")
    id("com.github.vlsi.gradle-extensions")
    id("com.github.vlsi.ide")
}

dependencies {
    implementation("org.checkerframework:checker-qual:3.22.+")

    testImplementation("org.junit.platform:junit-platform-commons:1.8.+")
    testImplementation("org.junit.platform:junit-platform-engine:1.8.+")
    testImplementation("org.junit.platform:junit-platform-launcher:1.8.+")
    testImplementation("org.junit.platform:junit-platform-suite-engine:1.8.+")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.+")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.+")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.apache.commons:commons-dbcp2:2.8.0")
    testImplementation("org.postgresql:postgresql:42.+")
    testImplementation("mysql:mysql-connector-java:8.0.+")
    testImplementation("org.mariadb.jdbc:mariadb-java-client:3.+")
    testImplementation("com.zaxxer:HikariCP:4.+") // Version 4.+ is compatible with Java 8
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc:2.7.+")
    testImplementation("org.mockito:mockito-inline:4.+")
    testImplementation("software.amazon.awssdk:rds:2.17.+")
    testImplementation("software.amazon.awssdk:ec2:2.17.+")
    testImplementation("org.testcontainers:testcontainers:1.17.+")
    testImplementation("org.testcontainers:mysql:1.17.+")
    testImplementation("org.testcontainers:postgresql:1.17.+")
    testImplementation("org.testcontainers:junit-jupiter:1.17.+")
    testImplementation("org.testcontainers:toxiproxy:1.17.+")
    testImplementation("org.slf4j:slf4j-simple:1.7.+")
}

tasks.check {
    dependsOn("jacocoTestCoverageVerification")
}

tasks.test {
    filter.excludeTestsMatching("integration.*")
}

checkstyle {
    // Checkstyle versions 7.x, 8.x, and 9.x are supported by JRE version 8 and above.
    toolVersion = "9.3"
    // Fail the build if there is at least one Checkstyle warning.
    maxWarnings = 0
    configDirectory.set(File(rootDir, "config/checkstyle"))
    configFile = configDirectory.get().file("google_checks.xml").asFile
}

spotless {
    isEnforceCheck = false

    format("misc") {
        target("*.gradle", "*.md", ".gitignore")

        trimTrailingWhitespace()
        indentWithTabs()
        endWithNewline()
    }

    java {
        googleJavaFormat("1.7")
    }
}

spotbugs {
    ignoreFailures.set(true)
}

tasks.spotbugsMain {
    reports.create("html") {
        required.set(true)
        outputLocation.set(file("$buildDir/reports/spotbugsMain.html"))
        setStylesheet("fancy-hist.xsl")
    }
}
tasks.spotbugsTest {
    reports.create("html") {
        required.set(true)
        outputLocation.set(file("$buildDir/reports/spotbugsTest.html"))
        setStylesheet("fancy-hist.xsl")
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                // Coverage verification will pass if it is greater than or equal to 1%.
                minimum = "0.01".toBigDecimal()
            }
        }
    }
}

val preprocessVersion by tasks.registering(JavaCommentPreprocessorTask::class) {
    baseDir.set(projectDir)
    sourceFolders.add("src/main/version/")
}

ide {
    generatedJavaSources(
        preprocessVersion,
        preprocessVersion.get().outputDirectory.get().asFile,
        sourceSets.main
    )
}

tasks.withType<Test> {
    useJUnitPlatform()

    fun passProperty(name: String, default: String? = null) {
        val value = System.getProperty(name) ?: default
        value?.let { systemProperty(name, it) }
    }

    systemProperty("java.util.logging.config.file", "${project.buildDir}/resources/test/logging-test.properties")
}

// Run Aurora Postgres integrations tests in container
tasks.register<Test>("test-integration-aurora-postgres") {
    group = "verification"
    filter.includeTestsMatching("integration.host.AuroraPostgresContainerTest.runTestInContainer")
}

tasks.register<Test>("test-performance-aurora-postgres") {
    group = "verification"
    filter.includeTestsMatching("integration.host.AuroraPostgresContainerTest.runPerformanceTestInContainer")
}

// Run standard Postgres tests in container
tasks.register<Test>("test-integration-standard-postgres") {
    group = "verification"
    filter.includeTestsMatching("integration.host.StandardPostgresContainerTest.runTestInContainer")
}

// Run Aurora Postgres integration tests in container with debugger
tasks.register<Test>("debug-integration-aurora-postgres") {
    group = "verification"
    filter.includeTestsMatching("integration.host.AuroraPostgresContainerTest.debugTestInContainer")
}

tasks.register<Test>("debug-performance-aurora-postgres") {
    group = "verification"
    filter.includeTestsMatching("testsuite.integration.host.AuroraPostgresContainerTest.debugPerformanceTestInContainer")
}

// Run standard Postgres integration tests in container with debugger
tasks.register<Test>("debug-integration-standard-postgres") {
    group = "verification"
    filter.includeTestsMatching("integration.host.StandardPostgresContainerTest.debugTestInContainer")
}

// Run standard Mysql tests in container
tasks.register<Test>("test-integration-standard-mysql") {
    group = "verification"
    filter.includeTestsMatching("integration.host.StandardMysqlContainerTest.runTestInContainer")
}

// Run standard Mysql integration tests in container with debugger
tasks.register<Test>("debug-integration-standard-mysql") {
    group = "verification"
    filter.includeTestsMatching("integration.host.StandardMysqlContainerTest.debugTestInContainer")
}

tasks.withType<Test> {
    dependsOn("jar")
    this.testLogging {
        this.showStandardStreams = true
    }
    useJUnitPlatform()
}
