package message.party;

import message.IMsgParty;

public class NullParty implements IMsgParty {
	private static final long serialVersionUID = -6798419672793807463L;
	private boolean question = false;
	private boolean response = false;
	boolean accepted = false;

	public boolean isQuestion() {
		return question;
	}

	public void setQuestion(boolean question) {
		this.question = question;
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	@Override
	public String toString() {
		return "NullParty [question=" + question + ", response=" + response + ", accepted=" + accepted + "]";
	}

}
