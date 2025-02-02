#!/bin/sh

docker run -p 9092:9092 -d --name kafka-kraft bashj79/kafka-kraft

docker exec -it kafka-kraft /bin/bash -c "
    KAFKA_BIN='/opt/kafka/bin'
    BOOTSTRAP_SERVER='localhost:9092'

    sh \$KAFKA_BIN/kafka-topics.sh --bootstrap-server \$BOOTSTRAP_SERVER --create --topic my-first-topic --partitions 1 --replication-factor 1
    sh \$KAFKA_BIN/kafka-topics.sh --bootstrap-server \$BOOTSTRAP_SERVER --list
    echo -e 'Hello World\nThe weather is fine\nI love Kafka' | sh \$KAFKA_BIN/kafka-console-producer.sh --bootstrap-server \$BOOTSTRAP_SERVER --topic my-first-topic
"

docker run -it -p 8080:8080 -e DYNAMIC_CONFIG_ENABLED=true --name kafka-ui provectuslabs/kafka-ui

docker exec -it --user=root kafka-ui /bin/sh -c "
    apk add --no-cache socat
    socat tcp-listen:9092,fork tcp:host.docker.internal:9092
"
