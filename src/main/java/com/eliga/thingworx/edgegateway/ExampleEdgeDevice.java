package com.eliga.thingworx.edgegateway;

import com.thingworx.common.processors.ReflectionProcessor;
import com.thingworx.metadata.ServiceDefinition;

public class ExampleEdgeDevice extends BaseEdgeDevice {

	public ExampleEdgeDevice(String gatewayIP, int gatewayPort, String thingName, String edgeDeviceIP,
			int edgeDevicePort) throws Exception {
		super(gatewayIP, gatewayPort, thingName, edgeDeviceIP, edgeDevicePort);
		ServiceDefinition serviceDefinition = new ServiceDefinition();
		serviceDefinition.setName("bogusTestService");
		defineService(serviceDefinition);
		ReflectionProcessor reflectionProcessor = ReflectionProcessor.getInstance(ExampleEdgeDevice.class,
				serviceDefinition);
		getServiceProcessors().put("bogusTestService", reflectionProcessor);
	}
	
	public void bogusTestService() {
		System.out.println("Handling this service");
	}


}
