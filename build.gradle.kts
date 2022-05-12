plugins {
    application // implicitly applies java & distribution plugin
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

group = "me.scaventz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
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

tasks.assembleDist {
    dependsOn(tasks.test)
}

tasks.register<JavaExec>("generateAST") {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    })
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.scaventz.tool.GenerateAST")
}