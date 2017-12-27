
package com.eliga.thingworx.edgegateway;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.ConnectionException;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.IPrimitiveType;
import com.thingworx.types.primitives.StringPrimitive;
import com.thingworx.types.properties.Property;

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

	public void put(String thingName, BaseEdgeDevice edgeDevice) {
		things.put(thingName, edgeDevice);
	}

	public ConnectedThingClient getConnectedThingClient() {
		return connectedThingClient;
	}

	public void send(Message message) throws TimeoutException, ConnectionException, Exception {
		String action = message.getPayload().getString(Constants.ACTION);
		if (Constants.PUSH.equals(action)) {
			setProperty(message);
		} else if ( Constants.INVOKE_PLATFORM.equals(action)){
			invokePlatform(message);
		}else {
			System.out.println(action+" not found");
		}
	}

	private void invokePlatform(Message message) {
		String thingName = message.getDestination();
		try {
			BaseEdgeDevice edgeDevice = things.get(thingName);
			String serviceName=message.getPayload().getString(Constants.SERVICE);
			JSONArray args =message.getPayload().getJSONArray(Constants.ARGS);
			for (int ii =0;ii<args.length();ii++){
				
			}
			ValueCollection parameters=new ValueCollection();
			edgeDevice.invokeService(serviceName, parameters);
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}

	private void setProperty(Message message) {
		String thingName = message.getDestination();
		try {
			BaseEdgeDevice edgeDevice = things.get(thingName);
			String propertyName = message.getPayload().getString(Constants.PROPERTY);
			String value = message.getPayload().getString(Constants.VALUE);
			Property property = edgeDevice.getProperty(propertyName);
			BaseTypes baseType = property.getPropertyDefinition().getBaseType();
			IPrimitiveType primitiveType = BaseTypes.ConvertToPrimitive(value, baseType);
			edgeDevice.setPropertyValue(propertyName, primitiveType);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
