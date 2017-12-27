import cherrypy
import threading
import time
import requests
import datetime
import json

class MessagePuller(object):
    def pullMessages(self):
        data='{"thingName":"TestRemoteThing","action":"pullMessages"}'
        response = requests.post("http://localhost:8080/EdgeGateway/", data=data, headers = service_headers)
        if response.status_code == 200:
            jsonRequest = response.json()
            print("Processing Messages ")
            for jsonMessage in jsonRequest:
                action = jsonMessage.get("action")
                if action == "edgePush":
                    print(action+" "+jsonMessage.get('thingName')+":"+jsonMessage.get('property')+"="+jsonMessage.get('value'))
                elif action == "invokeEdge":
                    print("invoking"+str(jsonMessage))
                else:
                    print("Can't get messages because "+response.status_code)



class MessageListener(object):
    
    messagePuller = None
    def __init__(self, messagePuller):
        self.messagePuller=messagePuller
    @cherrypy.expose
    @cherrypy.tools.allow(methods=['POST'])
    def index(self):
        print(cherrypy.request.method)
        body= cherrypy.request.body.read()
        jsonRequest = json.loads(body)
        action = jsonRequest.get("action")
        if action == "stopPush":
            runPush=False
        elif action == "startPush":
            runPush = True
        elif action == "messageAvailable":
            print("message available")
            self.messagePuller.pullMessages()
        print(action)
        return body


class PropertyPusher(threading.Thread):
    def __init__(self, count):
        threading.Thread.__init__(self)

    def run(self):
        service_headers = {'Accept' : 'application/json', 'Content-Type' : 'application/json'}
        counter = 1
        while runPush:
            try:
                value="value---"+str(counter)    
                counter += 1
                data='{"thingName":"TestRemoteThing","action":"push","property":"p1","value":"'+value+'"}'
                response = requests.post("http://localhost:8080/EdgeGateway/", data=data, headers = service_headers)
                if response.status_code == 200:
                    print("Update successful")
                else:
                    print("Update failed because "+response.status_code)
            except:
#                print(datetime.now()+" No connection")
                print("No connection")
            time.sleep(sleepTime)
        done

sleepTime=10
service_headers = {'Accept' : 'application/json', 'Content-Type' : 'application/json'}
runPush=True        
propertyPusher = PropertyPusher(7)
propertyPusher.daemon=True
propertyPusher.start()

cherrypy.config.update({'server.socket_port': 8083})
cherrypy.quickstart(MessageListener(MessagePuller()),'/test')
