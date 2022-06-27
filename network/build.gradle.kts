plugins {
  id("java")
}

group = "wang.qrwells"
version = "0.1"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("ch.qos.logback:logback-classic:1.2.11")

}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}