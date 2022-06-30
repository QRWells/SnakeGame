import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

repositories {
  google()
  mavenCentral()
  gradlePluginPortal()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
  id("idea")
}

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

group = "wang.qrwells"
version = "1.0-SNAPSHOT"

kotlin {
  jvm {
    compilations.all {
      kotlinOptions.jvmTarget = "17"
    }
    withJava()
  }
  sourceSets {
    val jvmMain by getting {
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation("org.jetbrains.exposed:exposed-core:+")
        implementation("org.jetbrains.exposed:exposed-dao:+")
        implementation("org.jetbrains.exposed:exposed-jdbc:+")
        implementation("org.xerial:sqlite-jdbc:3.36.0.3")
        implementation("org.slf4j:slf4j-api:1.7.36")
        implementation("ch.qos.logback:logback-classic:1.2.11")
        implementation("org.springframework.security:spring-security-crypto:+")
        implementation(project(":network"))
      }
    }
    val jvmTest by getting
  }
}

compose.desktop {
  application {
    mainClass = "MainKt"
    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "chat"
      packageVersion = "1.0.0"
    }
  }
}
