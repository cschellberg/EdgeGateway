package com.eliga.thingworx.edgegateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class EdgeServlet extends HttpServlet{
    
    private MessageDistributor messageDistributor = null;

    @Override
    public void init() throws ServletException {
         super.init();
         try {
            messageDistributor = new MessageDistributor();
            messageDistributor.init();
        } catch (InterruptedException e) {
             e.printStackTrace();
        }

    }
    
    

    @Override
    public void destroy() {
        super.destroy();
    }



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("servlet get");
        response.getWriter().println("gotcha");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String payload=IOUtils.toString(request.getInputStream(),"UTF-8");
        JSONObject jsonPayload =null;
        try {
            jsonPayload=new JSONObject(payload);
            String action = jsonPayload.getString(Constants.ACTION);
            if ( Constants.PUSH.equals(action)){
            Message message=new Message(jsonPayload);
            messageDistributor.distribute(message);
            }else if ( Constants.PULL_MESSAGES.equals(action)){
            	String thingName=jsonPayload.getString(Constants.THING_NAME);
            	BaseEdgeDevice edgeDevice = messageDistributor.getEdgeDeviceMap().get(thingName);
            	JSONArray jsonArray=new JSONArray();
            	List<Message> messages =new ArrayList<Message>();
            	edgeDevice.getEdgeMessageQueue().drainTo(messages);
            	for ( Message message:messages){
            		jsonArray.put(message.getPayload());
            	}
            	response.getWriter().println(jsonArray.toString());
             }
        } catch (JSONException e) {
             e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("servlet post"+jsonPayload);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("servlet put");
    }
    
    

}
