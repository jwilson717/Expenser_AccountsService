pipeline {
    agent any

    stages {
    	stage('Build'){
    		steps {
    			sh "mvn clean package"
    		}
    	}
        stage('Sonar Eval') {
            steps {
                sh "/home/jwilson/.jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonar/bin/sonar-scanner -X -Dsonar.projectVersion=1.0 -Dsonar.projectKey=AccountsService -Dsonar.java.source=. -Dsonar.java.binaries=./target"
            }
        }
        stage('Docker image') {
        	steps {
        		sh "docker build . -t expenser-accountsservice-image"
        	}
        }
    }
}