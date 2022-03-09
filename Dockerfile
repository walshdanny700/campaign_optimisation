FROM openjdk:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG WAR_FILE=build/libs/\*.war
COPY ${WAR_FILE} app.war
ENTRYPOINT ["java","-jar","app.war"]