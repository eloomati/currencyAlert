FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -u 1001 appuser
COPY target/data-provider-*.jar data-provider.jar
ENV JAVA_OPTS=""
USER appuser
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/data-provider.jar"]