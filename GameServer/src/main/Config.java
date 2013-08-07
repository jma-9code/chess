package main;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Config {
	
	private static final Logger logger = Logger.getLogger(Config.class);
	public static int SOCKET_CONNECTION_TIMEOUT = 60000;
	public static int TCP_PORT = 5776;
	public static boolean DB_USE = true;
	public static String DB_PATH = "../";
	public static String DB_BASE = "db_echecs.db";
	public static int BAN_MAX_ERREUR = 3;

	/**
	 * Initialisation static de la classe. Permet de charger les attribus classe
	 * e partir du fichier Config. En cas d'erreur on laisse les parametres par
	 * defaut.
	 */
	static {
		FileInputStream configFile = null;
		try {
			configFile = new FileInputStream("config.properties");
			Properties props = new Properties();
			props.load(configFile);
			configFile.close();
			TCP_PORT = props.getProperty("TCP_PORT") != null ? Integer.parseInt(props.getProperty("TCP_PORT")) : TCP_PORT;
			DB_PATH = props.getProperty("DB_PATH") != null ? props.getProperty("DB_PATH") : DB_PATH;
			DB_BASE = props.getProperty("DB_BASE") != null ? props.getProperty("DB_BASE") : DB_BASE;
		}
		catch (Exception e) {
			logger.error("Lancement de la configuration par defaut");
		}
	}
}
