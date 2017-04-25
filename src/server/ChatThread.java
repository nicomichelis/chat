package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatThread implements Runnable {

	private Socket client = null;

	public ChatThread (Socket client){
		this.client = client;
	}

	@Override
	public void run() {
		try{
			InputStream is = this.client.getInputStream();

			//Canale input per mess dal client a Server
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader buffer = new BufferedReader(reader);
			String line = buffer.readLine();
			
			
			
			//per ricevere strutture java :deserializzo
			//vedi StudentDeserializer.java
			//ObjectInputStream ois = new ObjectInputStream(is);
			
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
					ChatMessage msg = new ChatMessage("se1","re1",line);
					System.out.println(msg.getTimestamp());
					System.out.println(msg.getId());
					
					//invia risposta al client
					outbuffer.write("Message received ok");
					outbuffer.newLine();
					outbuffer.flush();
				}
				line = buffer.readLine();
			}
		}catch(Exception e ){
			System.out.println("ChatTread Exception:" + e.getMessage());
			e.printStackTrace();
		}

	}

}
