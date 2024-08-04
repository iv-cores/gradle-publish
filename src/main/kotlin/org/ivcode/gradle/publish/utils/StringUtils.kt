package org.ivcode.gradle.publish.utils

/**
 * A utility class for string manipulation
 */

fun String?.trimToNull(): String? {
    return this?.trim()?.takeIf { it.isNotEmpty() }
}