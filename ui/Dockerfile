FROM ubuntu

RUN echo "deb http://archive.ubuntu.com/ubuntu/ precise main universe" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install nginx -y

EXPOSE 80

ADD ./www /data/www
ADD ./nginx.conf /etc/nginx/nginx.conf

CMD nginx
