plugins {
    id("java")
    id("idea")
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.hytsnbr"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starter
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Spring Boot Configuration Processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Apache Commons
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.apache.commons:commons-collections4:4.4")

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // H2 Database
    runtimeOnly("com.h2database:h2")

    // Jsoup Java HTML Parser
    implementation("org.jsoup:jsoup:1.17.2")

    // Jackson Datatype: JSR310（java.time系の読み込み用）
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Unit Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
}

tasks.test {
    useJUnitPlatform()
}
tasks.build {
    dependsOn("clean")
}
tasks.compileJava {
    dependsOn("processResources")
}
tasks.jar {
    enabled = false
    dependsOn("bootJar")
}
