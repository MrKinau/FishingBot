FROM maven:3.8.5-eclipse-temurin-16 as builder
WORKDIR /srv/build
COPY . .
RUN mvn package

FROM eclipse-temurin:21-jre
COPY --from=builder /srv/build/target/FishingBot-*.jar /usr/lib/fishingbot/FishingBot.jar
COPY ./docker/docker-entrypoint.sh /
COPY docker/fishing-bot /usr/bin/fishing-bot
RUN apt-get update && apt-get install bash jq -y
ENV MC_SERVER=127.0.0.1
ENV MC_PORT=25565
ENV MC_PROTOCOL=AUTOMATIC
ENV MC_ONLINE_MODE=true
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["fishing-bot"]
