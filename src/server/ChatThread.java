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
	private boolean clientStatus; // true: sender, false: receiver
	
	public ChatThread (Socket client){
		this.client = client;
	}
	
	@Override
	public void run() {
		InputStream is = null;
		ObjectInputStream ois = null;
		OutputStream os = null;
		ObjectOutputStream oos = null;
		try {
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
							clientStatus = false;
						}
					} else {
						// User is not present
						ChatUser user = new ChatUser(nickname);
						nick = nickname; // Saving username to identify user
						user.setReceiver(true);
						ChatServer.userList.put(nickname, user);
						response = new ChatRequest(0);
						System.out.println("User "+ nick + " receiver connected. Waiting for sender...");
						clientStatus = false;
					}
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
							clientStatus = true;
						}
					} else {
						// User is not present
						ChatUser user = new ChatUser(nickname);
						user.setSender(true);
						nick = nickname;
						ChatServer.userList.put(nickname, user);
						response = new ChatRequest(0);
						System.out.println("User "+ nick + " sender connected. Waiting for receiver...");
						clientStatus = true;
					}
					break;
					
				case "quit":
					// Remove user from list
					ChatServer.userList.remove(nick);
					System.out.println("User " + nick + " disconnected");
					exit = true;
					response = new ChatRequest(-1, "User disconnected");
					break;
					
				case "quitsender":
					if (ChatServer.userList.get(nick).isConnected()) {
						ChatServer.userList.get(nick).setSender(false);
						System.out.println("User " + nick + " sender disconnected");
					} else {
						ChatServer.userList.remove(nick);
						System.out.println("User " + nick + " disconnected");
					}
					exit = true;
					response = new ChatRequest(-1, "sender disconnected");
					break;
					
				case "quitreceiver":
					if (ChatServer.userList.get(nick).isConnected()) {
						ChatServer.userList.get(nick).setReceiver(false);
						System.out.println("User " + nick + " receiver disconnected");
						response = new ChatRequest(-1, "receiver disconnected");
					} else {
						response = new ChatRequest(-1, "receiver was not connected");
					}
					break;
					
				case "publicmessage":
					if (!ChatServer.userList.get(nick).isConnected()) {
						String client = clientStatus ? "receiver":"sender";
						response = new ChatRequest(-1, client + " not connected");
						break;
					}
					ChatMessage msg = (ChatMessage)request.getParam();
					ChatServer.room.addMessage(msg);
					System.out.println(ChatServer.room.getLastMessage().print());
					response = new ChatRequest(0);
					break;
					
				case "privatemessage":
					if (!ChatServer.userList.get(nick).isConnected()) {
						String client = clientStatus ? "receiver":"sender";
						response = new ChatRequest(0, client + " not connected");
						break;
					}
					ChatMessage msgpriv = (ChatMessage)request.getParam();
					// Check if receiver is connected before insert
					ChatUser u = ChatServer.userList.get(msgpriv.getReceiver());
					if (u == null || !u.isConnected()  ) {
						response = new ChatRequest(-1, "the receiver of the message is not connected to the server");
						break;
					}
					ChatServer.room.addMessage(msgpriv);
					System.out.println(ChatServer.room.getLastMessage().print());
					response = new ChatRequest(0);
					break;
					
				case "getmessagesfrom":
					if (ChatServer.userList.get(nick) == null) {
						response = new ChatRequest(-1, "disconnected");
						exit=true;
						break;
					}
					if (!ChatServer.userList.get(nick).getSender()) {
						// wait for sender connection
						response = new ChatRequest(0);
						break;
					}
					// if receiver is false, the receiver should quit
					if (!ChatServer.userList.get(nick).getReceiver()) {
						response = new ChatRequest(-1, "disconnected");
						exit=true;
						break;
					}
					int from = (int) request.getParam();
					ArrayList<ChatMessage> list = (ArrayList<ChatMessage>) ChatServer.room.listMessages(nick, from);
					response = new ChatRequest(0,list);
					break;
					
				case "list":
					if (!ChatServer.userList.get(nick).isConnected()) {
						String client = clientStatus ? "receiver":"sender";
						response = new ChatRequest(-1, client + " not connected");
						break;
					}
					ArrayList<String> userlist = new ArrayList<String>();
					for ( ChatUser user : ChatServer.userList.values() ) {
					    if (user.isConnected()) {
					    	userlist.add(user.getNickname());
					    }
					}
					response = new ChatRequest("list",null,userlist,0);
					break;
					
				default:
					response = new ChatRequest(-1, "Generic error");
				}
				// Output channel: Server to Client
				os = this.client.getOutputStream();
				oos = new ObjectOutputStream(os);
				oos.writeObject(response);
				oos.flush();
			}
		}catch(Exception e ){
			if (clientStatus) {
				if (ChatServer.userList.get(nick).isConnected())
					ChatServer.userList.get(nick).setSender(false);
				else
					ChatServer.userList.remove(nick);
				System.out.println("Sender disconnected");
			} else {
				if (ChatServer.userList.get(nick).isConnected())
					ChatServer.userList.get(nick).setReceiver(false);
				else
					ChatServer.userList.remove(nick);
				System.out.println("Receiver disconnected");
			}
			// System.out.println("ChatTread Exception:" + e.getMessage());
			// e.printStackTrace();
		}

	}

}
