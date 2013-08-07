package message;

public class Talk2Party implements IMsgTchat {

	private static final long serialVersionUID = 1L;

	private String message = "";
	
	public Talk2Party(String msg){
		setMessage(msg);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
