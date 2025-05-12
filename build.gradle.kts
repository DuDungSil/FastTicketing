plugins {
    id("org.springframework.boot") version "3.4.2" 
    id("io.spring.dependency-management") version "1.1.4" 
    id("application")
    id("java")
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "application")
    apply(plugin = "java") 

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web") // 웹 애플리케이션
        implementation("org.springframework.boot:spring-boot-starter-websocket") // 웹 소켓
        implementation("org.springframework.boot:spring-boot-starter-data-jpa") // JPA
        
        // Lombok
        compileOnly("org.projectlombok:lombok:1.18.38")
        annotationProcessor("org.projectlombok:lombok:1.18.38")

        // MySQL
        implementation("mysql:mysql-connector-java:8.0.33")

        // Redis
        implementation ("org.springframework.data:spring-data-redis")
        implementation ("org.redisson:redisson-spring-boot-starter:3.23.5")

        // QueryDSL 설정
        implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
        implementation("com.querydsl:querydsl-core:5.0.0")

        annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
        annotationProcessor("jakarta.persistence:jakarta.persistence-api:3.1.0")
        annotationProcessor("jakarta.annotation:jakarta.annotation-api:2.1.1")

        // 테스트
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        runtimeOnly("com.h2database:h2")
    }
}

application {
    mainClass.set("org.example.App")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}