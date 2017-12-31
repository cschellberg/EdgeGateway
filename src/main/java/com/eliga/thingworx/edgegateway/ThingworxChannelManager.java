
package com.eliga.thingworx.edgegateway;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.ConnectionException;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.relationships.RelationshipTypes.ThingworxEntityTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;

public class ThingworxChannelManager {

    private final int numberOfConnections;

    private final ConcurrentHashMap<String, ThingworxChannel> channelMap = new ConcurrentHashMap<String, ThingworxChannel>();

    public ThingworxChannelManager(int numberOfConnections) {
        this.numberOfConnections = numberOfConnections;
    }

    public void initChannels(List<BaseEdgeDevice> edgeDevices)  {
        for (BaseEdgeDevice edgeDevice : edgeDevices) {
            try {
                initChannel(edgeDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initChannel(BaseEdgeDevice edgeDevice) throws Exception {
        ThingworxChannel thingworxChannel = channelMap.computeIfAbsent(edgeDevice.getThingName(), thingName -> {
            if (channelMap.size() < this.numberOfConnections) {
                ThingworxChannel tempChannel;
                try {
                    tempChannel = new ThingworxChannel();
                } catch (Exception ex) {
                    System.out.println("Could not create channel because " + ex);
                    return null;
                }
                return tempChannel;
            } else {
                Optional<ThingworxChannel> optionalThingworxChannel = channelMap.values().stream().min(new Comparator<ThingworxChannel>() {
                    @Override
                    public int compare(ThingworxChannel channel1, ThingworxChannel channel2) {
                        return channel1.size() - channel2.size();
                    }

                });
                return optionalThingworxChannel.get();
            }
        });
        ConnectedThingClient connectedThingClient = thingworxChannel.getConnectedThingClient();
        edgeDevice.setClient(connectedThingClient);
        try {
            edgeDevice.initializeServicesFromAnnotations();
            InfoTable infoTable = connectedThingClient.readProperties(ThingworxEntityTypes.Things, edgeDevice.getThingName(), 10000);
            for (FieldDefinition field : infoTable.getDataShape().getFields().values()) {
                edgeDevice.defineProperty(new PropertyDefinition(field.getName(), field.getDescription(), field.getBaseType()));
            }
            connectedThingClient.bindThing(edgeDevice);
            thingworxChannel.put(edgeDevice.getThingName(),edgeDevice);
        } catch (Exception e) {
            System.out.println(e);
        }
        edgeDevice.addPropertyChangeListener(edgeDevice);
        channelMap.put(edgeDevice.getThingName(), thingworxChannel);
    }

    public void sendMessage(Message message) throws TimeoutException, ConnectionException, Exception {
        ThingworxChannel thingworxChannel = channelMap.get(message.getDestination());
        if (thingworxChannel != null) {
            channelMap.put(message.getDestination(), thingworxChannel);
            thingworxChannel.send(message);
        } else {
            System.out.println("Could not send message because channel not initialized");
        }
    }

	public ConcurrentHashMap<String, ThingworxChannel> getChannelMap() {
		return channelMap;
	}
    
    

}
