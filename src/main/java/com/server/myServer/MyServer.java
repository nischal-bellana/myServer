package com.server.myServer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    	resetData();
    	JSONArray users = readData();
    	DatagramSocket serverSocket = new DatagramSocket(1234);
    	myFrame frame = new myFrame(users);
    	
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
    		
    		String rec = data(content);
    		
    		if(rec.charAt(0)=='c') {
    			frame.f.dispose();
    			break;
    		}
    		
    		processCommand(rec);
    		users = readData();
    		frame.refresh(users);
    		
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
	public static void processCommand(String rec) {
    	JSONObject usersObject = new JSONObject();
    	
        JSONArray users = readData();
        
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
        
        try(FileWriter file = new FileWriter("target/jsonfiles/users.json")){
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
        
        try(FileWriter file = new FileWriter("target/jsonfiles/users.json")){
        	file.write(usersObject.toJSONString());
        }
        catch(IOException e){
        	
        }
    }
    
    public static JSONArray readData() {
    	JSONParser jsonParser = new JSONParser();
    	
    	JSONObject usersObject = null; 
    	
    	try(FileReader file = new FileReader("target/jsonfiles/users.json")){
    		usersObject = (JSONObject) jsonParser.parse(file);
    	}catch (Exception e) {
			// TODO: handle exception
		}
    	
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

class myFrame {
	Frame f;
	Label heading;
	
	public myFrame() {
		f = new Frame();
    	f.setBounds(500,200,500, 500);
    	f.setTitle("Users");
    	f.setLayout(null);
    	f.setVisible(true);
    	f.addWindowListener (new WindowAdapter() {    
            public void windowClosing (WindowEvent e) {   
                f.dispose();
                System.exit(0);
            }    
        });  
  	
	}
	
	public myFrame(JSONArray users) {
		this();
		heading = new Label("UserName: ");
		heading.setBounds(10, 50, 300, 20);
		f.add(heading);
		
		for(int i=0; i< users.size(); i++) {
			JSONObject user = (JSONObject)users.get(i);
			Label label = new Label(user.get("name").toString());
			label.setBounds(10,80+30*i,300,20);
			f.add(label);
		}
	}
	
	public void refresh(JSONArray users) throws InterruptedException {
		f.removeAll();
		heading = new Label("UserName: ");
		heading.setBounds(10, 50, 300, 20);
		f.add(heading);
		
		for(int i=0; i< users.size(); i++) {
			JSONObject user = (JSONObject)users.get(i);
			Label label = new Label(user.get("name").toString());
			label.setBounds(10,80+30*i,300,20);
			f.add(label);
		}
		
	}
	
}
