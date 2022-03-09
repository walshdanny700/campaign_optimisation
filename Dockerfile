FROM openjdk:17-alpine
COPY build/libs/campaign_optimisation-0.0.1-SNAPSHOT.war app.war
ENTRYPOINT ["java","-jar","app.war"]

