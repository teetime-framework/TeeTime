pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn --batch-mode compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn --batch-mode test'
            }
        }
	stage('Static Analysis') {
            steps {
                sh 'mvn --batch-mode package checkstyle:checkstyle pmd:pmd -Dworkspace=' + env.WORKSPACE // spotbugs:spotbugs
            }
            post {
                always {
                    recordIssues enabledForFailure: true, tools: [mavenConsole(), java(), javaDoc()]
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
