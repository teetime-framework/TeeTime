pipeline {
    agent {
            docker {
              image 'prefec2/jdk11-maven-363-gradle671'
              args env.DOCKER_ARGS
            }
    }


    stages {
        stage('Build') {
            steps {
                sh './gradlew assemble'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
	stage('Static Analysis') {
            steps {
                sh './gradlew build'
            }
            post {
                always {
                    recordIssues enabledForFailure: true, tools: [java(), javaDoc()]
                    recordIssues enabledForFailure: true, tool: checkStyle()
// recordIssues enabledForFailure: true, tool: spotBugs()
                    recordIssues enabledForFailure: true, tool: pmdParser()
                }
            }
        }
//        stage ('Deploy') {
//            steps {
//                sh 'mvn --batch-mode deploy -Psigning -Dcobertura.skip -U'
//            }
//        }
    }
}
