FROM alpine/java:21-jdk
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
#docker  build -t  micro .
#docker run -p 8080:8080 micro
#docker login
#docker tag micro  allamchz/micro:latest
#docker push allamchz/micro:latest