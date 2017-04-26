package server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import structs.ChatRequest;

public class ChatThread implements Runnable {
	private Socket client = null;
	private boolean exit = false;
	private String nick;
	
	public ChatThread (Socket client){
		this.client = client;
	}
	
	@Override
	public void run() {
		try{
			while (!exit) {
				// Input channel: Client to Server
				InputStream is = this.client.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				
				// Read object from input stream
				ChatRequest request = (ChatRequest) ois.readObject();
				
				// Output channel: Server to Client
				OutputStream os = this.client.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(os);
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
					oos.writeObject(response);
					oos.flush();
					break;
					
				case "quit":
					// Remove user from list
					ChatServer.userList.remove(nick);
					// Send client ok message
					response = new ChatRequest(0);
					oos.writeObject(response);
					oos.flush();
					exit = true;
					System.out.println("User "+nick+ " disconnected");
					break;
					
				default:
					response = new ChatRequest(-1, "Generic error");
					oos.writeObject(response);
					oos.flush();
				}
			}
		}catch(Exception e ){
			System.out.println("ChatTread Exception:" + e.getMessage());
			e.printStackTrace();
		}

	}

}
