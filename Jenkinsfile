properties([
   pipelineTriggers([
      [$class: "GitHubPushTrigger"]
   ]),
   disableConcurrentBuilds()
])

node('host_ci') {
   //def mvnHome = env.M2_HOME
   stage('Pull changes') { // for display purposes
      // Get some code from a GitHub repository
      git poll: true, url: 'https://github.com/mr-anderson86/spring-host-status.git'
   }
   stage('Build') {
      // Run the maven - compile, test, and pack to rpm
      sh "mvn clean install"
   }
   stage('Tests results') {
      junit '**/target/surefire-reports/TEST-*.xml'
      archive 'target/*.jar'
   }
   stage('Docker image') {
      // create the new docker image
      sh "sudo docker build -t host-status ."
   }
   stage('Docker run') {
      //Deploying the docker image, meaning: runnig the host-status web application
      def ps_list = sh(script: 'sudo docker ps -a -f name=host-status-web -q | wc -l', returnStdout: true).trim().toInteger()
      if ( ps_list > 0 ) {
          sh "sudo docker rm -f `docker ps -a -f name=host-status-web -q`"
      }
      sh "sudo docker run -d --name host-status-web -p 8085:8085 host-status"
      def image_list = sh(script: 'docker images -f dangling=true -q | wc -l', returnStdout: true).trim().toInteger()
      if ( image_list > 0 )  {
          sh "sudo docker rmi `docker images -f dangling=true -q`"
      }
      echo "[INFO] Done! Go via web browrser to your host at port 8085."
   }
}
