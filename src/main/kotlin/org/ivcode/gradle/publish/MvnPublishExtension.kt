package org.ivcode.gradle.publish

import org.gradle.api.Project
import org.ivcode.gradle.publish.utils.trimToNull
import java.lang.System.getenv

// Environment variables
private const val ENV_MVN_URL = "MVN_URL"
private const val ENV_MVN_USERNAME = "MVN_USERNAME"
private const val ENV_MVN_PASSWORD = "MVN_PASSWORD"
private const val ENV_MVN_IS_ALLOW_INSECURE = "MVN_IS_ALLOW_INSECURE_PROTOCOL"

/**
 * The extension for the publish plugin
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

    /** Whether to allow insecure connections */
    var isAllowInsecureProtocol: Boolean? = null
}

/**
 * A helper function to finalize the configuration of the publish extension
 *
 * Environment variables take precedence over the extension properties.
 * Extension properties take precedence over default values
 */
internal fun PublishExtension.finalize(project: Project) = PublishExtension().apply {
    url = getenv(ENV_MVN_URL).trimToNull()
        ?: this@finalize.url.trimToNull()

    username = getenv(ENV_MVN_USERNAME).trimToNull()
        ?: this@finalize.username.trimToNull()

    password = getenv(ENV_MVN_PASSWORD).trimToNull()
        ?: this@finalize.password.trimToNull()

    groupId = this@finalize.groupId.trimToNull()
        ?: project.group.toString().trimToNull()

    artifactId = this@finalize.artifactId.trimToNull()
        ?: project.name.trimToNull()

    version = this@finalize.version.trimToNull()
        ?: project.version.toString().trimToNull()

    isAllowInsecureProtocol = getenv(ENV_MVN_IS_ALLOW_INSECURE).trimToNull()?.toBoolean()
        ?: this@finalize.isAllowInsecureProtocol
        ?: false
}