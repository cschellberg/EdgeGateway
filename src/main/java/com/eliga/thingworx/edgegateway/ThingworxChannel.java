
package com.eliga.thingworx.edgegateway;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.ConnectionException;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.relationships.RelationshipTypes.ThingworxEntityTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.primitives.StringPrimitive;

public class ThingworxChannel {

    private ConcurrentHashMap<String, BaseEdgeDevice> things = new ConcurrentHashMap<String, BaseEdgeDevice>();

    private ConnectedThingClient connectedThingClient;

    public ThingworxChannel() throws Exception {
        ClientConfigurator config = new ClientConfigurator();
        config.setUri(EdgeGatewaySettings.getInstance().getThingworxUrl());
        config.setAppKey(EdgeGatewaySettings.getInstance().getThingworxAppKey());
        config.ignoreSSLErrors();
        connectedThingClient = new ConnectedThingClient(config);
        connectedThingClient.start();
        if (connectedThingClient.waitForConnection(30000)) {
            System.out.println("The client is now connected.");
        }
    }

    public int size() {
        return things.size();
    }
    
    public void put(String thingName, BaseEdgeDevice edgeDevice){
        things.put(thingName, edgeDevice);
    }

    public ConnectedThingClient getConnectedThingClient() {
        return connectedThingClient;
    }

    public void send(Message message) throws TimeoutException, ConnectionException, Exception {
        String thingName = message.getDestination();
        try {
            BaseEdgeDevice edgeDevice = things.get(thingName);
            String property = message.getPayload().getString(Constants.PROPERTY);
            String value = message.getPayload().getString(Constants.VALUE);
            edgeDevice.setPropertyValue(property, new StringPrimitive(value));
            System.out.println("The name of the Thing " + edgeDevice.getName());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
