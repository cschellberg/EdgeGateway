
package com.eliga.thingworx.edgegateway;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.ConnectionException;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.ServiceDefinition;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
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
			setProperties(message);
		} else {
			System.out.println(action + " not found");
		}
	}

	private void setProperties(Message message) {
		String thingName = message.getDestination();
		try {
			BaseEdgeDevice edgeDevice = things.get(thingName);
			JSONArray jsonProperties = message.getPayload().getJSONArray(Constants.PROPERTIES);
			for (int ii = 0; ii < jsonProperties.length(); ii++) {
				JSONObject jsonProperty = jsonProperties.getJSONObject(ii);
				String propertyName = jsonProperty.getString(Constants.PROPERTY);
				String value = jsonProperty.getString(Constants.VALUE);
				Property property = edgeDevice.getProperty(propertyName);
				BaseTypes baseType = property.getPropertyDefinition().getBaseType();
				IPrimitiveType primitiveType = BaseTypes.ConvertToPrimitive(value, baseType);
				System.out.println("Setting property "+propertyName+" with "+primitiveType.getValue());
				edgeDevice.setPropertyValue(propertyName, primitiveType);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
