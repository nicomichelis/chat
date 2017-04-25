package server;

import java.net.ServerSocket;
import java.net.Socket;
public class ChatServer {
	private static int port = 4000;
	public static ChatRoom room;
	private static ServerSocket server;
	
	public static void main(String[] args) {
		try {
			room = new ChatRoom();
			System.out.println("SERVER STARTED");
			server = new ServerSocket(port);
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
