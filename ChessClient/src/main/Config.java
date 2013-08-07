package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Config {

	/**
	 * Mode debug permettant d'accéder à des fonctionnalités pour les
	 * développeurs afin de lancer leurs tests
	 */
	public static final boolean DEBUG = false;

	/** Lecture de la taille de l'écran */
	public static final Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();

	/**
	 * NB ligne du tableau
	 */
	public static int BOARD_SIZE = 8;

	/**
	 * Taille graphique d'une piece
	 */
	public static int CHESSMEN_SIZE = 50;

	public static int CHESSMEN_TAKED_SIZE = 25;

	/**
	 * Identifiant du joueur
	 */
	public static String NAME = "test";

	/**
	 * Mot de passe du joueur
	 */
	public static String PWD = "test";

	/**
	 * Adresse du serveur de jeu
	 */
	public static String ADRESS_SERV = "178.170.101.60";
	// public static String ADRESS_SERV = "127.0.0.1";

	/**
	 * Temps en secondes d'un cycle d'essai de reconnexion
	 */
	public static final long RETRY_CONNECTION_CYCLE = 5;

	/**
	 * Temps en millisecondes de timeout pour la connexion de la socket
	 */
	public static final int TIMEOUT_CONNECTION = 1000;

	public static int PARTY_TIME = 600;

	// Personalisation
	public static Color BOARD_SQUARE_SELECT = Color.CYAN;
	public static Color BOARD_POSSIBILITY = Color.GREEN;
	public static Color BOARD_CHECK = Color.RED;
	public static Color BOARD_CHECKMATE = Color.BLACK;
	public static Color BOARD_SQUARE_BLACK = new Color(209, 138, 71);
	public static Color BOARD_SQUARE_WHITE = new Color(248, 233, 159);
	// 0,5s par mouvement
	public static int MOVE_RAPIDITY = 1000;
	public static int CHESSMEN_STYLE = 10;
}