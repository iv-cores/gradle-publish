import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

node {
    checkout scm
    def isPrimaryBranch = "${env.BRANCH_IS_PRIMARY}".equalsIgnoreCase("true")

    properties([
        parameters([
            booleanParam(name: 'publish', defaultValue: isPrimaryBranch, description: 'Publish to Maven'),
        ])
    ])
    def isPublishToMaven = params.publish ?: isPrimaryBranch

    docker.image("registry.ivcode.org/corretto-ubuntu:21-jammy").inside {
        stage("build") {
            sh './gradlew clean build'
        }

        stage("publish") {
            if(!isPublishToMaven) {
                Utils.markStageSkippedForConditional(STAGE_NAME)
                return
            }

            withEnv(["MVN_URL=${MVN_URI_SNAPSHOT}"]) {
                withCredentials([usernamePassword(credentialsId: 'mvn-snapshot', usernameVariable: 'MVN_USERNAME', passwordVariable: 'MVN_PASSWORD')]) {
                    sh './gradlew publish'
                }
            }
        }
    }
}
