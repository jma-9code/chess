package message.party;

import java.util.ArrayList;

import message.IMsgParty;

import commun.AbsPartyInfo;


public class ListParties implements IMsgParty {

	private static final long serialVersionUID = -8697268478352829773L;
	private ArrayList<AbsPartyInfo> listOfParties;

	public ListParties() {
		listOfParties = new ArrayList<AbsPartyInfo>();
	}

	public ListParties(ArrayList<AbsPartyInfo> listOfParties) {
		// Je laisse en commentaire juste pour le fun ... merci Antoine.
		// Je pouvais chercher longtemps pourquoi il n'y avait pas de partie...
		// listOfParties = new ArrayList<AbsPartyInfo>();
		this.listOfParties = listOfParties;
	}

	public void setListOfParties(ArrayList<AbsPartyInfo> listOfParties) {
		this.listOfParties = listOfParties;
	}

	public ArrayList<AbsPartyInfo> getListOfParties() {
		return listOfParties;
	}

}
