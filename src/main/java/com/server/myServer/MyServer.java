package com.server.myServer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.awt.event.*; 

/**
 * Hello world!
 *
 */
public class MyServer {
    @SuppressWarnings("unchecked")
	public static void main( String[] args ) throws InterruptedException, IOException {
    	JSONArray users = readData();
    	DatagramSocket serverSocket = new DatagramSocket(1234);
    	System.out.println("Version 1.0");
    	byte[] content = new byte[65535];
    	
    	InetAddress ip = null;
    	DatagramPacket packet = null;
    	int clientport = -1;
    	
    	while(true) {
    		packet = new DatagramPacket(content, content.length);
    		System.out.println("Receiving ...");
    		serverSocket.receive(packet);
    		
    		String received = data(content);
    		System.out.println(received);
    		System.out.println(packet.getAddress().toString()+" "+packet.getPort());
    		
    		if(received.equals("request")) {
    			StringBuilder str = new StringBuilder("200\n");
    			users = readData();
    			str.append(users.toJSONString());
    			String s = str.toString();
    			
    			content = s.getBytes();
    			ip = packet.getAddress();
    			clientport = packet.getPort();
    			
    			packet = new DatagramPacket(content, content.length, ip, clientport);
    			
    			serverSocket.send(packet);
    			
    			break;
    		}
    		
    		content = new byte[65535];
    		
    	}
    	
    	content = new byte[65535];
    	
    	while(true) {
    		packet = new DatagramPacket(content, content.length);
    		System.out.println("Receiving ...");
    		serverSocket.receive(packet);
    		
    		System.out.println(packet.getAddress().toString()+" "+packet.getPort());
    		
    		String rec = data(content);
    		
    		if(rec.charAt(0)=='c') {
    			break;
    		}
    		
    		users = readData();
    		processCommand(rec, users);
    		System.out.println("After modify:");
    		readData();
    		
    		StringBuilder str = new StringBuilder("200\n");
			str.append(users.toJSONString());
			String s = str.toString();
			
			content = s.getBytes();
			ip = packet.getAddress();
			clientport = packet.getPort();
			packet = new DatagramPacket(content, content.length, ip, clientport);
			
			serverSocket.send(packet);
    		
			content = new byte[65535];
    	}
    	
    	serverSocket.close();
    }
    
    public static String data(byte[] a) 
    { 
        if (a == null) 
            return null; 
        StringBuilder ret = new StringBuilder(); 
        int i = 0; 
        while (a[i] != 0) 
        { 
            ret.append((char) a[i]); 
            i++; 
        } 
        return ret.toString(); 
    }
    
    public static int Number(String s) {
    	
    	int i = 0;
    	int num = 0;
    	while(i<s.length()) {
    		char c = s.charAt(i);
    		if(!Character.isDigit(c)) return -1;
    		num*=10;
    		num+= c-'0';
    		i++;
    	}
    	
    	return num;
    }
    
    @SuppressWarnings("unchecked")
	public static void processCommand(String rec, JSONArray users) {
    	JSONObject usersObject = new JSONObject();
        
        char c = rec.charAt(0);
        
        switch(c) {
        case 'r':
        	String match = rec.substring(2);
        	for(int i=0; i<users.size(); i++) {
        		JSONObject user = (JSONObject)users.get(i);
        		
        		if(user.get("name").toString().equals(match)) {
        			users.remove(user);
        			break;
        		}
        	}
        	break;
        case 'a':
        	users.add(createUser(rec.substring(2)));
        	break;
        }
        
        usersObject.put("users", users);
        
        try(FileWriter file = new FileWriter("users.json")){
        	file.write(usersObject.toJSONString());
        }
        catch(IOException e){
        	
        }
    }
    
    @SuppressWarnings("unchecked")
	public static JSONObject createUser(String name) {
    	JSONObject user = new JSONObject();
    	user.put("name", name);
    	return user;
    }
    
    @SuppressWarnings("unchecked")
	public static void resetData() {
    	JSONObject usersObject = new JSONObject();
    	
        JSONArray users = new JSONArray();
        
        users.add(createUser("Nischal"));
        users.add(createUser("Srilaya"));
        users.add(createUser("Navaneetha"));
        users.add(createUser("Venkata Rao"));
        
        usersObject.put("users", users);
        
        try(FileWriter file = new FileWriter("users.json")){
        	file.write(usersObject.toJSONString());
        }
        catch(IOException e){
        	
        }
    }
    
    public static JSONArray readData() {
    	JSONParser jsonParser = new JSONParser();
    	
    	JSONObject usersObject = null; 
    	
    	try(FileReader file = new FileReader("users.json")){
    		usersObject = (JSONObject) jsonParser.parse(file);
    	}catch (Exception e) {
			// TODO: handle exception
		}
    	
    	System.out.println("Data:");
    	
    	if(usersObject != null) {
    		JSONArray users = (JSONArray) usersObject.get("users");
    		for(int i=0; i< users.size(); i++) {
    			System.out.println(users.get(i));
    		}
    		return users;
    	}
    	
    	return null;
    	
    }
    
}
