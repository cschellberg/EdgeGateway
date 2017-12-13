package com.eliga.thingworx.edgegateway;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    
    private final MessageType messageType;
    
    private final String destination;
    
    private final JSONObject payload;

    
    public Message(MessageType messageType, JSONObject payload) throws JSONException {
        this.messageType = messageType;
        this.destination = payload.getString(Constants.THING_NAME);
        this.payload = payload;
   }


    public MessageType getMessageType() {
        return messageType;
    }
    
    public String getDestination() {
        return destination;
    }
    
    

    public JSONObject getPayload() {
        return payload;
    }


    @Override
    public String toString() {
        return "Message [messageType=" + messageType + ", destination=" + destination + ", payload=" + payload + "]";
    }

    
    
       
}
