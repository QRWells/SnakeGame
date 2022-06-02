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
        implementation("io.vertx:vertx-jdbc-client:4.3.1")
        implementation("org.jetbrains.exposed:exposed-core:0.38.1")
        implementation("org.jetbrains.exposed:exposed-dao:0.38.1")
        implementation("org.jetbrains.exposed:exposed-jdbc:0.38.1")
        implementation("com.github.almasb:fxgl:17.1")
        implementation(project(":network"))
        implementation(project(":snake"))
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
