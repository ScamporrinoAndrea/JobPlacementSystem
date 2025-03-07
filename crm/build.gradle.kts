import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.jpa") version "1.9.23"
}
val springCloudVersion by extra("2023.0.2")

group = "it.polito.g21"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("org.springframework:spring-aspects")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.apache.camel.springboot:camel-spring-boot-starter:4.5.0")
	implementation("org.apache.camel.springboot:camel-google-mail-starter:4.5.0")
	implementation("org.apache.camel.springboot:camel-http-starter:4.5.0")
	implementation("org.apache.camel:camel-google-mail:4.5.0")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.kafka:spring-kafka")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
	testImplementation("io.mockk:mockk:1.12.0")
	implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("org.apache.camel:camel-core:3.14.1")
	implementation("org.apache.camel:camel-http:3.14.1")
	implementation("com.google.api-client:google-api-client:1.32.1")
	//implementation("com.google.apis:google-api-services-gmail:1.32.1")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")





}
dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
