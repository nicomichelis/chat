package client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import structs.ChatMessage;
import structs.ChatRequest;
public class ChatClientSender {

	private static int port = 4000;
	private static ChatRequest response;
	private static String nickname;
	
	public static void main(String[] args) {
		boolean quit = false;
		ChatRequest req = null;
		// Output channel: Client to Server
		OutputStream os = null;			
		ObjectOutputStream oos = null;
		// Input channel: Server to Client
		InputStream is = null;
		ObjectInputStream iis = null;
		String ipaddr;
		ipaddr = (args.length==0) ? "127.0.0.1" : args[0];
		InetSocketAddress addr  = new InetSocketAddress(ipaddr, port);
		Socket s = new Socket();
		
		try {
			s.connect(addr);
			// Login part
			do {
				// Request for a nick
				System.out.println("Insert Nickname: ");
				// Keyboard Input
				InputStreamReader reader = new InputStreamReader(System.in);
				BufferedReader buffer = new BufferedReader(reader);	
				nickname = buffer.readLine();
				
				// Write to server
				os = s.getOutputStream();			
				oos = new ObjectOutputStream(os);
				// Request login
				req = new ChatRequest("loginsender", nickname);
				oos.writeObject(req);
				oos.flush();
				
				// Wait for Server response
				is = s.getInputStream();
				iis = new ObjectInputStream(is);
				
				// Server response
				response = (ChatRequest)iis.readObject();
				if (response.getResponseCode() == -1)
					System.out.println(response.getError());
				
			} while(response.getResponseCode() != 0);
			System.out.println("Connected to the Server");
			
			// Send requests to server
			while (!quit) {
				System.out.println("Comando: ");
				// Legge da tastiera
				InputStreamReader reader = new InputStreamReader(System.in);
				BufferedReader buffer = new BufferedReader(reader);	
				String line = buffer.readLine();
				if (line.startsWith("/")) {
					// System command
					line = line.substring(1); // remove the /
					switch (line.split(" ", 2)[0]) { // taking only the first word
					case "quit":
						req = new ChatRequest("quit");
						os = s.getOutputStream();			
						oos = new ObjectOutputStream(os);
						oos.writeObject(req);
						oos.flush();
						System.out.println("Closing...");
						is = s.getInputStream();
						iis = new ObjectInputStream(is);
						req = (ChatRequest)iis.readObject();
						// Quit anyway
						quit=true;
						break;
					case "list":
						os = s.getOutputStream();			
						oos = new ObjectOutputStream(os);
						req = new ChatRequest("list");
						oos.writeObject(req);
						oos.flush();
						is = s.getInputStream();
						iis = new ObjectInputStream(is);
						req = (ChatRequest)iis.readObject();
						if (req.getResponseCode()!=0) {
							System.out.println(req.getError());
						} else {
							if (req.getResponseCode()==0) {
								System.out.println("Active user list: ");
								@SuppressWarnings("unchecked")
								ArrayList<String> userlist = (ArrayList<String>) req.getParam();
								for (String username:userlist) {
									System.out.println("- "+username);
								}
							} else {
								System.out.println(req.getError());
							}
						}
						break;
					default:
						System.out.println("Command not recognized");	
					}
				} else {
					if (line.startsWith("@")) {
						// Private message to someone
						// the first thing after the @ is the nickname of the receiver
						line = line.substring(1); // remove the @
						ChatMessage message = new ChatMessage(nickname, line.split(" ", 2)[0], line.split(" ", 2)[1]);
						req = new ChatRequest("privatemessage", message);
						os = s.getOutputStream();			
						oos = new ObjectOutputStream(os);
						oos.writeObject(req);
						oos.flush();
						// wait for server response
						is = s.getInputStream();
						iis = new ObjectInputStream(is);
						req = (ChatRequest)iis.readObject();
						
					} else {
						// Public message
						ChatMessage message = new ChatMessage(nickname, null, line);
						req = new ChatRequest("publicmessage", message);
						os = s.getOutputStream();			
						oos = new ObjectOutputStream(os);
						oos.writeObject(req);
						oos.flush();
						// wait for server response 
						is = s.getInputStream();
						iis = new ObjectInputStream(is);
						req = (ChatRequest)iis.readObject();
						System.out.println(req.getResponseCode());
					}
				}
			}
			s.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}	

	}

}
