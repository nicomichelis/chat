package client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import structs.ChatRequest;
public class ChatClientReceiver {

	private static int port = 4000;
	private static ChatRequest response;
	private static boolean quit = false;
	
	public static void main(String[] args) {
		String ipaddr;
		if (args.length==0) ipaddr="127.0.0.1";
			else ipaddr=args[0];
		InetSocketAddress addr  = new InetSocketAddress(ipaddr, port);
		
		@SuppressWarnings("resource")
		Socket s = new Socket();
		
		try {
			s.connect(addr);
			do {
				// Output channel: Client to Server
				OutputStream os = s.getOutputStream();			
				ObjectOutputStream oos = new ObjectOutputStream(os);
				
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
				
				// Input channel: Server to Client
				InputStream is = s.getInputStream();
				ObjectInputStream iis = new ObjectInputStream(is);
				
				// Server response
				response = (ChatRequest)iis.readObject();
				if (response.getResponseCode() == -1)
					System.out.println(response.getError());
				
			} while(response.getResponseCode() != 0);
			System.out.println("Connected to the Server");
			
			while (!quit) {
				InputStream is = s.getInputStream();
				ObjectInputStream iis = new ObjectInputStream(is);
				// Server response
				response = (ChatRequest)iis.readObject();
				if (response.getResponseCode()==0) {
					// just write what's written in the message
					System.out.println((String)response.getParam());
				} else {
					System.out.println((String)response.getParam());
					OutputStream os = s.getOutputStream();			
					ObjectOutputStream oos = new ObjectOutputStream(os);
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
