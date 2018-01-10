Advantages

Easy to setup
Not limited to HTML 5 protocol.  For example, an edge device can be implemented that connects to devices via Bluetooth
Client can be implemented in any language that supports http protocol and JSON.

Requirements
Java SDK 1.8
Docker
Gradle
Thingworx war file and license
Oracle VM Virtual Box
Python (2.8+)

Installation

To install a trial thingworx instance

Download trial edition here, https://developer.thingworx.com/resources/downloads/foundation-trial-edition

Install Oracle VM Virtual Box on your box
Install Docker on your box
Copy the ThingworxWar and license.bin(in the download zip) to the directory [Path to project]\EdgeGateway\docker\imageFiles
CD to the directory [Path to project]\EdgeGateway\docker
Create a virtual machine by executing the following command
docker-machine create --driver virtualbox thingworx
This will create a virtual box denominated thingworx

Start the machine by executing

docker-machine start thingworx
docker-machine env thingworx
Then execute the shell commands displayed by the docker-machine env command.  Take note of the IP address that is assigned to the container

Create a docker container by executing these commands

docker build -t dons_tomcat . 
docker run -i -p 8888:8080 -t dons_tomcat /bin/bash 

The second command will start up the container but not the tomcat server in the container.

To start the command go to the directory, /opt and execute the command, ./start-app

This will start the thingworx platform.  Thingworx will probably take a couple of minutes to start up.  To access the platform enter this address in a web browser (http://[your IP address from the env command]:8888/Thingworx)

Something like this(in windows)

@FOR /f "tokens=*" %i IN ('docker-machine env thingworx') DO @%i

Login credentials are Administrator/trUf6yuz2?_Gub

To Run

Import the test remote things into the platform(TestEntities.xml)

This will create the test things that the edge nano server will connect to.

Set your properties in the application_settings.json (app_key(created in the thingworx platform, be careful of the expiration date), url(if using Docker, the IP address of your container) and port(8888 if using Docker) of your thingworx instance)

Build the EdgeNanoServer with the gradle script.  After building the nanoserver in Eclipse by executing the eclipse command , "Run on Server"

The edge nano server should connect to the the thingworx platform.  This can be verified by check that the remote things are bound by clicking on the Monitoring tab and then selecting RemoteThings.  Both things TestRemoteThing and TestRemoteThing2 on the left side of the screen should be green.  If they are red the edge nano server is not being bound to the remote things.

Run the testClient.py script in the python directory by excuting command

python ./testClient.py
Remote value should change periodically based on this value in the testServer.py

sleepTime=30

In this case value will change every 30 seconds after the remote thing properties are bound.  To bind open the thing on the plaform, select Manager Property Bindings and drag p1 and p3 from the remote tab on the left.

There is one test service, BogusTestService.  If you execute this service on the platform, you will see the json payload echoed in the python script.

Overview of Edge Nano Server

The edge nano server will pull all property definitions from the platform so you must create the thing to echo the properties of the remote device.

The services are implemented on the EdgeNanoServer through annotations.  And example can be seen in the class, ExampleEdgeDevice.  This will encapsulate the service name and parameters into a JSON payload and send it to the python client.

I am also working on a test client in C++

Any questions can be referred to Donald Schellberg
dschellberg@gmail.com
I am also available for consulting