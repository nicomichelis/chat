package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import structs.ChatMessage;
import structs.ChatRequest;

public class ChatThread implements Runnable {

	private Socket client = null;

	public ChatThread (Socket client){
		this.client = client;
	}

	@Override
	public void run() {
		try{
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
