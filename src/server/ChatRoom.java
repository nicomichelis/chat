package server;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
	private List<ChatMessage> messageList;
	private int lastID;
	
	ChatRoom(){
		messageList = new ArrayList<ChatMessage>();
		lastID = -1;
	}
	
	public synchronized int addMessage(ChatMessage msg) {
		this.lastID++;
		msg.setId(lastID);
		this.messageList.add(msg);
		return this.lastID;
	}
	
	public List<ChatMessage> listMessages(String chatUser, int lastMessage) {
		ArrayList<ChatMessage> retMessages = new ArrayList<ChatMessage>();
		if (lastMessage == -1) {
			// Only last message should be displayed
			for (int i = lastID; i > 0; i++) {
				if (messageList.get(i).getReceiver() == null || messageList.get(i).getReceiver() == chatUser){
					retMessages.add(messageList.get(i));
					return retMessages;
				}
			}
			return null;
		} else {
			// lastMessage contains the ID of the last message displayed by the user
			// all messages past that one should be displayed
			// remember to check for private messages!
			for (int i = lastID; i < messageList.size(); i++) {
				if (messageList.get(i).getReceiver() == null || messageList.get(i).getReceiver() == chatUser){
					retMessages.add(messageList.get(i));
				}
			}
			return retMessages;
		}
	}

}
