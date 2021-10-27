import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.31"

    kotlin("jvm") version kotlinVersion
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
}
