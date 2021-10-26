pipeline {
    agent {
            docker {
              image 'prefec2/jdk11-maven-363-gradle671'
              args env.DOCKER_ARGS
            }
    }

    environment {
        GRADLE_USER_HOME= env.WORKSPACE + '/.gradle'
    }

    stages {
        stage('Build') {
            steps {
                sh 'mkdir $GRADLE_USER_HOME ; ./gradlew assemble'
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
