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
        implementation("io.vertx:vertx-jdbc-client:+")
        implementation("org.jetbrains.exposed:exposed-core:+")
        implementation("org.jetbrains.exposed:exposed-dao:+")
        implementation("org.jetbrains.exposed:exposed-jdbc:+")
        implementation("org.openjfx:javafx-swing:18")
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
