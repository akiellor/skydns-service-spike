FROM ubuntu

RUN apt-get update
RUN apt-get install openjdk-6-jre -y

EXPOSE 8080

ADD ./build/libs/health-unspecified-shadow.jar /opt/app.jar
ADD ./start.sh /opt/start.sh

CMD /opt/start.sh
