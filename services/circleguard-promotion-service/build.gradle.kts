plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.2.4"))
    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:3.2.4"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:neo4j:1.19.3")
}
// Bloque añadido para inyectar variables de entorno de Jenkins a los tests
tasks.withType<Test> {
    useJUnitPlatform()
    
    // Inyecta el socket de Docker a la JVM de los tests
    environment("DOCKER_HOST", "unix:///var/run/docker.sock")
    // Desactiva validaciones problemáticas en entornos CI/CD (Docker-in-Docker)
    environment("TESTCONTAINERS_RYUK_DISABLED", "true")
    environment("TESTCONTAINERS_CHECKS_DISABLE", "true")
    
    // (Opcional) Hace que los logs de los tests sean más detallados en la consola de Jenkins
    testLogging {
        events("passed", "skipped", "failed")
    }
}