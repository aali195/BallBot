import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.31"
    val shadowVersion = "7.1.0"
    val flywayPluginVersion = "8.0.2"
    val jooqPluginVersion = "6.0.1"

    kotlin("jvm") version kotlinVersion
    id("com.github.johnrengelman.shadow") version shadowVersion
    id("org.flywaydb.flyway") version flywayPluginVersion
    id("nu.studer.jooq") version jooqPluginVersion
}

group = "com.github.ball"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io/")
}

val kotlinxCoroutinesVersion = "1.5.2"
val jdaKtxVersion = "1223d5cbb8a8caac6d28799a36001f1844d7aa7d"
val jdaVersion = "4.3.0_277"
val jacksonKotlinVersion = "2.13.0"
val postgresqlJdbcVersion = "42.3.1"
val jooqVersion = "3.15.4"
val flywayVersion = "8.0.2"
val kotlinLoggingVersion = "2.0.11"
val slf4jSimpleVersion = "1.7.32"

dependencies {
    jooqGenerator("org.postgresql:postgresql:$postgresqlJdbcVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

    implementation("net.dv8tion:JDA:$jdaVersion")
    implementation("com.github.minndevelopment:jda-ktx:$jdaKtxVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonKotlinVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("org.jooq:jooq-meta:$jooqVersion")
    implementation("org.postgresql:postgresql:$postgresqlJdbcVersion")

    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jSimpleVersion")

    testImplementation(kotlin("test"))
}

val mainPackage = "com.github.ball.ballbot"

val dbDriver = "org.postgresql.Driver"
val dbUrl = "jdbc:postgresql://localhost:5432/ballbot_db"
val dbUser = "ballbot"
val dbPassword = "toor"
val dbSchema = "ballbot_schema"

flyway {
    driver = dbDriver
    url = dbUrl
    user = dbUser
    password = dbPassword
    schemas = arrayOf(dbSchema)
}

jooq {
    version.set(jooqVersion)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = dbDriver
                    url = dbUrl
                    user = dbUser
                    password = dbPassword
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
                        inputSchema = dbSchema
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
