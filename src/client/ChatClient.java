package client;

//nella classe bin :   java client.ChatClient 127.0.0.1
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import structs.ChatRequest;
public class ChatClient {

	private static int port = 4000;
	private static ChatRequest response;
	
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
				// canale output per mess da client vs server
				OutputStream os = s.getOutputStream();			
				ObjectOutputStream oos = new ObjectOutputStream(os);
				
				// Request for a nick
				System.out.println("Insert Nickname: ");
				// Legge da tastiera
				InputStreamReader reader = new InputStreamReader(System.in);
				BufferedReader buffer = new BufferedReader(reader);	
				String line = buffer.readLine();
				
				// Request login
				ChatRequest req = new ChatRequest("loginsender", line);
				oos.writeObject(req);
				oos.flush();
				
				// canale input per mess da Server a Client
				InputStream is = s.getInputStream();
				ObjectInputStream iis = new ObjectInputStream(is);
				
				// Server response
				response = (ChatRequest)iis.readObject();
				if (response.getResponseCode() == -1)
					System.out.println(response.getError());
				
			} while(response.getResponseCode() != 0);
			System.out.println((String)response.getParam());
			/*
			boolean end = true;
			while(end){
				String line = buffer.readLine();
				switch (line) {
				case "quit":
					System.out.println("ye");
					end = false;
					break;
				}
				ChatMessage msg = new ChatMessage("io",null,line);
				oos.writeObject(msg);
				oos.flush();
				
				//String response = inbuffer.readLine();
				//System.out.println("Server responde : " + response);
			}
*/
		}catch(Exception e){
			e.printStackTrace();
		}	

	}

}
