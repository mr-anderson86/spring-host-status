properties([
   pipelineTriggers([
      [$class: "GitHubPushTrigger"]
   ]),
   disableConcurrentBuilds()
])

node('builder_node') {
   //def mvnHome = env.M2_HOME
   stage('Pull changes') { // for display purposes
      // Get some code from a GitHub repository
      git poll: true, url: 'ssh://git@bitbucket:7999/bssproj/spring-example.git'
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
      ansiblePlaybook( 
         playbook: 'ansible/deployment.yml',
         inventory: 'ansible/hosts.ini')
      echo "[INFO] Done! Go via web browrser to your host at port 8085."
   }
}
