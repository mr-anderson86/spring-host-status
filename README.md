# Spring Host Status - a whole CI-CD process

## Description:

Please note: there's a "better" (newer) version of this project in my [GitLab repo](https://gitlab.com/mr-anderson86/spring-host-status).  
There it runs via GitLab CI, uploads the Maven artifact to the repo, and pushes the Docker Image as well, and deploys the container in a remove OpenShift cluster.  
  
This repository represents a Jenkins pipeline, which all runs on a whole CI-CD process.  
This web application based on java and spring.  
Originally, initiated using this website: https://start.spring.io/ it's a very good site, highly recommended.  
The final output is a docker container which runs this application, and the user can access your web application (via web browser) and see the new changes (without any action what so ever from his side).  

Meaning:
1. The pipeline first builds the Java application using Maven.
2. Then it builds an image out of the Dockerfile.
3. It runs a container using the image above.
4. Final output: end user "logs in" your application, and seeing your "new feature", without him needing to do any action on his side.

Screenshots of final results can be found in the [images directory](images) :-)  
  

The repository holds:
1. The Java Spring web application (all under src)
2. Dockerfile 
3. Ansible playbook - which runs the docker container   
(Just because I wanted it to be with ansible)
4. Jenkinsfile - scripted (Maybe also declerative in the future)

## Prerequisites:
1. Jenkins of course, with relevant node configured and running (Also needs ansible plugin installed).
2. The builder machine needs git, JDK 1.8, Maven (3.3.9 is good) Docker and Ansible - all installed.  
(Tested only on RedHat server, but it all might work also on ubuntu, CentOS and others)
3. Don't forget to open port 8085 on your security group :)  
(or whichever port you want to expose your app on, but don't forget to change in the code)
4. Add \<your jenkins url\>/github-webhook/ into your repository Webhooks.  
(Go to Settings -> Webhooks -> and add there your webhook).

### The Java Spring web application:
* Based on Java 1.8 (Uses Maven for the build stage)
* By default it listens on port 8085 (src/main/resources/application.properties)
* The main index page is written here: src/main/java/com/spring/app/hoststatus/resource/HostResources.java
* All it provides is a single page with some basic host status (Host name, OS, CPU% usage, Memory, etc...)

### The Dockerfile does as follows:
* Based on openjdk:8
* Adds the target/host-status.jar file to the home dir of the container
* Exposing port 8085
* Runs the command: java -jar host-status.jar (turns up the web application)

### The Ansible playbook does as follows:
* Stops and removes container (if there was existing one)
* Starts a new container based on the latest image which was built.
* Removes the old docker image

### The Jenkinsfile:
* It starts automatically on each git hub push event  
(but you do need to run the job manually for the first time, after that this configuration is set for good.)
* Agent is running on "builder_node"  
(if you fork this repo, you can always change the node name)
* Pull changes from the git repository
* Build stage: Builds the project and run all the tests, using Maven.
* Tests stage: runs junit, and archiving all jar files
* Docker image stage: builds the new docker image, which contains the the Java web app (jar file), which contains all your latest changes.
* Docker run stage: the "deploy" stage, it runs new container, using the new docker image from above (using the ansible playbook).

## Other Ideas:
* you can always install Nexus or Artifactory and deploy the jar files there.
* Then you can run the docker build stage on a different host (node), and pull the jar from Nexus/Artifactory.
* You can also docker push the image into your docker hub (or Nexus or others)
* And then, on a different host/node (the "deployment" host) you can docker pull that image and run container from it.
* This way it will represent a whole CI-CD process, which builds the project artifacts on 1 server, builds the docker image on another, and actually deploys the container on a different host.

### The end, enjoy :)

