package com.eliga.thingworx.edgegateway;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.thingworx.communications.client.ConnectionException;

public class MessageDistributor {

	private final LinkedBlockingDeque<Message> thingworxMessageQueue = new LinkedBlockingDeque<Message>();

	private final ThingworxChannelManager thingworxChannelManager = new ThingworxChannelManager(1);

	private final Map<String, BaseEdgeDevice> edgeDeviceMap = new HashMap<String, BaseEdgeDevice>();

	private boolean processThingworxMessages = true;

	public MessageDistributor() throws InterruptedException {
		// TODO make work threads configurarable
		ThingworxMessageProcessor thingworxMessageProcessor = new ThingworxMessageProcessor(10);
		Thread thingworxProcessingThread = new Thread(thingworxMessageProcessor);
		thingworxProcessingThread.setDaemon(true);
		thingworxProcessingThread.start();

	}

	public void distribute(Message message) throws InterruptedException {
		thingworxMessageQueue.put(message);
	}

	public ThingworxChannelManager getThingworxChannelManager() {
		return thingworxChannelManager;
	}

	public void stop() {
		processThingworxMessages = false;
	}

	private class ThingworxMessageProcessor implements Runnable {

		private final ExecutorService executor;

		public ThingworxMessageProcessor(int numberOfWorkerThreads) {
			executor = Executors.newFixedThreadPool(numberOfWorkerThreads);
		}

		@Override
		public void run() {
			try {
				while (processThingworxMessages) {
					try {
						Message thingworxMessage = thingworxMessageQueue.take();

						if (thingworxMessage != null) {
							Future<String> future = executor.submit(() -> {
								try {
									thingworxChannelManager.sendMessage(thingworxMessage);
									// TODO create dynamic result wrapper in
									// JSON format
									return Constants.SUCCESS;
								} catch (Exception e) {
									e.printStackTrace();
									return Constants.FAILURE;
								}
							});
							//TODO make timeout configurarable
							String result=future.get(100, TimeUnit.SECONDS);
							System.out.println("Result of platform operation "+result);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} finally {
				//TODO make shutdown more graceful
				executor.shutdown();
			}
			System.out.println("Finished processing thingworx messsages");

		}

	}

	public void init() {
		EdgeGatewaySettings edgeGatewaySettings = EdgeGatewaySettings.getInstance();
		thingworxChannelManager.initChannels(edgeGatewaySettings.getEdgeDevices());
		for (BaseEdgeDevice edgeDevice : edgeGatewaySettings.getEdgeDevices()) {
			this.edgeDeviceMap.put(edgeDevice.getThingName(), edgeDevice);
		}
	}

	public Map<String, BaseEdgeDevice> getEdgeDeviceMap() {
		return edgeDeviceMap;
	}

}
