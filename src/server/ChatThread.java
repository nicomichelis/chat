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
							System.out.println("a");
						} else {
							// Receiver not connected (but Sender is), connect it
							user.setReceiver(true);
							ChatServer.userList.put(nickname, user);
							response = new ChatRequest(0);
							System.out.println("b");
						}
					} else {
						// User is not present
						ChatUser user = new ChatUser(nickname);
						user.setReceiver(true);
						ChatServer.userList.put(nickname, user);
						System.out.println("c");
						response = new ChatRequest(0);
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
							System.out.println("aa");
						} else {
							// Sender not connected (but Receiver is) connect it
							user.setSender(true);
							ChatServer.userList.put(nickname, user);
							response = new ChatRequest(0);
							System.out.println("bb");
						}
					} else {
						// User is not present
						ChatUser user = new ChatUser(nickname);
						user.setSender(true);
						ChatServer.userList.put(nickname, user);
						System.out.println("cc");
						response = new ChatRequest(0);
					}
					oos.writeObject(response);
					oos.flush();
					break;
					
				default:
					response = new ChatRequest(-1, "Generic error");
					oos.writeObject(response);
					oos.flush();
				}
			}
			
			
			/*
			ChatRequest resp;
			do {
				//Canale input per mess dal client a Server
				InputStream is = this.client.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				// Read object from input stream
				ChatRequest Nick = (ChatRequest) ois.readObject();
				
				// TODO: Check for used nickname
				
				// TO REMOVE
				System.out.println((String)Nick.getParam());
				
				// Generate response
				resp = new ChatRequest(0);
				// Send response to client if everything's ok
				OutputStream os = this.client.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(resp);
			} while (resp.getResponseCode()!=0);
			
			*/
			/*
			ChatMessage msg = (ChatMessage)ois.readObject();
			String line = msg.getMessage();
			
			//Canale output per mess da Server al client
			OutputStream os = this.client.getOutputStream();
			OutputStreamWriter wr = new OutputStreamWriter(os);
			BufferedWriter outbuffer = new BufferedWriter(wr);
			
			while(line != null){
				if(line.equals("quit")){
					this.client.close();
					System.out.println("CONNECTION CLOSED BY CLIENT");
					break;
				}else{
					System.out.println(msg.getTimestamp());
					System.out.println(msg.getId());
					System.out.println(msg.getMessage());
					
					//invia risposta al client
					outbuffer.write("Message received ok");
					outbuffer.newLine();
					outbuffer.flush();
				}
				msg = (ChatMessage)ois.readObject();
			
			}*/
		}catch(Exception e ){
			System.out.println("ChatTread Exception:" + e.getMessage());
			e.printStackTrace();
		}

	}

}
