package client;

//nella classe bin :   java client.ChatClient 127.0.0.1
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
public class ChatClient {

	private static int port = 4000;
	
	public static void main(String[] args) {
		
		String ipaddr = args[0];
		InetSocketAddress addr  = new InetSocketAddress(ipaddr, port);
		Socket s = new Socket();
	
		try {
			s.connect(addr);
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader buffer = new BufferedReader(reader);
			
			//++++COMUNICAZIONE BIDIREZIONALE++++
			// canale output per mess da client vs server
			OutputStream os = s.getOutputStream();
			OutputStreamWriter wr = new OutputStreamWriter(os);
			BufferedWriter outbuffer = new BufferedWriter(wr);
			
			//per scrivere strutture java:serializzazione
			//vedi StudentSerializer.java...
			//ObjectOutputStream oos = new ObjectOutputStream(os);
			
			// canale input per mess da Server a Client
			InputStream is = s.getInputStream();
			InputStreamReader rd = new InputStreamReader(is);
			BufferedReader inbuffer = new BufferedReader(rd);
			
			while(true){
				String line = buffer.readLine();
				outbuffer.write(line);
				outbuffer.newLine();
				outbuffer.flush();
				if(line.equals("quit")){
					break;
				}
				String response = inbuffer.readLine();
				System.out.println("Server responde : " + response);
			}

		}catch(Exception e){
			e.printStackTrace();
		}	

	}

}
