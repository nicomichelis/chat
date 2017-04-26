package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
public class ChatServer {

	private static int port = 4000;
	private static ServerSocket server;
	public static ChatRoom room = new ChatRoom();
	public static HashMap<String, ChatUser> userList = new HashMap<String, ChatUser>();
	
	public static void main(String[] args) {
		userList = new HashMap<String, ChatUser>();
		
		try {
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
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
