package com.eliga.thingworx.edgegateway;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    
    
    private final String destination;
    
    private final JSONObject payload;

    
    public Message( JSONObject payload) throws JSONException {
        this.destination = payload.getString(Constants.THING_NAME);
        this.payload = payload;
   }

    
    public String getDestination() {
        return destination;
    }
    
    
    public JSONObject getPayload() {
        return payload;
    }


    @Override
    public String toString() {
        return "Message destination=" + destination + ", payload=" + payload + "]";
    }

    
    
       
}
