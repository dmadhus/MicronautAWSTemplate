plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    id("org.jetbrains.kotlin.kapt") version "1.6.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.6.2"
    //enable to add compile time optimizations, will increase build time to reduce startup time
    //id("io.micronaut.aot") version "3.6.2"
    id("jacoco")
    id("info.solidsoft.pitest") version "1.7.0"
}

version = "0.1"
group = "com.bytestream"

val kotlinVersion=project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

allOpen {
    //this makes AOP work as it makes classes non-final :)
    annotations("io.micronaut.http.annotation.Controller", "io.micronaut.core.annotation.Introspected")
}

dependencies {
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.micrometer:micronaut-micrometer-annotation")
    kapt("io.micronaut.serde:micronaut-serde-processor")
    kapt("io.micronaut.openapi:micronaut-openapi:4.5.2")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut:micronaut-session")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")

    //bean validation support https://docs.micronaut.io/latest/guide/index.html#beanValidation
    implementation("io.micronaut:micronaut-validation")
    //If you need full JSR380 support then use the Hibernate version of the validator
    //implementation("io.micronaut.beanvalidation:micronaut-hibernate-validator")


    implementation("io.micronaut.micrometer:micronaut-micrometer-core")
    //use the plain micrometer registry for cloudwatch
    implementation("io.micrometer:micrometer-registry-cloudwatch2:1.9.5")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")

    //https://aws.amazon.com/blogs/developer/introducing-enhanced-dynamodb-client-in-the-aws-sdk-for-java-v2/
    implementation("software.amazon.awssdk:dynamodb-enhanced:2.17.295"){
        exclude(group = "software.amazon.awssdk", module = "apache-client")
        exclude(group = "software.amazon.awssdk", module = "netty-nio-client")

    }
    implementation("software.amazon.awssdk:auth:2.17.295"){
        exclude(group = "software.amazon.awssdk", module = "apache-client")
        exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
    }
    implementation("software.amazon.awssdk:sts:2.17.295"){
        exclude(group = "software.amazon.awssdk", module = "apache-client")
        exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
    }
    runtimeOnly("ch.qos.logback:logback-classic")
    compileOnly("org.graalvm.nativeimage:svm")
    implementation("io.micronaut:micronaut-validation")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("io.kotest:kotest-assertions-json:4.6.4")
    //testImplementation("io.kotest:kotest-runner-junit5:5.5.1")
    //testImplementation("io.kotest:kotest-assertions-core:5.5.1")

}


application {
    mainClass.set("com.bytestream.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
}



tasks.test {
    configure<JacocoTaskExtension> {
        isEnabled = true
        excludes = listOf("com/bytestream/config/**")
        excludeClassLoaders = emptyList()
        isIncludeNoLocationClasses = false
        output = JacocoTaskExtension.Output.FILE
    }
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}


tasks.jacocoTestReport {
    dependsOn(tasks.test)
    finalizedBy(tasks.jacocoTestCoverageVerification)

    classDirectories.setFrom(classDirectories.files.map {
        fileTree(it).matching {
            exclude("com/bytestream/config/**")
            exclude("com/bytestream/Application.class")
        }
    })
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            classDirectories.setFrom(tasks.jacocoTestReport.get().classDirectories)
            limit {
                minimum = "0.9".toBigDecimal()
            }
        }
    }
}

tasks.check{
    dependsOn(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}


jacoco {
    toolVersion = "0.8.7"
}
graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("kotest")
    processing {
        incremental(true)
        annotations("com.bytestream.*")
    }
    // aot {
    // Please review carefully the optimizations enabled below
    // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
    //optimizeServiceLoading.set(true)
    //convertYamlToJava.set(true)
    //precomputeOperations.set(true)
    //cacheEnvironment.set(true)
    //optimizeClassLoading.set(true)
    //deduceEnvironment.set(true) https://github.com/micronaut-projects/micronaut-aot/blob/master/aot-std-optimizers/src/main/java/io/micronaut/aot/std/sourcegen/DeduceEnvironmentSourceGenerator.java
    //optimizeNetty.set(true)
    // }
}



configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("io.micronaut:micronaut-jackson-databind"))
                .using(module("io.micronaut.serde:micronaut-serde-jackson:1.3.2"))
    }
}
