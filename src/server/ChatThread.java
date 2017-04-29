package server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import structs.ChatMessage;
import structs.ChatRequest;

public class ChatThread implements Runnable {
	private Socket client = null;
	private boolean exit = false;
	private String nick  = null;
	
	public ChatThread (Socket client){
		this.client = client;
	}
	
	@Override
	public void run() {
		
		try{
			InputStream is = null;
			ObjectInputStream ois = null;
			OutputStream os = null;
			ObjectOutputStream oos = null;
			while (!exit) {
				// Input channel: Client to Server
				is = this.client.getInputStream();
				ois = new ObjectInputStream(is);
				
				// Read object from input stream
				ChatRequest request = (ChatRequest) ois.readObject();
				
				
				String nickname;
				ChatRequest response;
				switch (request.getRequestCode()) {
				case "loginreceiver":
					nickname = (String)request.getParam();
					if (ChatServer.userList.get(nickname)!=null) {
						// User is already present, check if receiver is already connected
						ChatUser user = ChatServer.userList.get(nickname);
						if (user.getReceiver()) {
							// Receiver already connected
							response = new ChatRequest(-1,"Error: receiver already connected");
						} else {
							// Receiver not connected (but Sender is), connect it
							nick = nickname;
							System.out.println("User "+ nick + " receiver&sender connected");
							user.setReceiver(true);
							ChatServer.userList.put(nickname, user);
							response = new ChatRequest(0);
						}
					} else {
						// User is not present
						ChatUser user = new ChatUser(nickname);
						nick = nickname; // Saving username to identify user
						user.setReceiver(true);
						ChatServer.userList.put(nickname, user);
						response = new ChatRequest(0);
						System.out.println("User "+ nick + " receiver connected. Waiting for sender...");
					}
					// Output channel: Server to Client
					os = this.client.getOutputStream();
					oos = new ObjectOutputStream(os);
					oos.writeObject(response);
					oos.flush();
					break;
					
				case "loginsender":
					nickname = (String)request.getParam();
					if (ChatServer.userList.get(nickname)!=null) {
						// User is already present, check if sender is already connected
						ChatUser user = ChatServer.userList.get(nickname);
						if (user.getSender()) {
							// Sender already connected
							response = new ChatRequest(-1,"Error: sender already connected");
						} else {
							// Sender not connected (but Receiver is). connect it
							nick = nickname;
							System.out.println("User "+ nick + " sender&receiver connected");
							user.setSender(true);
							ChatServer.userList.put(nickname, user);
							response = new ChatRequest(0);
						}
					} else {
						// User is not present
						ChatUser user = new ChatUser(nickname);
						user.setSender(true);
						nick = nickname;
						ChatServer.userList.put(nickname, user);
						response = new ChatRequest(0);
						System.out.println("User "+ nick + " sender connected. Waiting for receiver...");
					}
					// Output channel: Server to Client
					os = this.client.getOutputStream();
					oos = new ObjectOutputStream(os);
					oos.writeObject(response);
					oos.flush();
					break;
					
				case "quit":
					// Remove user from list
					ChatServer.userList.remove(nick);
					exit = true;
					System.out.println("User "+nick+ " disconnected");
					break;
					
				case "publicmessage":
					ChatMessage msg = (ChatMessage)request.getParam();
					ChatServer.room.addMessage(msg);
					System.out.println(ChatServer.room.getLastMessage().print());
					break;
					
				case "privatemessage":
					ChatMessage msgpriv = (ChatMessage)request.getParam();
					ChatServer.room.addMessage(msgpriv);
					System.out.println(ChatServer.room.getLastMessage().print());
					break;
					
				case "getmessagesfrom":
					int from = (int) request.getParam();
					ArrayList<ChatMessage> list = (ArrayList<ChatMessage>) ChatServer.room.listMessages(nick, from);
					response = new ChatRequest(0,list);
					// Output channel: Server to Client
					os = this.client.getOutputStream();
					oos = new ObjectOutputStream(os);
					oos.writeObject(response);
					oos.flush();
					break;
					
				case "list":
					ArrayList<String> userlist = new ArrayList<String>();
					for ( ChatUser user : ChatServer.userList.values() ) {
					    if (user.isConnected()) {
					    	userlist.add(user.getNickname());
					    }
					}
					response = new ChatRequest(0,userlist);
					// Output channel: Server to Client
					os = this.client.getOutputStream();
					oos = new ObjectOutputStream(os);
					oos.writeObject(response);
					oos.flush();
					break;
					
				default:
					response = new ChatRequest(-1, "Generic error");
					// Output channel: Server to Client
					os = this.client.getOutputStream();
					oos = new ObjectOutputStream(os);
					oos.writeObject(response);
					oos.flush();
				}
			}
		}catch(Exception e ){
			ChatServer.userList.remove(nick);
			System.out.println("ChatTread Exception:" + e.getMessage());
			e.printStackTrace();
		}

	}

}
