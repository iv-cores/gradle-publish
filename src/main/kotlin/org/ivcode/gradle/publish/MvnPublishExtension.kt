package org.ivcode.gradle.publish

import org.gradle.api.Project
import java.lang.System.getenv

private const val ENV_MVN_URL = "MVN_URL"
private const val ENV_MVN_USERNAME = "MVN_USERNAME"
private const val ENV_MVN_PASSWORD = "MVN_PASSWORD"

/**
 * Publish
 */
open class PublishExtension {
    /** The url of the maven repository */
    var url: String? = null

    /** The username of the maven repository, if any */
    var username: String? = null

    /** The password of the maven repository, if any */
    var password: String? = null

    /** The group id of the maven artifact */
    var groupId: String? = null

    /** The artifact id of the maven artifact */
    var artifactId: String? = null

    /** The version of the maven artifact */
    var version: String? = null
}

/**
 * Sets the default values
 *
 * Environment variables take precedence over the extension properties.
 * Extension properties take precedence over default values
 */
internal fun PublishExtension.setDefaults(project: Project) {
    url         = getenv(ENV_MVN_URL) ?: url
    username    = getenv(ENV_MVN_USERNAME) ?: username
    password    = getenv(ENV_MVN_PASSWORD) ?: password
    groupId     = groupId ?: project.group.toString()
    artifactId  = artifactId ?: project.name
    version     = version ?: project.version.toString()
}