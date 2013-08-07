package network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import message.IMsg;

import org.apache.log4j.Logger;

/**
 * Système d'abonnement pour la gestion simplifiée et déportée de la réception
 * des objets sur le réseau. Cette classe s'utilise en collaboration de
 * l'interface {@link NetworkEventHandler}.
 * 
 * @author Florent
 * 
 */
public class NetworkEventManager {

	private static final Logger logger = Logger.getLogger(NetworkEventManager.class);

	/**
	 * Liste des abonnements
	 */
	private HashMap<Class<? extends IMsg>, LinkedBlockingQueue<NetworkEventHandler>> eventHandlers;
	private LinkedBlockingQueue<NetworkStateListener> stateListeners;
	private LinkedList<NetworkEvent> eventsCached;

	public NetworkEventManager() {
		eventHandlers = new HashMap<Class<? extends IMsg>, LinkedBlockingQueue<NetworkEventHandler>>();
		stateListeners = new LinkedBlockingQueue<NetworkStateListener>();
		eventsCached = new LinkedList<NetworkEvent>();
	}

	/**
	 * Notifie aux handlers abonnés, l'arrivée d'un objet sur le réseau.
	 * 
	 * @param ev
	 */
	protected void fireNetworkEvent(IMsg obj) {
		NetworkEvent event = new NetworkEvent(obj);
		fireNetworkEvent(event);
	}

	/**
	 * Notifie aux handlers abonnés, l'arrivée d'un évènement sur le réseau.
	 * 
	 * @param ev
	 */
	protected void fireNetworkEvent(NetworkEvent event) {
		logger.debug("Network event of type " + event.getSource().getClass().getSimpleName());

		// Send event to handlers targeting the event's class
		Class<? extends IMsg> evClass = (Class<? extends IMsg>) event.getSource().getClass();
		LinkedBlockingQueue<NetworkEventHandler> evHandlers = eventHandlers.get(evClass);

		if (evHandlers != null && !evHandlers.isEmpty()) {
			for (NetworkEventHandler h : evHandlers) {
				logger.debug("Handle sur " + h.getClass().getSimpleName() + " pour " + event.getSource().getClass().getSimpleName());
				h.handleNetworkEvent(event);
			}
		}
		else {
			logger.warn("No handler to fire the " + evClass.getName() + " object");
			eventsCached.add(event);
		}
	}

	/**
	 * Vide les évènements mis en cache et n'ayant pas de handlers au moment de
	 * leur réception.
	 */
	public synchronized void flushCache() {
		logger.debug("Flushing network events cached");
		LinkedList<NetworkEvent> events = (LinkedList<NetworkEvent>) eventsCached.clone();
		eventsCached.clear();

		for (NetworkEvent event : events) {
			fireNetworkEvent(event);
		}
	}

	/**
	 * Abonne un handler à un évènement donné.
	 * 
	 * @param eh
	 * @param ec
	 */
	public void addHandler(NetworkEventHandler eh, Class<? extends IMsg> ec) {
		logger.debug("Adding " + eh.getClass().getSimpleName() + " for " + ec.getSimpleName() + " handlers");
		LinkedBlockingQueue<NetworkEventHandler> handlersList = eventHandlers.get(ec);
		if (handlersList == null) {
			handlersList = new LinkedBlockingQueue<NetworkEventHandler>();
			eventHandlers.put(ec, handlersList);
		}

		if (!handlersList.contains(eh)) {
			handlersList.add(eh);
		}
	}

	/**
	 * Supprime un handler de tous les évènements possibles
	 * 
	 * @param eh
	 */
	public void removeHandler(NetworkEventHandler eh) {
		logger.debug("Removing all handlers for " + eh.getClass().getSimpleName());
		for (Entry<Class<? extends IMsg>, LinkedBlockingQueue<NetworkEventHandler>> entry : eventHandlers.entrySet()) {
			LinkedBlockingQueue<NetworkEventHandler> handlersList = entry.getValue();
			handlersList.remove(eh);
		}
	}

	/**
	 * Désabonne un handler d'un évènement donné.
	 * 
	 * @param eh
	 * @param ec
	 */
	public void removeHandler(NetworkEventHandler eh, Class<? extends IMsg> ec) {
		LinkedBlockingQueue<NetworkEventHandler> handlersList = eventHandlers.get(ec);
		if (handlersList != null) {
			handlersList.remove(eh);
		}
	}

	/**
	 * Notifie les listeners que l'état du réseau a changé.
	 * 
	 * @param obj
	 */
	protected void fireNetworkStateChanged(Object obj) {
		logger.debug("Network state changed");

		for (NetworkStateListener h : stateListeners) {
			logger.debug("Notification network state changed to " + h.getClass().getSimpleName());
			h.networkStateChanged(obj);
		}
	}

	/**
	 * Fonction d'abonnement aux changements d'état du réseau
	 * 
	 * @param eh
	 */
	public void addNetworkStateListener(NetworkStateListener eh) {
		stateListeners.add(eh);
	}

	/**
	 * Fonction de désabonnement aux changements d'état du réseau
	 * 
	 * @param eh
	 */
	public void removeNetworkStateListener(NetworkStateListener eh) {
		stateListeners.remove(eh);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		logger.info("Finalize NetworkEventManager");
		stateListeners.clear();
		stateListeners = null;
		eventHandlers.clear();
		eventHandlers = null;
	}
}