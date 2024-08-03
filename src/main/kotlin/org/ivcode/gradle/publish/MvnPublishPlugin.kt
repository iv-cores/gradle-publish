package org.ivcode.gradle.publish

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.PasswordCredentials
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
        publishExtension.finalize(project)

        setupMavenPublish(project, publishExtension)
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

        // ensure the url is set before publishing
        tasks.getByName("publish").doFirst {
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
        if(!shouldCreatePublication(project, publishExtension)) {
            return
        }

        publications.create("maven", MavenPublication::class.java) {
            groupId = publishExtension.groupId
            artifactId = publishExtension.artifactId
            version = publishExtension.version

            from(project.components.getByName("java"))
        }
    }

    /**
     * Determines if a publication should be created. If the project is using the java-gradle-plugin, then a publication
     * should not be created.
     *
     * @param project The Gradle project to configure.
     * @param publishExtension The publish extension to use for configuration.
     * @return True if a publication should be created, false otherwise.
     */
    private fun shouldCreatePublication(project: Project, publishExtension: PublishExtension): Boolean {
        return if (project.plugins.hasPlugin("java-gradle-plugin")) {
            require(publishExtension.groupId == null && publishExtension.artifactId == null && publishExtension.version == null) {
                "Cannot specify groupId, artifactId, or version if using java-gradle-plugin"
            }
            false
        } else {
            true
        }
    }
}
