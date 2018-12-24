# Simple java sprint web application + CI/CD using jenkins, maven and docker

## Application description:
This web application based on java and spring is a very simple web page which shows very basic host details such as:
* Hostname
* Operating system
* CPU% usage
* Memory status
* etc  
Scroll down to the bottom for screenshot.

## Project description:
1. Setup a CI flow for the application
2. Build and deploy the project on a docker container

## Prerequisites
----
### Prerequisites - host-status application (codewise):  
1. Project is based on Java (1.8) and including tests  
2. Code should be pushed to Github (public repo)

### Prerequisites - servers
Launch 2 servers in AWS console:
1. Jenkins server - Redhat/ubuntu server
2. Application build server - RedHat server
3. optional: Application deployment server (there you'll need only docker, but you will have to modify the Jenkinsfile to copy the docker image to this server and run container over there).

## Prerequisites in app server:
### Server general configuration -
1. AWS security group - expose ssh access from Jenkins server, and from any other you wish (My ip/ all / other)

### Application build - 
1. Install jdk 1.8, maven (3.3.9 is good), git, docker 

### Github -  
1. Add <jenkins url>/github-webhook/ to Github Webhooks.
2. In Jenkins ui - install plugins: github-plugin, git plugin (should come already with the jenkins by default)
3. AWS security group - expose TCP 8080 access (or your Jenkins app port) from Github ip

## Prerequisites in Jenkins server:  
### Server general configuration -   
1. AWS security group - expose ssh access from whichever your choice is (My ip / all / other)
2. AWS security group - expose TCP 8080 access (or your Jenkins app port) from whichever your choice is (My ip / all / other)

### Jenkins - 
1. In the Jenkins unix account, Generate ssh key
2. Copy public key and add to authorized_keys in the build server.
3. In Jenkins ui - create ssh credentials to the build account using key above.
4. In Jenkins ui - create a node called 'host_ci' (or which ever you wish, just note that you'll have to modify the Jenkinsfile), connecting via ssh with key (use credentials above).
5. In the node configuration - check "Environment variables" and add variable named 'M2_HOME' and value maven dir on build server. (only for build server)
6. if you do have deployment server, repet steps 2-4 to do the same for the deploy server.


----
# CI/CD -
Creation of CI/CD job -  
* Create a new pipeline job in Jenkins. job configuration:   
  - Pipeline definition - Pipeline script from SCM
  - SCM - git
  - Repository URL - the link to current repository.
  - Credentials - none.
  - After saving configuration, run job manually once.
    ## CI:  
      - Job is being triggered on any push event on git repository.
      - git pull   
      - Build the project and run all the tests.
      - Build the docker image which contains the spring application.
    ## CD:  
      - Runs a docker container using the image from above.

# Final output:
You can access the application via web browser, going to the deployment IP address:8085  
(unless you changed the port in the Dockerfile or in the docker run command in the Jenkinsfile).  
Screenshot below:  
![Screenshot](https://github.com/mr-anderson86/spring-host-status/blob/master/screenshot.PNG)


### Ehe end :) ENJOY...
