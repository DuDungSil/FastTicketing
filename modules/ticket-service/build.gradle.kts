dependencies {
    // 모듈 
    implementation (project(":common"))
    implementation (project(":user-service"))
    implementation (project(":performance-service"))
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false 
}

application {
    mainClass.set("org.example.TicketApplication")
}