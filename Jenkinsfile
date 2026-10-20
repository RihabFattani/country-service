pipeline {
    agent any
    tools {
        jdk 'JDK21'
        maven 'M2_HOME'
    }
    stages {
        stage('Checkout code') {
            steps {
                git branch: 'main', url: 'https://github.com/RihabFattani/Country-service.git'
            }
        }
        stage('Compile, test, package') {
            steps {
                sh 'mvn clean install'
            }
            post {
                success {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=country-service -Dsonar.projectName=country-service'
                }
            }
        }
        stage('Deploy jar to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-creds', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh 'mvn deploy -DskipTests -s settings.xml'
                }
            }
        }
        stage('Store reports in Nexus') {
            steps {
                sh '''
                    zip -r jacoco-report.zip target/site/jacoco
                    zip -r surefire-reports.zip target/surefire-reports
                '''
                withCredentials([usernamePassword(credentialsId: 'nexus-creds', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh '''
                        curl --fail -u "$NEXUS_USER:$NEXUS_PASS" --upload-file jacoco-report.zip \
                          "http://localhost:8081/repository/reports/country-service/$BUILD_NUMBER/jacoco-report.zip"
                        curl --fail -u "$NEXUS_USER:$NEXUS_PASS" --upload-file surefire-reports.zip \
                          "http://localhost:8081/repository/reports/country-service/$BUILD_NUMBER/surefire-reports.zip"
                    '''
                }
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
                sh 'docker run -d --name country-service -p 8082:8082 my-country-service:$BUILD_NUMBER'
            }
        }
    }
}