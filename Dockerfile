FROM openjdk:17-alpine

WORKDIR /app

RUN apk add git maven wget

RUN git clone https://github.com/timerdar/FamilyFarm

RUN mvn clean install -f FamilyFarm

CMD ["java", "-jar", "/app/authService/target/authService-0.0.1-SNAPSHOT.jar"]