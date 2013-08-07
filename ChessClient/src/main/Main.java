package main;

import gui.LoginGUI;

import javax.swing.UIManager;

import network.NetworkClient;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class Main {
	/**
	 * 
	 */
	private static final String PATH_LOG4J = "/res/log4j.xml";
	private static final Logger logger = Logger.getLogger(Main.class);
	private static LoginGUI login;

	static {
		try {
			UIManager.setLookAndFeel("com.nilo.plaf.nimrod.NimRODLookAndFeel");
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			DOMConfigurator.configure(Main.class.getResource(PATH_LOG4J));
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Fichier des parametres de journalisation introuvable (/res/log4j.xml)");
			System.exit(-1);
		}
		logger.debug("Fichier des parametres de journalisation charge");
	}

	public static void main(String[] args) {
		NetworkClient.getInstance();
		if (args.length != 0) {
			Config.NAME = args[0];
			Config.PWD = args[1];
		}
		// lancement de la fenetre d'identification
		login = new LoginGUI();

		// hooks de fermeture propre
		Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {
			@Override
			public void run() {
				setName("Shutdown Hook");
				logger.info("Hook shutdown : Arrêt du réseau");
				NetworkClient.getInstance().interrupt();
			}
		});
	}
}
