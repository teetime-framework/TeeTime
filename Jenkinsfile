#!/usr/bin/env groovy

pipeline {

  agent {
            docker {
              image 'prefec2/jdk11-maven-363-gradle671'
              args env.DOCKER_ARGS
            }
  }

  environment {
      GRADLE_USER_HOME= "${env.WORKSPACE}/.gradle"
  }

  stages {
    stage('Build') {
      steps {
        sh 'test -d $GRADLE_USER_HOME || mkdir $GRADLE_USER_HOME'
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
        sh './gradlew check'
      }
      post {
        always {
          recordIssues(
            enabledForFailure: true, tools: [
              java(), 
              javaDoc(),
              checkStyle(pattern: '**/build/reports/checkstyle/*.xml'),
              pmdParser(pattern: '**/build/reports/pmd/*.xml')
            ]
          )
        }
      }
    }
    stage ('Deploy') {
      when {
        beforeAgent true
        branch 'master'
      }
      steps {
        withCredentials([
          usernamePassword(
            credentialsId: 'artifactupload', 
            usernameVariable: 'teetimeMavenUser', 
            passwordVariable: 'teetimeMavenPassword'
          )
        ]) {
          sh './gradlew publish'
        }
      }
    }
  }
}
