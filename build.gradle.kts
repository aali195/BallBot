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
}

val kordVersion = "0.8.0-M7"
val slf4jSimpleVersion = "1.7.32"

dependencies {
    implementation("dev.kord:kord-core:$kordVersion")
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
