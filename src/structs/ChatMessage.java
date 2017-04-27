package structs;

import java.io.Serializable;
import java.sql.Timestamp;

public class ChatMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Timestamp timestamp;
	private String sender;
	private String receiver; // null if public
	private String message;
	private int id;
	
	// Default constructor
    public ChatMessage() {
    	this.timestamp = new Timestamp(System.currentTimeMillis());
    	this.sender = "";
    	this.receiver = "";
    	this.message = "";
    	this.id = -1;
    }
    
    public ChatMessage(String send, String rec, String msg) {
    	this.timestamp = new Timestamp(System.currentTimeMillis());
    	this.sender = send;
    	this.receiver = rec;
    	this.message = msg;
    	this.id = -1;
    }
    
    // Setters and Getters
    public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String print() {
		return "["+this.timestamp+"] ["+ this.sender + "]: "+ this.message;
	}
    
}
