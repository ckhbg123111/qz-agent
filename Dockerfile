FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY qz-agent-api/pom.xml qz-agent-api/pom.xml
COPY qz-agent-biz/pom.xml qz-agent-biz/pom.xml
COPY qz-agent-web/pom.xml qz-agent-web/pom.xml
RUN mvn -q -e -DskipTests dependency:go-offline

COPY qz-agent-api/src qz-agent-api/src
COPY qz-agent-biz/src qz-agent-biz/src
COPY qz-agent-web/src qz-agent-web/src
RUN mvn -q -DskipTests package -pl qz-agent-web -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/qz-agent-web/target/*.jar /app/app.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
