FROM hseeberger/scala-sbt:11.0.3_1.2.8_2.13.0 as builder
WORKDIR /build
COPY project project
COPY build.sbt .
RUN sbt update
COPY . .
RUN sbt compile
RUN sbt -J-Xss4M stage

FROM openjdk:11.0.3-jdk-slim-stretch
WORKDIR /app
COPY --from=builder /build/target/universal/stage/. .
RUN mv bin/$(ls bin | grep -v .bat) bin/start
CMD ["./bin/start"]
