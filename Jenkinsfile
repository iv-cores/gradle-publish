node {
    checkout scm

    docker.image("registry.ivcode.org/corretto-ubuntu:21-jammy").inside {
        stage("build") {
            sh './gradlew clean build'
        }

        stage("publish") {
            withEnv(["MVN_URL=${MVN_URI_SNAPSHOT}"]) {
                withCredentials([usernamePassword(credentialsId: 'mvn-snapshot', usernameVariable: 'MVN_USERNAME', passwordVariable: 'MVN_PASSWORD')]) {
                    sh './gradlew publish -x jar -x sourcesJar -x assemble -x build'
                }
            }
        }
    }
}
