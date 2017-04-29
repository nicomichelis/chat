package server;

import java.util.ArrayList;
import java.util.List;

import structs.ChatMessage;

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
	
	public synchronized ChatMessage getLastMessage() {
		if (lastID == -1) 
			return null;
		else return messageList.get(lastID);
	}
	
	
	public synchronized List<ChatMessage> listMessages(String chatUser, int lastMessage) {
		ArrayList<ChatMessage> retMessages = new ArrayList<ChatMessage>();
		for (int i = lastMessage + 1; i < messageList.size(); i++) {
			if (messageList.get(i).getReceiver() == null || messageList.get(i).getReceiver().equals(chatUser)){
				retMessages.add(messageList.get(i));
			}
		}
		return retMessages;
	}

}
