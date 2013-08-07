package main;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import server.Server;

public class Main {
	private static final String PATH_LOG4J = "/res/log4j.xml";
	private static final Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		new Main().loadLogging();
		new Server().start();
	}

	public void loadLogging() {
		try {
			DOMConfigurator.configure(getClass().getResource(PATH_LOG4J));
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Fichier des parametres de journalisation introuvable (/res/log4j.xml)");
			System.exit(-1);
		}
		logger.debug("Fichier des parametres de journalisation charge");
	}
}
