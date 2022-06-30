plugins {
  id("java")
  id("idea")
  kotlin("jvm")
}

group = "wang.qrwells"
version = "0.1"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
  implementation("org.jetbrains.exposed:exposed-core:+")
  implementation("org.jetbrains.exposed:exposed-dao:+")
  implementation("org.jetbrains.exposed:exposed-jdbc:+")
  implementation("com.arkivanov.decompose:decompose:+")
  implementation("org.xerial:sqlite-jdbc:3.36.0.3")
  implementation("ch.qos.logback:logback-classic:1.2.11")
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("commons-logging:commons-logging:1.2")
  implementation("org.springframework.security:spring-security-crypto:+")
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}