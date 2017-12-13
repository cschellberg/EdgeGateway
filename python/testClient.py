import time
import requests


print("Beginning application")

if __name__ == "__main__":
    service_headers = {'Accept' : 'application/json', 'Content-Type' : 'application/json'}
    for ii in range(0,10000):
        value="value---"+str(ii)    
        data='{"thingName":"TestRemoteThing","property":"p1","value":"'+value+'"}'
        response = requests.post("http://localhost:8080/EdgeGateway/", data=data, headers = service_headers)
        if response.status_code == 200:
            print("Update successful")
        else:
            print("Update failed because "+response.status_code)
        time.sleep(2)