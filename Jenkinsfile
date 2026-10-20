pipeline {
    agent any
    tools {
        jdk 'JDK21'
        maven 'M2_HOME'
    }
    stages {
        stage('Checkout code') {
            steps {
                git branch: 'main', url: 'https://github.com/RihabFattani/country-service.git'
            }
        }
        stage('Build maven') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Build Dockerfile') {
            steps {
                sh 'docker build -t my-country-service:$BUILD_NUMBER .'
                withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerhubpwd')]) {
                    sh 'docker login -u TON_USER_DOCKERHUB -p ${dockerhubpwd}'
                }
                sh 'docker tag my-country-service:$BUILD_NUMBER TON_USER_DOCKERHUB/my-country-service:$BUILD_NUMBER'
                sh 'docker push TON_USER_DOCKERHUB/my-country-service:$BUILD_NUMBER'
            }
        }
        stage('Deploy micro-service') {
            steps {
                sh 'docker rm -f country-service || true'
                sh 'docker run -d --name country-service -p 8081:8081 my-country-service:$BUILD_NUMBER'
            }
        }
    }
}
