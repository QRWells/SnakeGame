plugins {
  id("java")
  id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "wang.qrwells"
version = "0.1"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

javafx {
  version = "18"
  modules = listOf(
    "javafx.controls",
    "javafx.graphics",
    "javafx.fxml",
    "javafx.media"
  )
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}