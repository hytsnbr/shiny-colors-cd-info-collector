plugins {
    id("java")
    id("idea")
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.hytsnbr"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starter
    implementation(libs.spring.boot.starter.batch)
    implementation(libs.spring.boot.starter.validation)

    // Spring Boot Configuration Processor
    annotationProcessor(libs.spring.boot.configuration.processor)

    // Apache Commons
    implementation(libs.bundles.apache.commons)

    // H2 Database
    runtimeOnly(libs.h2)

    // Jsoup Java HTML Parser
    implementation(libs.jsoup)

    // Jackson Datatype: JSR310（java.time系の読み込み用）
    implementation(libs.jackson.datatype.jsr310)

    // Gson
    implementation(libs.gson)

    // Unit Test
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.batch.test)
}

tasks.test {
    useJUnitPlatform()
}
tasks.compileJava {
    dependsOn("processResources")
}
tasks.jar {
    enabled = false
    dependsOn("bootJar")
}
