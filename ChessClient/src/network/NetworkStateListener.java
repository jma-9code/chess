package network;

public interface NetworkStateListener {
	/**
	 * Notification de changement d'état
	 * 
	 * @param obj
	 *            Nouvel état
	 */
	public void networkStateChanged(Object obj);
}
