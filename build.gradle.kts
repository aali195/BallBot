import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*

plugins {
    val kotlinVersion = "1.6.10"
    val shadowVersion = "7.1.2"
    val flywayPluginVersion = "8.4.4"
    val jooqPluginVersion = "7.1"

    kotlin("jvm") version kotlinVersion
    id("com.github.johnrengelman.shadow") version shadowVersion
    id("org.flywaydb.flyway") version flywayPluginVersion
    id("nu.studer.jooq") version jooqPluginVersion
}

group = "com.github.ball"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io/")
}

val kotlinxCoroutinesVersion = "1.6.0"
val ktorVersion = "1.6.7"
val jdaKtxVersion = "1223d5cbb8a8caac6d28799a36001f1844d7aa7d"
val jdaVersion = "4.4.0_350"
val penicillinVersion = "6.2.2"
val jacksonKotlinVersion = "2.13.1"
val postgresqlJdbcVersion = "42.3.1"
val flywayVersion = "8.4.3"
val hikariVersion = "5.0.1"
val jooqVersion = "3.16.3"
val kotlinLoggingVersion = "2.1.21"
val slf4jSimpleVersion = "1.7.35"

dependencies {
    jooqGenerator("org.postgresql:postgresql:$postgresqlJdbcVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("net.dv8tion:JDA:$jdaVersion")
    implementation("com.github.minndevelopment:jda-ktx:$jdaKtxVersion")

    implementation("blue.starry:penicillin:$penicillinVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonKotlinVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.jooq:jooq-meta:$jooqVersion")
    implementation("org.postgresql:postgresql:$postgresqlJdbcVersion")

    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jSimpleVersion")

    testImplementation(kotlin("test"))
}

val mainPackage = "com.github.ball.ballbot"

val dbProperties = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "/db/db.properties")))
}

flyway {
    driver = dbProperties.getProperty("DB_DRIVER")
    url = dbProperties.getProperty("DB_URL")
    user = dbProperties.getProperty("DB_USER")
    password = dbProperties.getProperty("DB_PASSWORD")
    schemas = arrayOf(dbProperties.getProperty("DB_SCHEMA"))
}

jooq {
    version.set(jooqVersion)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = dbProperties.getProperty("dbDriver")
                    url = dbProperties.getProperty("dbUrl")
                    user = dbProperties.getProperty("dbUser")
                    password = dbProperties.getProperty("dbPassword")
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        includes = ".*"
                        excludes = listOf(
                            "flyway_schema_history",
                            "st_approxhistogram",
                            "_?st_histogram",
                            "st_approxquantile",
                            "st_pixelofvalue",
                            "_?st_valuecount",
                            "_?st_quantile",
                            "st_valuepercent",
                            "_st_tile",
                            "st_dumpvalues",
                            "geometry_columns",
                            "raster_columns",
                            "raster_overviews",
                            "spatial_ref_sys"
                        ).joinToString(separator = "|")
                        inputSchema = dbProperties.getProperty("dbSchema")
                        isTableValuedFunctions = false
                        isIncludeRoutines = false
                    }
                    generate.apply {
                        isDeprecated = false
                        isPojos = false
                        isJavaTimeTypes = true
                    }
                    target.apply {
                        packageName = "$mainPackage.domain.generated"
                        directory = "src/main/generated-src"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
        kotlinOptions.jvmTarget = "11"
    }
    test {
        useJUnitPlatform()
    }
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "$mainPackage.BallBotKt"))
        }
    }
}
