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
                    sh 'docker login -u rihabfatteni -p ${dockerhubpwd}'
                }
                sh 'docker tag my-country-service:$BUILD_NUMBER rihabfatteni/my-country-service:$BUILD_NUMBER'
                sh 'docker push rihabfatteni/my-country-service:$BUILD_NUMBER'
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                script{
             kubeconfig(credentialsId: 'kubeconfig-file', serverUrl: ''){
                sh 'kubectl apply -f deployment.yml'
                sh 'kubectl apply -f service.yml'
            }
        }
    }
  }
}
}
