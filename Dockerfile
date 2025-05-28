FROM amazoncorretto:11-alpine-jdk

COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS=""
ENV SPRING_PROPS=""
ENV PROFILES_ACTIVE=docker
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS $SPRING_PROPS -jar app.jar"]