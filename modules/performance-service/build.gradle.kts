dependencies {
    // 모듈 
    implementation (project(":common"))
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false 
}