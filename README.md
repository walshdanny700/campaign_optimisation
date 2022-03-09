# Getting Started

### Installation Requirements

Java Version 11: [Amazon Corretto 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/generic-linux-install.html)

Java Version 17:

### Steps To Run Application

#### Gradle
For Linux OS You may need to run in project root directory

    chmod 755 ./gradlew

Inside Terminal Window of your IDE run the following commands in project root directory

    # for Linux ./gradlew
    1. gradlew build 

    2. gradlew bootRun

#### Docker

    # Build Image With Gradle
    1. sudo ./gradlew bootBuildImage --imageName=walshdanny700/campaign_optimisation

    2. sudo docker push walshdanny700/campaign_optimisation

    2. sudo docker run -p 8080:8080 walshdanny700/campaign_optimisation

### View Api Documentation

    http://localhost:8080/docs/index.html
