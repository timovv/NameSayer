pipeline {
    agent any

    tools {
        maven 'maven-3.5.3'

    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true clean package'
            }

            post {
                success {
                    archiveArtifacts 'target/*.jar'
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
        }
    }
}