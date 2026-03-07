plugins {
    id("java")

    // Spring Boot
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)

    // コードフォーマッター
    alias(libs.plugins.spotless)

    // カバレッジツール
    id("jacoco")

    // 静的解析ツール
    id("checkstyle")
    id("pmd")
}

group = "com.hytsnbr"

java {
    toolchain {
        // JDKバージョン指定
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

spotless {
    // origin/mainブランチとの差分のみをフォーマット対象とする
    ratchetFrom("origin/main")

    java {
        // フォーマット対象
        target("src/**/*.java")
        // フォーマット対象外
        targetExclude("$rootProject.layout.buildDirectory/**/*.java")

        // フォーマット時、使用するコードスタイル
        googleJavaFormat().aosp()

        // 変数・フィールドなどに付随するアノテーションもフォーマット対象にする
        formatAnnotations()

        // インポート順
        importOrder("java|javax", "org", "com", "jp", "", "\\#")
        // フォーマット時に不必要なインポートを除去する
        removeUnusedImports()
    }

    kotlinGradle {
        // フォーマット対象
        target("*.gradle.kts")

        // 使用するフォーマッター
        ktlint("1.5.0")
    }
}

jacoco {
    toolVersion = "0.8.12"
}

checkstyle {
    toolVersion = "13.0.0"

    configFile = file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false // 違反がある場合にビルドを失敗させる
    maxWarnings = 0
}

pmd {
    toolVersion = "7.0.0"

    isConsoleOutput = true
    isIgnoreFailures = false

    // ルールセットを指定
    ruleSets =
        listOf(
            "category/java/errorprone.xml",
            "category/java/bestpractices.xml",
            "category/java/codestyle.xml",
            "category/java/design.xml",
        )
}

tasks {
    withType<Test> {
        // JUnit Platform を使用する
        useJUnitPlatform()

        // カバレッジレポート作成
        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        dependsOn(test)

        reports {
            csv.required.set(false)
            xml.required.set(true)
            html.required.set(true)
        }
    }

    withType<Delete> {
        doLast {
            // デフォルトのcleanタスクでは削除されない「bin」ディレクトリを削除する
            val binDir = file("./bin")
            binDir.deleteRecursively()
        }
    }

    withType<JavaCompile> {
        // ビルド時に下記タスクも並行して実行させる
        dependsOn("processResources")

        doLast {
            // additional-spring-configuration-metadata.json を自動生成させる
            copy {
                from("./build/classes/java/main/META-INF/spring-configuration-metadata.json")
                into("./src/main/resources/META-INF")

                rename {
                    "additional-spring-configuration-metadata.json"
                }
            }
        }
    }

    withType<Jar> {
        // 標準生成されるJarファイルでは動作しないのでタスクを無効化
        enabled = false
    }
}