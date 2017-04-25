package structs;

import java.io.Serializable;

public class ChatRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String requestCode;
	private String error;
	private Object param;
	private int responseCode;
	
	public ChatRequest() {
		this.requestCode = "";
		this.error = "";
		this.param = null;
		this.responseCode = 0;
	}
	
	// To use from client to server
	public ChatRequest(String req, Object par) {
		this();
		this.requestCode = req;
		this.param = par;
	}
	
	// To use from server to client
	public ChatRequest(int res, String err, Object par) {
		this();
		this.responseCode = res;
		this.error = err;
		this.param = par;
	}
	public ChatRequest(int res) {
		this();
		this.responseCode = res;
	}

	// Getters only
	public String getRequestCode() {
		return requestCode;
	}

	public String getError() {
		return error;
	}

	public Object getParam() {
		return param;
	}

	public int getResponseCode() {
		return responseCode;
	}
}
