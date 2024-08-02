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
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }

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
    doFirst {
        if(System.getenv("MVN_URL") == null) {
            throw IllegalArgumentException("Missing environment variable: MVN_URL")
        }
    }
}

gradlePlugin {
    plugins {
        create("publish") {
            id = "org.ivcode.gradle.publish"
            implementationClass = "org.ivcode.gradle.publish.MvnPublishPlugin"
        }
    }
}
