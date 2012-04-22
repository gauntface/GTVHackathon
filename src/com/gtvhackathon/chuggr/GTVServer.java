package com.gtvhackathon.chuggr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class GTVServer {
	
	Context context;
	
	public GTVServer(Context context){
		this.context = context;
		
        new Thread(new SocketServerThread()).start();
        
	}
	

String getWifiIP(Context context){
    	
    	try{
    	
    	   WifiManager myWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	      
           WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
           int myIp = myWifiInfo.getIpAddress();
          
          
           int intMyIp3 = myIp/0x1000000;
           int intMyIp3mod = myIp%0x1000000;
          
           int intMyIp2 = intMyIp3mod/0x10000;
           int intMyIp2mod = intMyIp3mod%0x10000;
          
           int intMyIp1 = intMyIp2mod/0x100;
           int intMyIp0 = intMyIp2mod%0x100;
          
           return String.valueOf(intMyIp0)
             + "." + String.valueOf(intMyIp1)
             + "." + String.valueOf(intMyIp2)
             + "." + String.valueOf(intMyIp3)
             ;
    	
    	}catch(Exception e){
    		e.toString();
    	}
		return "";
           
    }
    
    Vector<String> playerDevicesIPs = new Vector<String>();
    
    void sendMessage(final String targetIP, String msg){
    	
    	boolean iphandshake = msg.length()<1;
    	
    	if(iphandshake){
    		msg = getWifiIP(context);    		
    	}
    	
        try {
            Socket s = new Socket(targetIP,4351);
            
            //outgoing stream redirect to socket
            OutputStream out = s.getOutputStream();
           
            PrintWriter output = new PrintWriter(out);
            output.println(msg);
            
            output.flush();
            out.flush();
            output.close();
            s.close();
            
            if(iphandshake){
                Log.e("xxxx", "gotip"+targetIP);
            	playerDevicesIPs.add(targetIP);
            }
            
	    } catch (Exception e){
	    	e.toString();
	    }

    }	
    
    
    class SocketServerThread implements Runnable{

		public void run(){
			
			try {
	            Boolean end = false;
	            ServerSocket ss = new ServerSocket(4351);
	            
	            
	            Socket s = null;
	            
	            while(!end){
	                    //Server is waiting for client here, if needed
	                    s = ss.accept();
	                    InputStream istream = s.getInputStream();
	                    InputStreamReader isreader = new InputStreamReader(istream);
	                    BufferedReader input = new BufferedReader(isreader);
	                     
	                    String st;
	                    boolean endRead = true;
	                    while(endRead){
	                    	st = input.readLine();
	                    	if(st!=null){
                                Log.e("xxx","someone sent something!!!");
			                    if(st.length()>0){
			                    	sendMessage(st,"Connected");
			                    	endRead = false;
			                    }
	                    	}
	                    
	                    }
	                   
	                    
	            }
	            
	            if(s!=null){
	            s.close();
	            }
	    ss.close();
	           
	           
	    } catch (Exception e){
	            e.printStackTrace();
	    }
			
			
		}
    	
    }

    void sendEvent(String whatwhat){
    	
    	for(int i = 0;i<playerDevicesIPs.size();i++){
    		sendMessage(playerDevicesIPs.get(i),whatwhat);
    	}
    	
    }
    

	
	
}
