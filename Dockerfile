# This image provides runtime environment for OrVisual API service

FROM openjdk:10.0.2-13

COPY build/libs/orvisual-api-*.jar /srv/

CMD "$JAVA_HOME/bin/java" --add-modules java.xml.bind \
    -Dspring.datasource.url=$DB_URL \
    -Dspring.datasource.username=$DB_USER \
    -Dspring.datasource.password=$DB_PASSWD \
    -Dspring.jpa.properties.hibernate.default_schema=$DB_SCHEMA \
    -jar `ls -1 /srv/orvisual-api-*.jar`




