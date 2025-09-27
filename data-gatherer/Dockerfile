FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/data-gatherer-*.jar data-gatherer.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/data-gatherer.jar"]