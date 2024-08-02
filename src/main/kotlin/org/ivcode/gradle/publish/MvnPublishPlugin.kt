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
     *
     */
    private fun setupAfterEvaluate(project: Project) = project.afterEvaluate {
        val publishExtension = project.extensions.getByName("publish") as PublishExtension
        publishExtension.setDefaults(project)

        setupMavenPublish(project, publishExtension)
    }

    /**
     * Sets up the maven publish plugin
     */
    private fun setupMavenPublish(project: Project, publishExtension: PublishExtension) = with(project) {
        plugins.apply("maven-publish")

        configure<PublishingExtension> {
            publications {
                create("maven", MavenPublication::class.java) {
                    groupId = publishExtension.groupId
                    artifactId = publishExtension.artifactId
                    version = publishExtension.version

                    from(project.components.getByName("java"))
                }
            }

            if (publishExtension.url != null) {

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
        }
    }
}
