package message;

public class TechnicalError extends Exception implements IMsg {
	private static final long serialVersionUID = -1288765107846679909L;
	private Description description;
	private String message;
	
	public TechnicalError (Description description, String message) {
		this.description = description;
		this.message = message;
	}
	
	public enum Description {
		DB_EXCEPTION,
		TO_BE_COMPLETED		
	}

	public Description getDescription() {
		return description;
	}
	
	public String getMessage () {
		return message;
	}
	
}
