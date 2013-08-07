package network;

import message.IMsg;

import org.apache.log4j.Logger;

public class NetworkManager extends NetworkEventManager {
	private static final Logger logger = Logger.getLogger(NetworkManager.class);

	public enum NMInfo {
		LOGIN, SUBSCRIBE, NEWPARTY, JOINPARTY, LEFTPARTY, UPDATEPLAYERS, UPDATEPARTYS, MSG2PARTY, MSG2ALL, STARTPARTY, ENDPARTY;
	}

	private static NetworkManager network = null;

	public static NetworkManager getInstance() {
		if (network == null) {
			network = new NetworkManager();
		}

		return network;
	}

	/**
	 * Gestion générale objet réseau
	 * 
	 * @param obj
	 * @throws NetworkException
	 */
	public void manageObject(final Object obj) throws NetworkException {
		IMsg msg = castIMsg(obj);
		fireNetworkEvent(msg);

		// Message public
		/*
		 * if (obj instanceof Talk2All) {
		 * Talk2All msg2all = (Talk2All) obj;
		 * NetworkClient.getInstance().getMsg2All().append(msg2all.getMessage()
		 * + "\n");
		 * // updateObservers(NMInfo.MSG2ALL);
		 * }
		 * // Message privee
		 * else if (obj instanceof Talk2Party) {
		 * Talk2Party msg2party = (Talk2Party) obj;
		 * NetworkClient.getInstance().getMsg2Party().append(msg2party.getMessage
		 * () + "\n");
		 * // updateObservers(NMInfo.MSG2PARTY);
		 * }
		 * else if (obj instanceof IMsg) {
		 * fireNetworkEvent((IMsg) obj);
		 * }
		 */
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static IMsg castIMsg(Object obj) throws NetworkException {
		if (obj instanceof IMsg) {
			return (IMsg) obj;
		}
		else {
			throw new NetworkException("IMsg object expected (" + obj.getClass().getSimpleName() + " received).");
		}
	}

	public void forceNetworkStateNotification() {
		fireNetworkStateChanged(NetworkClient.getInstance().getStatut());
	}
}
