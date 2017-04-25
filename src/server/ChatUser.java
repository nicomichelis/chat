package server;

public class ChatUser {
	private String nickname;
	private Boolean receiver;
	private Boolean sender;
	
	// Default constructor
	ChatUser() {
		this.nickname = "";
		this.receiver = false;
		this.sender = false;
	}
	// Constructor
	ChatUser(String nick) {
		this.nickname = nick;
		this.receiver = false;
		this.sender = false;
	}
	
	// Setters and Getters
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Boolean getReceiver() {
		return receiver;
	}
	public void setReceiver(Boolean receiver) {
		this.receiver = receiver;
	}
	public Boolean getSender() {
		return sender;
	}
	public void setSender(Boolean sender) {
		this.sender = sender;
	}
		
}
