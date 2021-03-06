import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.4.RELEASE"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
	kotlin("plugin.jpa") version "1.3.72"
	jacoco
}

group = "com.seamuslowry.branniganschess"
version = "1.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-oauth2-resource-server")
	implementation("org.springframework.security:spring-security-oauth2-jose")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-noarg")
	implementation("org.flywaydb:flyway-core")
	implementation("io.springfox:springfox-boot-starter:3.0.0")
	implementation("io.springfox:springfox-swagger-ui:3.0.0")
	runtimeOnly("org.postgresql:postgresql")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
		exclude(module = "mockito-core")
	}
	testImplementation("com.h2database:h2")
	testImplementation("com.ninja-squad:springmockk:2.0.1")
	testImplementation("org.awaitility:awaitility-kotlin:4.0.0")
	testImplementation("org.springframework.security:spring-security-test:5.2.2.RELEASE")

}

jacoco {
	toolVersion = "0.8.6"
}

fun Build_gradle.excludeTestFiles(): FileTree {
	return sourceSets.main.get().output.asFileTree.matching {
		exclude("**/*ApplicationKt.class")
	}
}

tasks.jacocoTestReport {
	reports {
		xml.isEnabled = true
		csv.isEnabled = true
		html.isEnabled = true
	}
	classDirectories.setFrom(excludeTestFiles())
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
	useJUnitPlatform {
		includeEngines("junit-jupiter", "spek2")
	}

	testLogging {
		events("passed", "failed", "skipped")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}
