FROM openjdk:17-oracle
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} Bank_REST.jar
ENTRYPOINT ["java","-jar","/Bank_REST.jar"]