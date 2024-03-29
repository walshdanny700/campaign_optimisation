# README

[![Gradle Build](https://github.com/walshdanny700/campaign_optimisation/actions/workflows/ci.yml/badge.svg)](https://github.com/walshdanny700/campaign_optimisation/actions/workflows/ci.yml) [![Coverage](.github/badges/jacoco.svg)](https://github.com/walshdanny700/campaign_optimisation/actions/workflows/ci.yml)

## Installation Requirements

Java Version 11: [Amazon Corretto 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/generic-linux-install.html)

Java Version 17:

### Steps To Run Application

#### Gradle
For Linux OS You may need to run in project root directory

    chmod 755 ./gradlew

Inside Terminal Window of your IDE run the following commands in project root directory

    # for Linux ./gradlew
    1. gradlew build 

    2. ./gradlew bootRun

### View Api Documentation

    http://localhost:8080/docs/index.html

    # view from docker image
    https://localhost:8443/docs/index.html

### Build Docker Image

    # Build Image With Gradle
    1. sudo ./gradlew bootBuildImage --imageName=walshdanny700/campaign_optimisation

    2. sudo docker push walshdanny700/campaign_optimisation

    2. sudo docker run -p 8443:8443 walshdanny700/campaign_optimisation

# Docker 

DockerHub: [DockerHub](https://hub.docker.com/repository/docker/walshdanny700/campaign_optimisation)

