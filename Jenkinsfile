node {
    checkout scm

    docker.image("registry.ivcode.org/corretto-ubuntu:21-jammy").inside {
        stage("build") {
            sh './gradlew clean build'
        }

        stage("publish") {
            sh './gradlew publish -x jar -x sourcesJar -x assemble -x build'
        }
    }
}