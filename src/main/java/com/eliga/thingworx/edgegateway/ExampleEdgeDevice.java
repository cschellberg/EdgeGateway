package com.eliga.thingworx.edgegateway;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thingworx.common.processors.ReflectionProcessor;
import com.thingworx.metadata.ServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.types.InfoTable;

@SuppressWarnings("serial")
public class ExampleEdgeDevice extends BaseEdgeDevice {

	public ExampleEdgeDevice(String gatewayIP, int gatewayPort, String thingName, String edgeDeviceIP,
			int edgeDevicePort) throws Exception {
		super(gatewayIP, gatewayPort, thingName, edgeDeviceIP, edgeDevicePort);
	}
	
	@ThingworxServiceDefinition(name = "BogusTestService")
	@ThingworxServiceResult(baseType = "INFOTABLE", name = "stringParam")
	public InfoTable BogusTestService(@ThingworxServiceParameter(baseType = "STRING", name = "stringParam")String stringParam) {
		System.out.println("Handling this service");
		try
		{
		JSONObject payload=new JSONObject();
		payload.put(Constants.ACTION, Constants.INVOKE_EDGE);
		payload.put(Constants.THING_NAME, this.getName());
		payload.put(Constants.SERVICE, "BogusTestService");
		JSONArray args=new JSONArray();
		JSONObject jsonArg=new JSONObject();
		jsonArg.put(Constants.ARG_NAME, "stringParam");
		jsonArg.put(Constants.ARG_VALUE, stringParam);
		args.put(jsonArg);
		payload.put(Constants.ARGS, args);
		Message message = new Message(payload);
		this.edgeMessageQueue.add(message);
		notifyDevice();
		}catch(Exception ex){
			ex.printStackTrace();
		}
        InfoTable infoTable=new InfoTable();
        return infoTable;
	}


}
