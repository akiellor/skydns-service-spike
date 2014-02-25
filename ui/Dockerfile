FROM ubuntu

RUN echo "deb http://archive.ubuntu.com/ubuntu/ precise main universe" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install nginx uuid curl watch -y

EXPOSE 80

ADD ./www /data/www
ADD ./nginx.conf /etc/nginx/nginx.conf
ADD ./run /opt/run
ADD ./skydnsd /opt/skydnsd

CMD /opt/run
