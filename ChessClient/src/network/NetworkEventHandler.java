/**
 * 
 */
package network;

import java.util.EventListener;

/**
 * @author Florent
 * 
 */
public interface NetworkEventHandler extends EventListener {
	/**
	 * Notification de la réception d'un objet pour lequel le handler est
	 * abonné.
	 * 
	 * @param obj
	 *            Encapsulation de l'objet reçu, dispo via getSource()
	 */
	public void handleNetworkEvent(NetworkEvent obj);
}
