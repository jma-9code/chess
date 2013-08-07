package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import client.Client;

import commun.AbsPartyInfo;

public abstract class AbsPartyManager implements Observer {

	public enum EPartyManager{
		ADDPARTY, REMOVEPARTY, JOINPARTY, LEFTPARTY, STATUTPARTY, SCORECHANGED;
	}
	
	protected HashMap<Client, AbsParty> partys = new HashMap<Client, AbsParty>();
	protected static Object lock = new Object();
	
	public ArrayList<AbsPartyInfo> getAllPartyInfo(){
		ArrayList<AbsPartyInfo> partysinfo = new ArrayList<AbsPartyInfo>();
		for ( AbsParty p : partys.values() ) {
			if (p != null) {
				partysinfo.add(p.getPartyInfo());
			}
		}
		return partysinfo;
	}
	
	public abstract boolean addParty(AbsParty p);
	
	public abstract boolean joinParty(int id, Client c);
	
	public abstract void leftParty(Client c);

	public abstract AbsParty getPartyInWhichIsClient(Client cli);

	@Override
	public abstract void update(Observable arg0, Object arg1);

	public HashMap<Client, AbsParty> getPartys() {
		return partys;
	}

	public void setPartys(HashMap<Client, AbsParty> partys) {
		this.partys = partys;
	}


	
	

}
