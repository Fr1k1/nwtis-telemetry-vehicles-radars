FROM amd64/eclipse-temurin:21

RUN apt-get update && \
    apt-get install -y wget unzip

WORKDIR /app

COPY . /app

COPY docker-entrypoint.app.sh /docker-entrypoint.sh
RUN chmod -R 777 /docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]
