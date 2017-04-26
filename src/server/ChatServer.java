package server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import structs.ChatRequest;
public class ChatServer {
	private static int port = 4000;
	private static HashMap<String, ChatUser> userlist;
	public static ChatRoom room;
	private static ServerSocket server;
	
	
	public static void main(String[] args) {
		try {
			userlist = new HashMap<String, ChatUser>();
			room = new ChatRoom();
			System.out.println("SERVER STARTED");
			server = new ServerSocket(port);
			while(true){
				Socket s = server.accept();
				System.out.println("CONNECTION ACCEPTED");
				// Check request for user
				InputStream is = s.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				ChatRequest Nick = (ChatRequest) ois.readObject();
				String nickname = (String)Nick.getParam();
				switch (Nick.getRequestCode()){
				case "loginsender":
					if (userlist.get(nickname) == null) {
						// new user, neither sender nor receiver is connected
						ChatUser user = new ChatUser(nickname);	
						user.setSender(true);
						userlist.put(nickname, user);
					} else {
						// user is already present
						ChatUser user = userlist.get(nickname);
						if (user.getSender()) {
							// Error sender already connected
							ChatRequest resp = new ChatRequest(-1,"Error: sender already connected");
							// Send response to client
							OutputStream os = s.getOutputStream();
							ObjectOutputStream oos = new ObjectOutputStream(os);
							oos.writeObject(resp);
							break;
						}
					}
					// Connecting sender to Server
					//Runner
					ChatThread chatThreadS = new ChatThread(s); 
					//Launcher
					Thread chatThreadTS = new Thread(chatThreadS);
					chatThreadTS.start();
					break;
					
				case "loginreceiver":
					if (userlist.get(nickname) == null) {
						// new user, neither sender nor receiver is connected
						ChatUser user = new ChatUser(nickname);	
						user.setReceiver(true);
						userlist.put(nickname, user);
					} else {
						// user is already present
						ChatUser user = userlist.get(nickname);
						if (user.getReceiver()) {
							// Error receiver already connected
							ChatRequest resp = new ChatRequest(-1,"Error: receiver already connected");
							// Send response to client
							OutputStream os = s.getOutputStream();
							ObjectOutputStream oos = new ObjectOutputStream(os);
							oos.writeObject(resp);
							break;
						}
					}
					// Send response to client, ok
					ChatRequest resp = new ChatRequest(0);
					OutputStream os = s.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					oos.writeObject(resp);
					// Connecting Receiver to Server
					System.out.println(nickname+" is connected");
					//Runner
					ChatThread chatThreadR = new ChatThread(s); 
					//Launcher
					Thread chatThreadTR = new Thread(chatThreadR);
					chatThreadTR.start();
					break;
				
				}
				/*
				//Runner
				ChatThread chatThread = new ChatThread(s); 
				//Launcher
				Thread chatThreadT = new Thread(chatThread);
				chatThreadT.start();
				*/
			}
		}catch(Exception e ){
			e.printStackTrace();
			
		}

	}

}
