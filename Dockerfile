FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/cinema.jar /cinema/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/cinema/app.jar"]
