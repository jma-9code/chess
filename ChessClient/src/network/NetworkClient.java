package network;

import gui.GUIErrorsException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import main.Config;
import message.Authentication;
import message.Error;
import message.IMsg;

import org.apache.log4j.Logger;

import commun.EGames;
import commun.Player;

public class NetworkClient extends Observable implements Runnable {

	private static final Logger logger = Logger.getLogger(NetworkClient.class);

	private static String SERVER_IP;

	static {
		try {
			SERVER_IP = InetAddress.getByName(Config.ADRESS_SERV).getHostAddress();
		}
		catch (UnknownHostException e) {
			new GUIErrorsException("Erreur réseau", "Résolution de l'hôte impossible, vérifiez votre connexion et l'état du serveur.")
					.showErrors(null);
		}
	}

	public enum ENetworkClient {
		/**
		 * Déconnecté état initial ou deconnexion utilisateur
		 */
		DISCONNECTED,

		/**
		 * Destination non disponible (tentative de connexion effectuée) ou
		 * ayant eu une erreur
		 */
		NOT_AVAILABLE,

		/**
		 * Connexion en attente de l'identification de l'utilisateur (ou d'une
		 * souscription).
		 */
		WAITING_IDENTIFICATION,

		/**
		 * Connecté, processus d'identification terminé.
		 */
		CONNECTED,

		/**
		 * Requête de déconnexion, permet de différencier le passage au statut
		 * disconnect (normal) et not_available (anormal).
		 */
		DISCONNECTION_REQUEST;
	}

	private static NetworkClient instance = null;
	private static final Object lock = new Object();

	private int SERVER_PORT = 5776;
	private Socket socketTCP = null;
	private ENetworkClient statut = ENetworkClient.DISCONNECTED;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private boolean running = true;

	private CountDownLatch identificationLock = new CountDownLatch(1);
	private CountDownLatch retryConnectionLock = new CountDownLatch(1);

	/**
	 * Instance player correspondant au client connecté
	 */
	private Player player;

	/**
	 * Fil d'exécution du réseau
	 */
	private static Thread th;

	private NetworkClient() {
	}

	public static NetworkClient getInstance() {
		if (instance == null) {
			instance = new NetworkClient();

			th = new Thread(instance, "Network Client");
			th.start();
		}

		return instance;
	}

	@Override
	public void run() {

		while (running) {

			try {
				initConnection();
				changeState(ENetworkClient.WAITING_IDENTIFICATION);

				// autoIdentification();
				identificationLock.await();
				// rearm latch
				identificationLock = new CountDownLatch(1);

				logger.info("User just connected");
				changeState(ENetworkClient.CONNECTED);

				startManagingObject();
				changeState(ENetworkClient.DISCONNECTED);
			}
			catch (Exception e) {
				if (statut != ENetworkClient.DISCONNECTION_REQUEST) {
					logger.info("Unable to connect to server, will retry in " + Config.RETRY_CONNECTION_CYCLE + "s : " + e.getMessage());
					e.printStackTrace();
					changeState(ENetworkClient.NOT_AVAILABLE);

					try {
						retryConnectionLock.await(Config.RETRY_CONNECTION_CYCLE, TimeUnit.SECONDS);
						// rearm latch
						retryConnectionLock = new CountDownLatch(1);
					}
					catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				else {
					logger.info("Disconnection requested");
				}

			}

		}

		logger.info("End of the network session");
	}

	private void initConnection() throws Exception {
		try {
			SocketAddress addr = new InetSocketAddress(SERVER_IP, SERVER_PORT);
			socketTCP = new Socket();
			socketTCP.connect(addr, Config.TIMEOUT_CONNECTION);

			// instanciation des streams chiffrés
			CipherStreamSystem cipherSystem = new CipherStreamSystem("AES", 128);
			cipherSystem.shareKeys(socketTCP);

			// opening stream output
			logger.debug("Opening cipher output stream");
			CipherOutputStream cipherOut = cipherSystem.getOutputSteam(socketTCP.getOutputStream());
			logger.debug("Opening object output stream");
			out = new ObjectOutputStream(cipherOut);
			out.flush();

			// opening stream input
			logger.debug("Opening cipher input stream");
			CipherInputStream cipherIn = cipherSystem.getInputStream(socketTCP.getInputStream());
			logger.debug("Opening object input stream");
			in = new ObjectInputStream(cipherIn);
			logger.info("Streams I/O opened, ready for identification");
		}
		catch (ConnectException e) {
			if (socketTCP != null) {
				socketTCP.close();
			}

			socketTCP = null;
			out = null;
			in = null;

			throw e;
		}
	}

	/**
	 * Processus d'identification. Si les informations nécessaires sont
	 * disponibles.
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void autoIdentification() throws Exception {
		if (Config.NAME != null && Config.PWD != null) {
			try {
				login(Config.NAME, Config.PWD);
			}
			catch (GUIErrorsException e) {
				logger.error("Auto-login failed.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Identifie l'utilisateur avec les informations données
	 * 
	 * @param name
	 * @param pwd
	 * @throws Exception
	 */
	public void login(String name, String pwd) throws GUIErrorsException {
		Config.NAME = name;
		Config.PWD = pwd;

		if (statut == ENetworkClient.DISCONNECTED || statut == ENetworkClient.NOT_AVAILABLE) {
			retryConnectionLock.countDown();
			throw new GUIErrorsException("Connexion", "Erreur : serveur indisponible.");
		}

		Authentication auth = new Authentication(name, pwd, null, null, EGames.GAME_CHESS);
		auth.setLogin(true);

		logger.debug("Sent a request of login verification.");
		sendObject(auth);

		IMsg obj = null;
		try {
			obj = NetworkManager.castIMsg(in.readObject());
		}
		catch (IOException e) {
			throw new GUIErrorsException("Identification", "Erreur du serveur : " + e.getMessage());
		}
		catch (Exception e) {
			logger.error("Failed to read object (type " + obj.getClass().getSimpleName() + ").");
			throw new GUIErrorsException("Identification", "Erreur lors de la réponse du serveur : " + e.getMessage());
		}

		if (obj instanceof Authentication && ((Authentication) obj).isLogin()) {
			player = ((Authentication) obj).getPlayer();
			NetworkManager.getInstance().fireNetworkEvent(obj);
			identificationLock.countDown();
		}
		else if (obj instanceof Error) {
			changeState(ENetworkClient.WAITING_IDENTIFICATION);
			throw new GUIErrorsException("Identification", (Error) obj);
		}
	}

	/**
	 * Inscription d'un utilisateur
	 * 
	 * @param name
	 * @param pwd
	 * @param email
	 * @param country
	 * @throws GUIErrorsException
	 */
	public void subscribe(String name, String pwd, String email, String country) throws GUIErrorsException {
		if (statut == ENetworkClient.DISCONNECTED || statut == ENetworkClient.NOT_AVAILABLE) {
			retryConnectionLock.countDown();
			throw new GUIErrorsException("Inscription", "Erreur : serveur indisponible.");
		}

		Authentication auth = new Authentication(name, pwd, country, email, EGames.GAME_CHESS);
		auth.setSubscription(true);

		logger.debug("Sent a request of subscription.");
		sendObject(auth);

		IMsg obj = null;
		try {
			obj = NetworkManager.castIMsg(in.readObject());
		}
		catch (Exception e) {
			logger.error("Failed to read object.");
			throw new GUIErrorsException("Inscription", "Erreur lors de la réponse du serveur : " + e.getMessage());
		}

		if (obj instanceof Error) {
			logger.info("The subscription contains error(s).");
			throw new GUIErrorsException("Inscription", (Error) obj);
		}
		else if (obj instanceof Authentication) {
			logger.info("Subscription confirmed !");
			NetworkManager.getInstance().fireNetworkEvent(obj);
		}
	}

	/**
	 * Boucle de prise des objets en entrée réseau
	 */
	private void startManagingObject() throws Exception {
		logger.debug("Start managing object");
		while (running) {
			// si in renvoie une exception, on remonte au process de connexion
			// car on considère que la connexion est HS.
			Object obj = in.readObject();

			try {
				NetworkManager.getInstance().manageObject(obj);
			}
			catch (NetworkException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Permet de close le programme, meme si on est dans la boucle principal
	 */
	public synchronized void interrupt() {
		changeState(ENetworkClient.DISCONNECTION_REQUEST);
		running = false;

		try {
			in.close(); // Fermeture du flux
		}
		catch (Exception e) {
		}

		try {
			socketTCP.close(); // Fermeture du flux
		}
		catch (Exception e) {
		}

		th.interrupt();
	}

	/**
	 * Changement d'état du serveur
	 * 
	 * @param state
	 */
	private void changeState(ENetworkClient state) {
		if (ENetworkClient.DISCONNECTED.equals(state) || ENetworkClient.NOT_AVAILABLE.equals(state)) {
			this.player = null;
		}

		this.statut = state;
		NetworkManager.getInstance().fireNetworkStateChanged(state);
	}

	/**
	 * Méthode d'envoi d'objet réseau
	 * 
	 * @param obj
	 */
	public synchronized void sendObject(Object obj) {
		try {
			logger.debug("Sent " + obj.getClass().getSimpleName() + " object to the server");
			out.writeObject(obj);
			out.flush();
			out.reset();
		}
		catch (Exception e) {
			logger.error("send object failed : " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Récupère une instance de player. Si cette instance n'est pas disponible,
	 * une exception est levée.
	 * 
	 * @return
	 * @throws NetworkException
	 */
	public Player getPlayer() throws NetworkException {
		if (player == null) {
			throw new NetworkException("Instance player null");
		}

		return player;
	}

	public void updateObservers(Object obj) {
		setChanged();
		notifyObservers(obj);
	}

	public ENetworkClient getStatut() {
		return statut;
	}

	public void setStatut(ENetworkClient statut) {
		this.statut = statut;
	}

	public boolean isConnected() {
		return this.statut == ENetworkClient.CONNECTED;
	}

}
