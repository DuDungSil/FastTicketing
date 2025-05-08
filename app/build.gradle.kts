plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // 모듈 
    implementation (project(":user-service"))
    implementation (project(":performance-service"))
    implementation (project(":ticket-service"))
    implementation (project(":common"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass.set("org.example.App")
}