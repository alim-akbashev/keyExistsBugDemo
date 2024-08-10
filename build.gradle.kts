plugins {
    java
    `java-library`
}

dependencies {
    implementation("org.rocksdb:rocksdbjni:9.4.0")
}

repositories {
    mavenCentral()
}
