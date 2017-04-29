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
	private static ChatRequest response;
	
	public static void main(String[] args) {
		int indexMessage = -1;
		boolean quit = false;
		String ipaddr;
		if (args.length==0) ipaddr="127.0.0.1";
			else ipaddr=args[0];
		InetSocketAddress addr  = new InetSocketAddress(ipaddr, port);
		
		@SuppressWarnings("resource")
		Socket s = new Socket();
		
		try {
			s.connect(addr);
			// Output channel: Client to Server
			OutputStream os = s.getOutputStream();			
			ObjectOutputStream oos = new ObjectOutputStream(os);
			// Input channel: Server to Client
			InputStream is = null;
			ObjectInputStream iis = null;
			do {
				// Request for a nick
				System.out.println("Insert Nickname: ");
				// Keyboard Input
				InputStreamReader reader = new InputStreamReader(System.in);
				BufferedReader buffer = new BufferedReader(reader);	
				String line = buffer.readLine();
				
				// Request login
				ChatRequest req = new ChatRequest("loginreceiver", line);
				oos.writeObject(req);
				oos.flush();
				
				is = s.getInputStream();
				iis = new ObjectInputStream(is);
				
				// Server response
				response = (ChatRequest)iis.readObject();
				if (response.getResponseCode() == -1)
					System.out.println(response.getError());
				
			} while(response.getResponseCode() != 0);
			System.out.println("Connected to the Server");
			
			while (!quit) {
				Thread.sleep(1000);
				// Check for new messages
				// Output channel: Client to Server
				os = s.getOutputStream();			
				oos = new ObjectOutputStream(os);
				ChatRequest req = new ChatRequest("getmessagesfrom", indexMessage);
				oos.writeObject(req);
				oos.flush();

				// wait for server response
				is = s.getInputStream();
				iis = new ObjectInputStream(is);
				// Server response
				response = (ChatRequest)iis.readObject();
				if (response.getResponseCode()==0) {
						@SuppressWarnings("unchecked")
						ArrayList<ChatMessage> retMessages = (ArrayList<ChatMessage>) response.getParam();
						for (ChatMessage msg:retMessages) {
							indexMessage = msg.getId();
							if (msg.getReceiver()!= null) 
								System.out.println("@"+msg.print());
							else
								System.out.println(msg.print());
						}
				} else {
					System.out.println((String)response.getParam());
					os = s.getOutputStream();			
					oos = new ObjectOutputStream(os);
					oos.writeObject(new ChatRequest("quit"));
					oos.flush();
					// Wait for server response to quit
					// Input channel: Server to Client
					is = s.getInputStream();
					iis = new ObjectInputStream(is);
					// Server response
					response = (ChatRequest)iis.readObject();
					if (response.getResponseCode()!=0) {
						System.out.println(response.getError());
					}
					System.out.println("Closing...");
					// Quit anyway
					quit=true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}	

	}

}
