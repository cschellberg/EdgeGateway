FROM tomcat


RUN apt-get update && apt-get install -y \
     procps \
     vim \
     curl \
     tar \
     gzip \
     gnupg \    
     zip \
     && rm -rf /var/cache/apk/*

VOLUME /ThingworxPlatform
VOLUME /ThingworxStorage
VOLUME /ThingworxBackupStorage

EXPOSE 8080
EXPOSE 8443

ADD imageFiles/start-app  /opt/
ADD imageFiles/license.bin /ThingworxPlatform/
ADD imageFiles/Thingworx.war /usr/local/tomcat/webapps/
ADD imageFiles/setenv.sh /usr/local/tomcat/bin/

RUN chmod 777 /opt/start-app

#ENTRYPOINT ["/opt/start-app"]
CMD ["run"]
