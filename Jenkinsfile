pipeline {
    agent any
    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Build') {
            steps { bat 'mvn clean compile' }
        }
        stage('Test') {
            steps { bat 'mvn test' }
            post {
                always { junit '**/target/surefire-reports/*.xml' }
            }
        }
        stage('Package') {
            steps { bat 'mvn package -DskipTests' }
        }
    }
    post {
        success { echo 'Build and tests passed.' }
        failure { echo 'Build failed; check console output.' }
    }
}