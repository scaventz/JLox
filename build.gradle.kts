plugins {
    application // implicitly applies java & distribution plugin
}

group = "me.scaventz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

// this makes running ./gradlew wrapper --gradle-version 7.2
// generates distributionUrl=https\://services.gradle.org/distributions/gradle-7.2-all.zip
tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

application {
    mainClass.set("com.scaventz.lox.Lox")
}

distributions {
    main {
        distributionBaseName.set("jlox")
        contents {
            from("src/main/resources/startup.bat")
        }
    }
}

tasks.jar {
    archiveFileName.set("jlox.jar")
}

tasks.named("distZip", Zip::class) {
    archiveClassifier.set("AMD-64")
}