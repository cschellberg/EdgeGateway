
docker build -t dons_tomcat . 
docker run -i -p 8888:8080 -t dons_tomcat /bin/bash 
