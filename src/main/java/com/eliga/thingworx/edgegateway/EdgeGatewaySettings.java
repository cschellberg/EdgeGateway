
package com.eliga.thingworx.edgegateway;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class EdgeGatewaySettings {


    private static EdgeGatewaySettings edgeGatewaySettings;
    
    private String thingworxUrl;
    
    private String thingworxAppKey;
    
    private String gatewayIP;
    
    private final List<EdgeDevice> edgeDevices=new ArrayList<EdgeDevice>();
    
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
            JSONObject jsonObject =
                new JSONObject(IOUtils.toString(EdgeGatewaySettings.class.getResourceAsStream("/application_settings.json"), "UTF-8"));
            thingworxUrl = jsonObject.getString(Constants.THINGWORX_URL);
            thingworxAppKey = jsonObject.getString(Constants.THINGWORX_APP_KEY);
            gatewayIP = jsonObject.getString(Constants.GATEWAY_IP);
            gatewayPort = jsonObject.getInt(Constants.GATEWAY_PORT);
            JSONArray jsonArray = jsonObject.getJSONArray(Constants.THINGS);
            for (int ii = 0; ii < jsonArray.length(); ii++) {
                JSONObject jsonThingObject = jsonArray.getJSONObject(ii);
                edgeDevices.add(new EdgeDevice(gatewayIP, gatewayPort, jsonThingObject.getString(Constants.THING_NAME),
                                               jsonThingObject.getString(Constants.EDGE_DEVICE_IP),
                                               jsonThingObject.getInt(Constants.EDGE_DEVICE_PORT)));
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

    
    
    public List<EdgeDevice> getEdgeDevices() {
        return edgeDevices;
    }

    @Override
    public String toString() {
        return "EdgeGatewaySettings [thingworxUrl=" + thingworxUrl + ", thingworxAppKey=" + thingworxAppKey + ", gatewayIP=" + gatewayIP +
                        ", edgeDevices=" + edgeDevices + ", gatewayPort=" + gatewayPort + "]";
    }


}
