package com.eliga.thingworx.edgegateway;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;


public class MessageDistributor {

    private final LinkedBlockingDeque<Message> thingworxMessageQueue =  new  LinkedBlockingDeque<Message>();
    
    
    private final ThingworxChannelManager thingworxChannelManager = new ThingworxChannelManager(1);
    
    private final Map<String,BaseEdgeDevice>  edgeDeviceMap= new HashMap<String,BaseEdgeDevice>();
    
    private boolean processThingworxMessages=true;
    
    public MessageDistributor() throws InterruptedException{
        ThingworxMessageProcessor thingworxMessageProcessor =new ThingworxMessageProcessor();
        Thread thingworxProcessingThread =new Thread(thingworxMessageProcessor);
        thingworxProcessingThread.setDaemon(true);
        thingworxProcessingThread.start();
        
    }
    
    public void distribute(Message message) throws InterruptedException{
        thingworxMessageQueue.put(message);
    }
    
    
    
    public ThingworxChannelManager getThingworxChannelManager() {
		return thingworxChannelManager;
	}

	public void stop(){
        processThingworxMessages=false;
    }
    
 
        
    private class ThingworxMessageProcessor implements Runnable{
    
        @Override
        public void run() {
            while(processThingworxMessages){
                try
                {
                Message thingworxMessage=thingworxMessageQueue.take();
                
                if ( thingworxMessage != null){
                 thingworxChannelManager.sendMessage(thingworxMessage);
                }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            System.out.println("Finished processing thingworx messsages");
            
        }
        
    }
    
  
    public void init() {
        EdgeGatewaySettings edgeGatewaySettings = EdgeGatewaySettings.getInstance();
        thingworxChannelManager.initChannels(edgeGatewaySettings.getEdgeDevices());
        for (BaseEdgeDevice edgeDevice:edgeGatewaySettings.getEdgeDevices()){
        	this.edgeDeviceMap.put(edgeDevice.getThingName(), edgeDevice);
        }   
    }

	public Map<String, BaseEdgeDevice> getEdgeDeviceMap() {
		return edgeDeviceMap;
	}

}
