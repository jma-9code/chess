package message;

import java.util.ArrayList;

/**
 * Cette énumération définie les codes erreurs concernant la partie réseau étant
 * géré par le système.
 */
public class Error implements IMsg {
	private static final long serialVersionUID = -5283931128440326386L;
	private ArrayList<ErrorType> listOfErrors;

	public Error() {
		listOfErrors = new ArrayList<ErrorType>();
	}

	public void addErrorType(ErrorType errorType) {
		listOfErrors.add(errorType);
	}

	public ArrayList<ErrorType> getErrors() {
		return this.listOfErrors;
	}

	public void emptyListOfErrors() {
		listOfErrors.clear();
	}

	public boolean hasError() {
		return (listOfErrors.size() != 0);
	}

	@Override
	public String toString() {
		StringBuffer msg = new StringBuffer();

		if (listOfErrors.size() > 1) {
			msg.append("Les erreurs suivantes sont survenues :\n");
		} else {
			msg.append("L'erreur suivante est survenue :\n");
		}

		for (ErrorType err : listOfErrors) {
			msg.append("- " + err.getDescription() + "\n");
		}

		return msg.toString();
	}

	public enum ErrorType {
		EMPTY_NAME("The name is missing."), 
		EMPTY_PASSWORD("The password is missing."), 
		EMPTY_COUNTRYCODE("The country code is missing."), 
		INEXISTANT_NAME("The name doesn't exist in the database."), 
		INCORRECT_PASSWORD("The password doesn't match the username."), 
		INCORRECT_EMAIL("The email is not valid."),
		EXISTANT_NAME("The name already exists in the database."), 
		TOO_SHORT_PASSWORD("The password is not long enough (min. 4 caracters)."),
 PLAYER_ALREADY_CONNECTED("The player is already connected");

		/**
		 * Message associé à l'erreur
		 */
		private String msg;

		ErrorType(final String _msg) {
			this.msg = _msg;
		}

		public String getDescription() {
			return msg;
		}
	}
}
