properties([
   pipelineTriggers([
      [$class: "GitHubPushTrigger"]
   ]),
   disableConcurrentBuilds()
])

pipeline {
    agent { node { label 'sky-builder_node-ssl' } }
    //environment { mvnHome = env.M2_HOME }
    stages {
        stage('Build code') {
            steps {
                //Run the maven - compile, test, and pack to rpm
                sh "mvn clean install"
                junit '**/target/surefire-reports/TEST-*.xml'
                archive 'target/*.jar'
            }
        }
        stage('Build Docker image') {
            steps {
                sh "sudo docker build -t host-status ."
                //sh "docker login <registry> <credentials>"
                //sh "docker push host-status"
            }
        }
        stage('Deploy container') {
            steps {
                /*If your are using docker push to remote registry,
                  You might want to update ansible/deployment.yml with the image's remote registry address*/
                ansiblePlaybook('ansible/deployment.yml') {
                    inventoryPath('ansible/hosts.ini')
                }
                echo "[INFO] Done! Go via web browrser to your host at port 8085."
            }
        }
    }
}
