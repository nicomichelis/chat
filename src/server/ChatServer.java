package server;

import java.net.ServerSocket;
import java.net.Socket;
public class ChatServer {
	private static int port = 4000;
	public static ChatRoom room;
	
	public static void main(String[] args) {
		try {
			System.out.println("SERVER STARTED");
			ServerSocket server = new ServerSocket(port);
			while(true){
				Socket s = server.accept();
				System.out.println("CONNECTION ACCEPTED");

				//Runner
				ChatThread chatThread = new ChatThread(s); 
				//Launcher
				Thread chatThreadT = new Thread(chatThread);
				chatThreadT.start();
			}
		}catch(Exception e ){
			e.printStackTrace();
			
		}

	}

}
