plugins {
  id("java")
  id("application")
  id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "wang.qrwells.sanke"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:+")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:+")
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.32")
  implementation("com.github.almasb:fxgl:+")

  implementation(project(":network"))
}

application {
  mainModule.set("wang.qrwells.snake")
  mainClass.set("wang.qrwells.snake.Main")
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