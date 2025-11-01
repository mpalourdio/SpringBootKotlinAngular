FROM eclipse-temurin:25-alpine
RUN apk update && apk upgrade && apk add bash
RUN adduser -D -s /bin/bash user
WORKDIR /home/user
COPY target/springbootkotlinangular.jar app.jar
RUN chown user:user app.jar
USER user
ENTRYPOINT ["java", "-jar", "app.jar"]
