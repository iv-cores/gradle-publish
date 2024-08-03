group = "org.ivcode"
version = "0.1-SNAPSHOT"

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    `maven-publish`
}

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
}

publishing {
    // only define the repository if the url is set
    // MVN_URL is required, but only when publishing
    if(System.getenv("MVN_URL") != null) {
        repositories {
            maven {
                url = uri(System.getenv("MVN_URL"))

                if(System.getenv("MVN_USERNAME") != null && System.getenv("MVN_PASSWORD") != null) {
                    credentials(PasswordCredentials::class.java) {
                        username = System.getenv("MVN_USERNAME")
                        password = System.getenv("MVN_PASSWORD")
                    }
                } else if (System.getenv("MVN_USERNAME") != null || System.getenv("MVN_PASSWORD") != null) {
                    throw IllegalArgumentException("Malformed Credentials: if username or password is set, both must be set")
                }
            }
        }
    }
}

tasks.named("publish").configure {
    // ensure the url is set before publishing
    doFirst {
        if(System.getenv("MVN_URL") == null) {
            throw IllegalArgumentException("Missing environment variable: MVN_URL")
        }
    }
}

gradlePlugin {
    plugins {
        create("gradle-publish") {
            id = "org.ivcode.gradle-publish"
            implementationClass = "org.ivcode.gradle.publish.MvnPublishPlugin"
        }
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.0")

}

tasks.test {
    useJUnitPlatform()
}
