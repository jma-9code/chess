package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import main.Config;
import message.ProfilePartiesList;
import message.TechnicalError;
import message.TechnicalError.Description;
import message.party.StateOfParty;

import org.apache.log4j.Logger;

import commun.ProfilePartyInfos;
import commun.ProfilePlayerInfos;

public class DataBase {
	
	public Connection conn = null;
	public String url = null;
	private static final Logger logger = Logger.getLogger(DataBase.class);
	private static DataBase instance = new DataBase(); 
	
	public static DataBase get(){
		return instance;
	}

	private DataBase() {
		try {
			Class.forName("org.sqlite.JDBC").newInstance();
			url = "jdbc:sqlite:" + Config.DB_BASE;
			logger.info("url : " + url);
			connect();
		} catch (final ClassNotFoundException e) {
			logger.error("JDBC :: Librairie absente");
			System.exit(-1);
		} catch (final Exception e) {
			logger.error("JDBC :: Erreur de connexion JDBC : " + e.getMessage().split("\n")[0]);
			System.exit(-1);
		}
	}
	
	private void connect() {
		try {
			if(conn != null) 
				conn.close();

			conn = DriverManager.getConnection(url);
			logger.info("BDD OK");
			
		} catch (final Exception e) {
			logger.fatal("JDBC :: Erreur de connexion JDBC : " + e.getMessage().split("\n")[0]);
			Config.DB_USE = false;
			System.exit(-1);
		}
	}
	
	private void controlConn() {
		try {
			if(conn.isClosed()) 
				connect();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConn() {
		return conn;
	}
		
	public String getCountryOfPlayer (String name) throws TechnicalError {
		final String sql = "SELECT country FROM players WHERE LOWER(name) = '"+name.toLowerCase()+"';";
		String country = null;
		try {
			controlConn();
			Statement stat = conn.createStatement();
			ResultSet res = stat.executeQuery (sql);
			logger.info("Fetching the country of player " + name);
			country = res.getString("country");
			res.close();
			return country;
		} catch (final SQLException e) {
			logger.error("SQL Exception while fetching the country of player " + name);
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}
	
	public String getHashPasswordOfPlayer (String name) throws TechnicalError {
		final String sql = "SELECT hashPassword FROM players WHERE LOWER(name) = '"+name.toLowerCase()+"';";
		String hashPassword = null;
		try {
			controlConn();
			Statement stat = conn.createStatement();
			ResultSet res = stat.executeQuery (sql);
			logger.info("Fetching the hashed password of player " + name);
			hashPassword = res.getString("hashPassword");
			res.close();
			return hashPassword;
		} catch (final SQLException e) {
			logger.error("SQL Exception while fetching the hashed password of player " + name);
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}
	
	public String getEmailOfPlayer(String name) throws TechnicalError {
		final String sql = "SELECT email FROM players WHERE LOWER(name) = '" + name.toLowerCase() + "';";
		String email;
		try {
			controlConn();
			Statement stat = conn.createStatement();
			ResultSet res = stat.executeQuery(sql);
			logger.info("Fetching the email of player " + name);
			email = res.getString("email");
			res.close();
			return email;
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while fetching the email of player " + name);
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public int getIdentifierOfPlayer(String name) throws TechnicalError {
		final String sql = "SELECT identifierOfPlayer FROM players WHERE LOWER(name) = '" + name.toLowerCase() + "';";
		int id;
		try {
			controlConn();
			Statement stat = conn.createStatement();
			ResultSet res = stat.executeQuery(sql);
			logger.info("Fetching the identifier of player " + name);
			id = res.getInt("identifierOfPlayer");
			res.close();
			return id;
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while fetching the identifier of player " + name);
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public String getSubscriptionDateOfPlayer(String name) throws TechnicalError {
		final String sql = "SELECT dateOfSubscription FROM players WHERE LOWER(name) = '" + name.toLowerCase() + "';";
		String date;
		try {
			controlConn();
			Statement stat = conn.createStatement();
			ResultSet res = stat.executeQuery(sql);
			logger.info("Fetching the date of subscription of player " + name);
			date = res.getString("dateOfSubscription");
			res.close();
			return date;
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while fetching the date of subscription of player " + name);
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public String getConnectionDateOfPlayer(String name) throws TechnicalError {
		final String sql = "SELECT dateOfLastConnection FROM players WHERE LOWER(name) = '" + name.toLowerCase() + "';";
		String date;
		try {
			controlConn();
			Statement stat = conn.createStatement();
			ResultSet res = stat.executeQuery(sql);
			logger.info("Fetching the last connection date of player " + name);
			date = res.getString("dateOfLastConnection");
			res.close();
			return date;
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while fetching the last connection date of player " + name);
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public void getPartiesOfPlayer(ProfilePartiesList profilePartiesList) throws TechnicalError {
		int id = profilePartiesList.getPlayerId();
		final String sql = "SELECT identifierOfBlackPlayer, identifierOfWhitePlayer, identifierOfParty, whoWins FROM parties WHERE identifierOfBlackPlayer = '"
				+ id + "' OR identifierOfWhitePlayer = '" + id + "' AND stateOfParty = '2' ORDER BY dateOfParty DESC;";
		ArrayList<ProfilePartyInfos> partiesList = new ArrayList<ProfilePartyInfos>();
		try {
			controlConn();
			Statement stat = conn.createStatement();
			ResultSet res = stat.executeQuery(sql);
			logger.info("Fetching the parties list");
			while (res.next()) {
				String whitePlayerName = getPlayerName(res.getInt("identifierOfWhitePlayer"));
				String blackPlayerName = getPlayerName(res.getInt("identifierOfBlackPlayer"));
				ProfilePlayerInfos infosWinner = null;
				if (res.getInt("whoWins") != -1) {
					String winner = getPlayerName(res.getInt("whoWins"));
					infosWinner = new ProfilePlayerInfos(winner, getCountryOfPlayer(winner), getScoreOfPlayer(winner));
				}
				else {
					infosWinner = new ProfilePlayerInfos("Partie nulle", "", 0);
				}
				partiesList.add(new ProfilePartyInfos(new ProfilePlayerInfos(whitePlayerName, getCountryOfPlayer(whitePlayerName),
						getScoreOfPlayer(whitePlayerName)), new ProfilePlayerInfos(blackPlayerName, getCountryOfPlayer(blackPlayerName),
						getScoreOfPlayer(blackPlayerName)), infosWinner, res.getInt("identifierOfParty")));
			}
			profilePartiesList.setPartiesList(partiesList);
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while fetching the parties list");
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public String getPlayerName(int id) throws TechnicalError {
		final String sql = "SELECT name FROM players WHERE identifierOfPlayer = '" + id + "';";
		String name;
		try {
			controlConn();
			Statement stat = conn.createStatement();
			ResultSet res = stat.executeQuery(sql);
			logger.info("Fetching the name of player id " + id);
			name = res.getString("name");
			res.close();
			return name;
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while fetching the name of player id " + id);
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public int getScoreOfPlayer(String name) throws TechnicalError {
		final String sql = "SELECT score FROM players WHERE LOWER(name) = '"+name.toLowerCase()+"';";
		int score;
		try {
			controlConn();
			Statement stat = conn.createStatement();
			ResultSet res = stat.executeQuery (sql);
			logger.info("Fetching the score of player " + name);
			score = res.getInt("score");
			res.close();
			return score;
		} catch (final SQLException e) {
			logger.error("SQL Exception while fetching the score of player " + name);
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}
	
	public void updateScoreOfPlayer(String name, int newScore) throws TechnicalError {
		final String sql = "UPDATE players " + "SET score=" + newScore + " WHERE name='" + name + "';";
		try {
			controlConn();
			Statement stat = conn.createStatement();
			stat.executeUpdate(sql);
			logger.info("Updating the score of the player whose name is " + name + ".");
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while updating the score of the player whose name is " + name + ".");
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public void updatePasswordOfPlayer(String name, String hashPassword) throws TechnicalError {
		final String sql = "UPDATE players " + "SET hashPassword='" + hashPassword + "' WHERE name='" + name + "';";
		try {
			controlConn();
			Statement stat = conn.createStatement();
			stat.executeUpdate(sql);
			logger.info("Updating the password of the player whose name is " + name + ".");
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while updating the password of the player whose name is " + name + ".");
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public void updateMailOfPlayer(String name, String email) throws TechnicalError {
		final String sql = "UPDATE players " + "SET email='" + email + "' WHERE name='" + name + "';";
		try {
			controlConn();
			Statement stat = conn.createStatement();
			stat.executeUpdate(sql);
			logger.info("Updating the email of the player whose name is " + name + ".");
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while updating the email of the player whose name is " + name + ".");
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public void updateCountryOfPlayer(String name, String country) throws TechnicalError {
		final String sql = "UPDATE players " + "SET country='" + country + "' WHERE name='" + name + "';";
		try {
			controlConn();
			Statement stat = conn.createStatement();
			stat.executeUpdate(sql);
			logger.info("Updating the country of the player whose name is " + name + ".");
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while updating the country of the player whose name is " + name + ".");
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public void updateDateOfLastConnection(String name, String date) throws TechnicalError {
		final String sql = "UPDATE players " + "SET dateOfLastConnection='" + date + "' WHERE name='" + name + "';";
		try {
			controlConn();
			Statement stat = conn.createStatement();
			stat.executeUpdate(sql);
			logger.info("Updating the date of last connection of the player whose name is " + name + ".");
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while updating the date of last connection of the player whose name is " + name + ".");
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public boolean nameExists(String name) throws TechnicalError {
		final String sql = "SELECT * FROM players WHERE LOWER(name) = '"+name.toLowerCase()+"';";
		boolean exists = false;
		try {
			controlConn();
			Statement stat = conn.createStatement();
			ResultSet res = stat.executeQuery (sql);
			exists = res.next() ? true : false;
			res.close();
			logger.info("Searching if the name " + name + " exists.");
			return exists;
		} catch (final SQLException e) {
			logger.error("SQL Exception while searching if the name " + name
 + " exists.");
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}
	
	public void addNewPlayer(String name, String hashPassword, String country, String email, String date)
			throws TechnicalError {
		final String sql = "INSERT INTO players (name, hashPassword, country, email, score, dateOfSubscription) VALUES " + "('" + name.toLowerCase()
				+ "', '" + hashPassword + "', '" + country.toUpperCase() + "', '" + email + "', 1000, '" + date
				+ "');";
		try {
			controlConn();
			Statement stat = conn.createStatement();
			stat.executeUpdate (sql);
			logger.info("Adding a new player : " + name);
		} catch (final SQLException e) {
			logger.error("SQL Exception while adding a new player " + name
 + ".");
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}
	
	public void addNewParty(String nameOfBlackPlayer, String nameOfWhitePlayer, StateOfParty stateOfParty)
			throws TechnicalError {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateNow = sdf.format(new Date());
		int identifierOfBlackPlayer = getIdentifierOfPlayer(nameOfBlackPlayer);
		int identifierOfWhitePlayer = getIdentifierOfPlayer(nameOfWhitePlayer);
		final String sql = "INSERT INTO parties (identifierOfBlackPlayer, identifierOfWhitePlayer, stateOfParty, whoWins, dateOfParty, serialParty) VALUES "
				+ "(" + identifierOfBlackPlayer + ", " + identifierOfWhitePlayer + ", " + stateOfParty.ordinal() + ", -1, '" + dateNow + "', NULL);";
		try {
			controlConn();
			Statement stat = conn.createStatement();
			stat.executeUpdate (sql);
			logger.info("Adding a new party.");
		} catch (final SQLException e) {
			logger.error("SQL Exception while adding a new party.");
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}
	
	public int getIdOfParty(String nameOfBlackPlayer, String nameOfWhitePlayer) throws TechnicalError {
		int identifierOfBlackPlayer = getIdentifierOfPlayer(nameOfBlackPlayer);
		int identifierOfWhitePlayer = getIdentifierOfPlayer(nameOfWhitePlayer);

		final String sql = "SELECT identifierOfParty FROM parties " + "WHERE identifierOfWhitePlayer = " + identifierOfWhitePlayer
				+ " AND identifierOfBlackPlayer = " + identifierOfBlackPlayer + "" + " AND stateOfParty = " + StateOfParty.RUNNING.ordinal() + ";";
		int identifierOfParty;
		try {
			controlConn();
			Statement stat = conn.createStatement();
			ResultSet res = stat.executeQuery(sql);
			logger.info("Fetching the id of a running party.");
			identifierOfParty = res.getInt("identifierOfParty");
			res.close();
			return identifierOfParty;
		}
		catch (final SQLException e) {
			logger.error("SQL Exception while fetching the id of a running party.");
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}

	public static String getHexString(byte[] b) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		}
		return result.toString();
	}

	public void updateParty(int identifierOfParty, StateOfParty stateOfParty, String whoWins, byte[] serialParty) throws TechnicalError {
		int identifierOfWhoWins;
		if (whoWins == null)
			identifierOfWhoWins = -1;
		else
			identifierOfWhoWins = getIdentifierOfPlayer(whoWins);

		final String sql = "UPDATE parties " +
 "SET stateOfParty=" + stateOfParty.ordinal() + ", " + "whoWins = " + identifierOfWhoWins + ", "
				+ "serialParty='"
 + getHexString(serialParty)
 + "' " +
 "WHERE identifierOfParty=" + identifierOfParty + ";";

		try {
			controlConn();
			Statement stat = conn.createStatement();
			stat.executeUpdate (sql);
			logger.info("Updating the state of the party whose identifier is "
 + identifierOfParty + ".");
		} catch (final SQLException e) {
			logger.error("SQL Exception while updating the state of the party whose identifier is "
 + identifierOfParty + ".");
			throw new TechnicalError(Description.DB_EXCEPTION, e.getMessage());
		}
	}
}
