package client;

import game.Tchat;
import game.chess.Chess_Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import message.Authentication;
import message.Error;
import message.Error.ErrorType;
import message.IMsgChess;
import message.IMsgClient;
import message.IMsgParty;
import message.IMsgTchat;
import message.TechnicalError;
import network.CipherStreamSystem;

import org.apache.log4j.Logger;

import server.DataBase;
import server.Server;

import commun.EGames;
import commun.Player;

public class Client extends Observable implements Runnable {
	
	private static final Logger logger = Logger.getLogger(Client.class);
	private static CipherStreamSystem cipherSystem = new CipherStreamSystem("AES", 128);

	public enum StateOfClient {
		TRY_CONNECT, AUTHENTICATION_SUCCESS, AUTHENTICATION_FAIL, NEWSCORE, DISCONNECT
	}
	
	private static int nextid = 0;
	private Player player;
	private int id;
	private Server server;
	private EGames game;
	public final static int PASSWORD_MINIMAL_SIZE = 4;

	// TCP
	private Socket sock;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean run = false;

	public void interrupt() {
		try {
			in.close(); // Fermeture du flux si l'interruption n'a pas
						// fonctionne.
		} catch (Exception e) {
		}
	}

	public Client(Server server, Socket _sock) {
		this.addObserver(ClientManager.get());
		this.server = server;
		sock = _sock;
		id = nextid++;

		updateObservers(StateOfClient.TRY_CONNECT);
		// Ouverture des flux
		try {
			// instanciation des streams chiffrés
			cipherSystem.shareKeys(sock);

			// opening stream output
			logger.debug("Opening cipher output stream");
			CipherOutputStream cipherOut = cipherSystem.getOutputSteam(sock.getOutputStream());
			logger.debug("Opening object output stream");
			out = new ObjectOutputStream(cipherOut);
			out.flush();

			// opening stream input
			logger.debug("Opening cipher input stream");
			CipherInputStream cipherIn = cipherSystem.getInputStream(sock.getInputStream());
			logger.debug("Opening object input stream");
			in = new ObjectInputStream(cipherIn);

			logger.info("Streams I/O opened.");
		}
		catch (Exception e) {
			logger.error("Erreur lors de l'instanciation des flux de la socket");
			e.printStackTrace();
		}
	}
	
	private boolean connect(){
		Error error = new Error();

		Object obj = null;
		String name = null, password = null, hashPassword = null, email = null, countryCode = null, dateNow = null;
		SimpleDateFormat sdfSubscription = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfConnection = new SimpleDateFormat("yyyyMMdd HHmmss");
		Authentication authentication = null;

		while (true) {
			error.emptyListOfErrors();
			try {
				obj = in.readObject();
			}
			catch (Exception e) {
				destructSession(StateOfClient.AUTHENTICATION_FAIL);
				return false;
			}

			/* Gérer l'inscription et l'identification */
			if (obj instanceof Authentication) {
				logger.debug("Authentication Object received from " + sock.getInetAddress() + ":" + sock.getPort());
				authentication = (Authentication) obj;
				/* On vérifie que les données envoyées ne sont pas nulles */

				if (authentication.getUsername() == null || authentication.getUsername().isEmpty())
					error.addErrorType(ErrorType.EMPTY_NAME);

				if (authentication.getPassword() == null || authentication.getPassword().isEmpty())
					error.addErrorType(ErrorType.EMPTY_PASSWORD);

				if (error.hasError()) {
					logger.info("The authentication contains errors.");
					sendObject(error);
				}
				else {

					/* Infos client distant */
					name = authentication.getUsername();
					password = authentication.getPassword();
					hashPassword = sha256(password);

					if (authentication.isLogin()) {
						logger.info("Attempt to login.");
						try {
							if (DataBase.get().nameExists(name)) {
								if (DataBase.get().getHashPasswordOfPlayer(name).equals(hashPassword)) {
									if (ClientManager.get().getClientByName(name) != null) {
										error.addErrorType(ErrorType.PLAYER_ALREADY_CONNECTED);
										sendObject(error);
									}
									else {
										player = new Player(name, DataBase.get().getScoreOfPlayer(name), null);
										countryCode = DataBase.get().getCountryOfPlayer(name);
										player.setCountry(countryCode);
										authentication.setCountryCode(countryCode);
										authentication.setPlayer(player);
										DataBase.get().updateDateOfLastConnection(name, sdfConnection.format(new Date()));
										sendObject(authentication);
										logger.info("Login confirmed");
										updateObservers(StateOfClient.AUTHENTICATION_SUCCESS);
										return true;
									}
								} else {
									error.addErrorType(ErrorType.INCORRECT_PASSWORD);
									logger.info("The login contains error(s)");
									sendObject(error);
								}
							} else {
								error.addErrorType(ErrorType.INEXISTANT_NAME);
								logger.info("The logicon contains error(s).");
								sendObject(error);
							}
						} catch (TechnicalError technicalError) {
							sendObject(technicalError);
							logger.error("Sent an technical error.");
							logger.error(technicalError.getMessage());
							return false;
						}
					} else if (authentication.isSubscription()) {
						logger.info("Attempt to subscribe.");
						if (authentication.getCountryCode() == null)
							error.addErrorType(ErrorType.EMPTY_COUNTRYCODE);
						if (password.length() < PASSWORD_MINIMAL_SIZE)
							error.addErrorType(ErrorType.TOO_SHORT_PASSWORD);
						if (!authentication.isMailValid())
							error.addErrorType(ErrorType.INCORRECT_EMAIL);
						if (error.hasError()) {
							logger.info("The subscription contains error(s).");
							sendObject(error);
						} else {
							try {
								if (DataBase.get().nameExists(name)) {
									error.addErrorType(ErrorType.EXISTANT_NAME);
									logger.info("The subscription contains error(s).");
									sendObject(error);
								} else {
									email = authentication.getEmail();
									dateNow = sdfSubscription.format(new Date());
									DataBase.get().addNewPlayer(name, hashPassword, authentication.getCountryCode(), email, dateNow);
									player = new Player(name, 0, null);
									player.setCountry(authentication.getCountryCode());
									authentication.setPlayer(player);
									sendObject(authentication);
									logger.info("Subscription confirmed");
								}
							} catch (TechnicalError technicalError) {
								sendObject(technicalError);
								logger.error("Sent a technical error.");
								logger.error(technicalError.getMessage());
								return false;
							}
						}
					} else {
						logger.warn("Type of authentication is not informed.");
						destructSession(StateOfClient.AUTHENTICATION_FAIL);
						return false;
					}
				}
			} else {
				updateObservers(StateOfClient.AUTHENTICATION_FAIL);
				logger.info("The received packet was not expected.");
				return false;
			}
		}
	}

	public void destructSession(StateOfClient etat) {
		updateObservers(etat);
		try {
			logger.debug("Destruction of session");

			if (sock != null && !sock.isClosed()) {
				out.close();
				in.close();
				sock.close();
			}

		}
		catch (IOException e) {
			// e.printStackTrace();
		}
		finally {
			// nettoyage gc
			sock = null;
			in = null;
			out = null;
		}
	}

	public void run() {

		if (connect()) {
			run = true;
			while (run) {
				Object obj = null;

				try {
					obj = in.readObject();
					managePacket(obj);
				}
				catch (IOException e) {
					logger.error("Input stream of client " + player.getName() + " just closed");
					destructSession(StateOfClient.DISCONNECT);
					run = false;
					break;
				}
				catch (Exception e) {
					logger.error("Error while managing " + obj.getClass().getSimpleName() + " object (client " + player.getName() + ") : "
							+ e.getMessage());
					e.printStackTrace();
					// destructSession(StateOfClient.DISCONNECT);
					// run = false;
					// break;
				}

				// reset
				obj = null;
			}
		}
	}

	public void managePacket(Object obj) {
		/*
		 * Selon le type de paquet, on envoie le message au gestionnaire de
		 * - tchat
		 * - parties
		 * - jeu d'échec
		 */
		if (obj instanceof IMsgTchat) {
			logger.info("Reception of a tchat object.");
			Tchat.manageTchat(this, obj);
		}
		else if (obj instanceof IMsgParty) {
			logger.info("Reception of a party object.");
			Chess_Network.manageChessParties(this, obj);
		}
		else if (obj instanceof IMsgChess) {
			logger.info("Reception of a chess object.");
			Chess_Network.manageChessPlayer(this, obj);
		}
		else if (obj instanceof IMsgClient) {
			logger.info("Reception of a client object.");
			ClientManager.manageClients(this, obj);
		}
	}
	
	public synchronized void sendObject(Object obj) {
		try {
			out.writeObject(obj);
			out.flush();
			out.reset();
		}
		catch (Exception e) {
			logger.error("Error while sending object on network : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public InetAddress getInetAddress() {
		return sock.getInetAddress();
	}

	public int getPort() {
		return sock.getPort();
	}

	public static String sha256(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(text.getBytes("iso-8859-1"), 0, text.length());

			return convertToHex(md.digest());
		} catch (Exception e) {
		}
		return null;
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	@Override
	public String toString() {
		return player.getName();
	}

	public Socket getSock() {
		return sock;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}


	public int getUid() {
		return id;
	}

	public void setUid(int uid) {
		this.id = uid;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void updateObservers(Object obj) {
		setChanged();
		notifyObservers(obj);
	}

	public EGames getGame() {
		return game;
	}

	public void setGame(EGames game) {
		this.game = game;
	}

}