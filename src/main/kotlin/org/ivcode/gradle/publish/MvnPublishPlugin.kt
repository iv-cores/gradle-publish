package org.ivcode.gradle.publish

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure

/**
 * A gradle plugin for publishing artifacts to maven
 */
class MvnPublishPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("publish", PublishExtension::class.java)
        setupAfterEvaluate(project)
    }

    /**
     * Sets up the publish extension and configures Maven publishing after the project is evaluated.
     *
     * @param project The Gradle project to configure.
     */
    private fun setupAfterEvaluate(project: Project) = project.afterEvaluate {
        val publishExtension = project.extensions.getByName("publish") as PublishExtension
        val copy = publishExtension.finalize(project)

        setupMavenPublish(project, copy)
    }

    /**
     * Configures the Maven publishing plugin.
     *
     * @param project The Gradle project to configure.
     * @param publishExtension The publish extension to use for configuration.
     */
    private fun setupMavenPublish(project: Project, publishExtension: PublishExtension) = with(project) {
        // apply the maven-publish plugin
        plugins.apply("maven-publish")

        println("Register Publish Task")

        // ensure the url is set before publishing
        tasks.getByName("publish").doFirst {
            println("Publish Task: ${publishExtension.url}")
            println("is null: ${publishExtension.url==null}")
            requireNotNull(publishExtension.url) { "maven url must be set" }
        }

        // configure the publishing extension
        configure<PublishingExtension> {
            configurePublication(project, publishExtension)
            configureRepository(project, publishExtension)
        }
    }

    /**
     * Configures the Maven repository for publishing.
     *
     * @param project The Gradle project to configure.
     * @param publishExtension The publish extension to use for configuration.
     */
    private fun PublishingExtension.configureRepository (
        project: Project,
        publishExtension: PublishExtension
    ) = with(project) {

        if (publishExtension.url == null) {
            // if the url isn't defined, then we don't need to configure the repository.
            // the url is optional when not publishing
            return
        }

        repositories {
            maven {
                url = uri(publishExtension.url!!)
                isAllowInsecureProtocol = publishExtension.isAllowInsecureProtocol ?: false

                if(publishExtension.username != null && publishExtension.password != null) {
                    credentials(PasswordCredentials::class.java) {
                        username = publishExtension.username
                        password = publishExtension.password
                    }
                } else if (publishExtension.username != null || publishExtension.password != null) {
                    throw IllegalArgumentException("Malformed Credentials: if username or password is set, both must be set")
                }
            }
        }
    }

    /**
     * Configures the Maven publication for publishing.
     *
     * @param project The Gradle project to configure.
     * @param publishExtension The publish extension to use for configuration.
     */
    private fun PublishingExtension.configurePublication(
        project: Project,
        publishExtension: PublishExtension
    ) {
        publications.create("maven", MavenPublication::class.java) {
            groupId = publishExtension.groupId
            artifactId = publishExtension.artifactId
            version = publishExtension.version

            // Java Projects
            if(project.tasks.findByName("jar") != null) {
                if(project.plugins.hasPlugin("java-gradle-plugin")) {
                    println("The java-gradle-plugin and java publications conflict. The jar artifact will not be published.")
                } else {
                    artifact(project.tasks.named("jar").get())
                }
            }

            // Source Code
            if(project.tasks.findByName("sourcesJar") != null) {
                artifact(project.tasks.named("sourcesJar").get())
            }

            // Javadocs
            if (project.tasks.findByName("javadocJar") != null) {
                artifact(project.tasks.named("javadocJar").get())
            }

            // Spring Boot Projects.
            if(project.plugins.hasPlugin("org.springframework.boot") && project.tasks.findByName("bootJar") != null) {
                if(!project.plugins.hasPlugin("java-gradle-plugin")) {
                    println("The java-gradle-plugin and spring-boot publications conflict. The bootJar artifact will not be published.")
                } else {
                    artifact(project.tasks.named("bootJar").get())
                }
            }
        }
    }
}
