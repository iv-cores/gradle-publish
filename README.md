# gradle-publish
A gradle plugin for publishing artifacts to maven. This plugin wraps `maven-publish`, configuring it so that
environmental variables can be used to publish artifacts

## Apply

`build.gradle.kts`
```kotlin
plugins {
    id ("org.ivcode.gradle.publish") version "1.0"
}
```

## Supported Plugins
The plugin will identify the following plugins and configure the `maven-publish` plugin accordingly.
- `java`
- `java-library`
- `java-gradle-plugin`
- `org.springframework.boot`

## Configuration
The extension can be used to configure the plugin. Typically, you wouldn't specify the `url`, `username`, or `password`.
This could be configured using environment variables. And if the defaults are to be used for the `groupId`,
`artifactId`, and `version`, then you wouldn't need to define the extension at all.

### Extension

`build.gradle.kts`
```kotlin
publish {
    url      = "https://my-mvn.com" // Repo URL        | Required for non-local publishing
    username = "my-username"        // Repo Username   | Optional (default = none)
    password = "my-password"        // Repo Password   | Optional (default = none)
    isAllowInsecure = true          // Allow Insecure  | Optional (default = false)

    groupId    = "com.my-group-id"  // Mvn Group Id    | Optional (default = ${project.group})
    artifactId = "my-project-name"  // Mvn Artifact Id | Optional (default = ${project.name})
    version    = "my-version"       // Mvn Version     | Optional (default = ${project.version})
}
```

### Environment Variables
The repository can be defined using environmental variables. If defined, they overwrite whatever is defined in the
extension.

| Variable        | Description   | 
|-----------------|---------------|
| `MVN_URL`       | Repo Url      |
| `MVN_USERNAME`  | Repo Username |
| `MVN_PASSWORD`  | Repo Password |