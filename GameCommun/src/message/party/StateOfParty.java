package message.party;



public enum StateOfParty {
	/**
	 * La partie est en attente d'un joueur.
	 */
	WAITING, 
	
	/**
	 * La partie est en cours.
	 */
	RUNNING,
	
	/**
	 * La partie est terminée.
	 */
	FINISHED;
}
