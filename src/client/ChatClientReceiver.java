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
public class ChatClientReceiver {

	private static int port = 4000;
	private static String nickname;
	
	public static void main(String[] args) {
		ChatRequest req;
		int indexMessage = -1;
		boolean quit = false;
		String ipaddr;
		
		ipaddr = (args.length==0) ? "127.0.0.1" : args[0];
		InetSocketAddress addr  = new InetSocketAddress(ipaddr, port);
		Socket s = new Socket();
		
		// Output channel: Client to Server
		OutputStream os = null;		
		ObjectOutputStream oos = null;
		// Input channel: Server to Client
		InputStream is = null;
		ObjectInputStream iis = null;
		try {
			s.connect(addr);
			// Login part
			do {
				// Request for a nick
				System.out.print("Insert Nickname: ");
				// Keyboard Input
				InputStreamReader reader = new InputStreamReader(System.in);
				BufferedReader buffer = new BufferedReader(reader);	
				nickname = buffer.readLine();
				
				// Write to server
				os = s.getOutputStream();			
				oos = new ObjectOutputStream(os);
				req = new ChatRequest("loginreceiver", nickname);
				oos.writeObject(req);
				oos.flush();
				
				// Wait for Server response
				is = s.getInputStream();
				iis = new ObjectInputStream(is);
				
				// Server response
				req = (ChatRequest)iis.readObject();
				if (req.getResponseCode() == -1)
					System.out.println(req.getError());
				
			} while(req.getResponseCode() != 0);
			System.out.println("Connected to the Server");
			
			while (!quit) {
				// Check for new messages
				os = s.getOutputStream();			
				oos = new ObjectOutputStream(os);
				req = new ChatRequest("getmessagesfrom", indexMessage);
				oos.writeObject(req);
				oos.flush();

				// wait for server response
				is = s.getInputStream();
				iis = new ObjectInputStream(is);
				// Server response
				req = (ChatRequest)iis.readObject();
				
				if (req.getResponseCode()==0) {
					// the server sent a list of messages
					if (req.getParam()!=null) {
						@SuppressWarnings("unchecked")
						ArrayList<ChatMessage> retMessages = (ArrayList<ChatMessage>) req.getParam();
						for (ChatMessage msg:retMessages) {
							indexMessage = msg.getId();
							if (msg.getReceiver()!= null) 
								System.out.println("@"+msg.print());
							else
								System.out.println(msg.print());
						}
					}
				} else {
					// server sent an error or a quit
					System.out.println(req.getError());
					quit=true;
				}
				Thread.sleep(1000);
			}
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}	

	}

}
