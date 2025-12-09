FROM tomcat:9.0-jdk17-openjdk

COPY target/laba6-manual.war /usr/local/tomcat/webapps/laba6-manual.war

ENV DB_HOST=postgres
ENV DB_PORT=5432
ENV DB_NAME=labs
ENV DB_USER=postgres
ENV DB_PASSWORD=12345

EXPOSE 8080

CMD ["catalina.sh", "run"]