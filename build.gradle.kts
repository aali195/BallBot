import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.31"
    val shadowVersion = "7.1.0"

    kotlin("jvm") version kotlinVersion
    id("com.github.johnrengelman.shadow") version shadowVersion
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
val kotlinLoggingVersion = "2.0.11"
val slf4jSimpleVersion = "1.7.32"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

    implementation("net.dv8tion:JDA:$jdaVersion")
    implementation("com.github.minndevelopment:jda-ktx:$jdaKtxVersion")

    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jSimpleVersion")

    testImplementation(kotlin("test"))
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
            attributes(Pair("Main-Class", "com.github.ball.ballbot.BallBotKt"))
        }
    }
}
