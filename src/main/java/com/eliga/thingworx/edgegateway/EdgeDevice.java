
package com.eliga.thingworx.edgegateway;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import com.thingworx.common.processors.ReflectionProcessor;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.communications.client.things.VirtualThingPropertyChangeEvent;
import com.thingworx.communications.client.things.VirtualThingPropertyChangeListener;
import com.thingworx.metadata.ServiceDefinition;

public class EdgeDevice extends VirtualThing implements VirtualThingPropertyChangeListener {

    private final String gatewayIP;

    private final int gatewayPort;

    private final String thingName;

    private final String edgeDeviceIP;

    private final int edgeDevicePort;
    
    private final HttpClient httpClient;
    
    private final HttpClientContext edgeContext;
    
    private final LinkedBlockingDeque<Message> edgeMessageQueue =  new  LinkedBlockingDeque<Message>();

	private final URI edgeUri;



    public EdgeDevice(String gatewayIP, int gatewayPort, String thingName, String edgeDeviceIP, int edgeDevicePort) throws Exception {
        super();
        EdgeDeviceThing edgeDeviceThing=new EdgeDeviceThing();
        this.gatewayIP = gatewayIP;
        this.gatewayPort = gatewayPort;
        this.thingName = thingName;
        this.setName(thingName);
        this.setDescription(thingName+" edge device");
        this.edgeDeviceIP = edgeDeviceIP;
        this.edgeDevicePort = edgeDevicePort;
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setName("bogusTestService");
        edgeDeviceThing.defineService(serviceDefinition);
        ReflectionProcessor reflectionProcessor = ReflectionProcessor.getInstance(EdgeDeviceThing.class, serviceDefinition);
        edgeDeviceThing.getServiceProcessors().put("bogusTestService", reflectionProcessor);
        URIBuilder uriBuilder=new URIBuilder();
    	uriBuilder.setHost(this.getEdgeDeviceIP());
    	uriBuilder.setPort(this.getEdgeDevicePort());
    	uriBuilder.setScheme("http");
    	uriBuilder.setPath("/test/");
    	edgeUri = uriBuilder.build();
    	edgeContext = HttpClientContext.create();
        HttpClientBuilder httpClientBuilder= HttpClientBuilder.create();
        httpClient=httpClientBuilder.build();
    }

    public String getGatewayIP() {
        return gatewayIP;
    }

    public int getGatewayPort() {
        return gatewayPort;
    }

    public String getThingName() {
        return thingName;
    }

    public String getEdgeDeviceIP() {
        return edgeDeviceIP;
    }

    public int getEdgeDevicePort() {
        return edgeDevicePort;
    }

    public LinkedBlockingDeque<Message> getEdgeMessageQueue() {
		return edgeMessageQueue;
	}

	@Override
    public void propertyChangeEventReceived(VirtualThingPropertyChangeEvent propertyChangeEvent) {
       JSONObject payload=new JSONObject();
	try {
		payload.put(Constants.THING_NAME, this.getName());
		payload.put(Constants.PROPERTY, propertyChangeEvent.getProperty().getPropertyDefinition().getName());
		payload.put(Constants.VALUE, propertyChangeEvent.getPrimitiveValue().getStringValue());
		Message message=new Message(MessageType.EDGE,payload);
		this.edgeMessageQueue.add(message);
		notifyDevice();
	} catch (Exception e) {
		e.printStackTrace();
	}
    }

    private void notifyDevice() throws URISyntaxException, ClientProtocolException, IOException, JSONException {
 		HttpPost request=new HttpPost(edgeUri);
 		try
 		{
		JSONObject jsonObject=new JSONObject();
		jsonObject.put(Constants.ACTION, Constants.MESSAGE_AVAILABLE);
		HttpEntity entity=new StringEntity(jsonObject.toString());
		request.setEntity(entity);
		httpClient.execute(request,edgeContext);
 		}finally{
		request.releaseConnection();
 		}
		
	}

	private class EdgeDeviceThing extends VirtualThing{
    public void bogusTestService() {
        System.out.println("Handling this service");
    }
    }
}
