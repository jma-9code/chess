package message.party;



public enum StateOfPlayer {
	/**
	 * Le joueur est actuellement en attente.
	 */
	WAITING, 
	
	/**
	 * Le joueur est en train de jouer.
	 */
	INGAME, 
	
	/**
	 * Le joueur a gagné.
	 */
	WIN, 
	
	/**
	 * Le joueur a perdu.
	 */
	LOSE, 
	
	/**
	 * Le joueur est parti.
	 */
	LEFT,

	/**
	 * La partie est nulle.
	 */
	NULL;
}
