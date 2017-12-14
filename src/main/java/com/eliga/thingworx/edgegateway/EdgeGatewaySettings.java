
package com.eliga.thingworx.edgegateway;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class EdgeGatewaySettings {

	private static EdgeGatewaySettings edgeGatewaySettings;

	private String thingworxUrl;

	private String thingworxAppKey;

	private String gatewayIP;

	private final List<BaseEdgeDevice> edgeDevices = new ArrayList<BaseEdgeDevice>();

	private int gatewayPort;

	public synchronized static EdgeGatewaySettings getInstance() {
		if (edgeGatewaySettings == null) {
			edgeGatewaySettings = new EdgeGatewaySettings();
			edgeGatewaySettings.init();
		}
		return edgeGatewaySettings;
	}

	private void init() {
		try {
			Map<String, Constructor> constructorMap = new HashMap<String, Constructor>();
			JSONObject jsonObject = new JSONObject(IOUtils
					.toString(EdgeGatewaySettings.class.getResourceAsStream("/application_settings.json"), "UTF-8"));
			thingworxUrl = jsonObject.getString(Constants.THINGWORX_URL);
			thingworxAppKey = jsonObject.getString(Constants.THINGWORX_APP_KEY);
			gatewayIP = jsonObject.getString(Constants.GATEWAY_IP);
			gatewayPort = jsonObject.getInt(Constants.GATEWAY_PORT);
			JSONArray jsonArray = jsonObject.getJSONArray(Constants.THINGS);
			for (int ii = 0; ii < jsonArray.length(); ii++) {
				JSONObject jsonThingObject = jsonArray.getJSONObject(ii);
				String handler = jsonThingObject.getString("handler");
				Constructor<BaseEdgeDevice> constructor = constructorMap.computeIfAbsent(handler, handlerClass -> {
					Constructor<BaseEdgeDevice> tmpConstructor = null;
				    Class[] type = { String.class,Integer.TYPE,String.class,String.class, Integer.TYPE };
					try{
					Class classDefinition = Class.forName(handler);
					tmpConstructor = classDefinition.getConstructor(type);
					}catch(Exception ex){
						ex.printStackTrace();
					}
					return tmpConstructor;
				});
				Object[] args={gatewayIP, gatewayPort, jsonThingObject.getString(Constants.THING_NAME),
						jsonThingObject.getString(Constants.EDGE_DEVICE_IP),
						jsonThingObject.getInt(Constants.EDGE_DEVICE_PORT)};
				BaseEdgeDevice baseEdgeDevice = constructor.newInstance(args);
				edgeDevices.add(baseEdgeDevice);
			}
		} catch (Exception ex) {
			edgeGatewaySettings = null;
			System.out.println("Unable to initialize gateway settings because " + ex);
		}

	}

	public String getThingworxUrl() {
		return thingworxUrl;
	}

	public String getThingworxAppKey() {
		return thingworxAppKey;
	}

	public static EdgeGatewaySettings getEdgeGatewaySettings() {
		return edgeGatewaySettings;
	}

	public String getGatewayIP() {
		return gatewayIP;
	}

	public int getGatewayPort() {
		return gatewayPort;
	}

	public List<BaseEdgeDevice> getEdgeDevices() {
		return edgeDevices;
	}

	@Override
	public String toString() {
		return "EdgeGatewaySettings [thingworxUrl=" + thingworxUrl + ", thingworxAppKey=" + thingworxAppKey
				+ ", gatewayIP=" + gatewayIP + ", edgeDevices=" + edgeDevices + ", gatewayPort=" + gatewayPort + "]";
	}

}
