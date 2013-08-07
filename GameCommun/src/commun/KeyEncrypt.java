package commun;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Objet caractérisant une clé de chiffrement utilisée pour chiffrer une donnée
 * afin qu'elle soit transférable sur le réseau en sécurité.
 * 
 * @author Floww
 */
public class KeyEncrypt implements Serializable {
	private static final long serialVersionUID = -6276776751695441344L;

	/**
	 * Clé de chiffrement
	 */
	private final String key;

	/**
	 * Constructeur de clé de chiffrement
	 */
	public KeyEncrypt(final String _key) {
		key = _key;
	}

	/**
	 * Constructeur depuis une taille
	 */
	public KeyEncrypt(final int size) {
		key = genererKey(size);
	}

	/**
	 * Taille de la clé
	 * 
	 * @return
	 */
	public int length() {
		return key.length();
	}

	/**
	 * @see String#charAt(int)
	 */
	public char charAt(final int offset) {
		return key.charAt(offset);
	}

	/**
	 * Génére une clé
	 * 
	 * @param size
	 *            Taille de la cle
	 */
	private String genererKey(final int size) {
		// 33 => 126 Borne ascii (126 - 33 = 93)
		final StringBuffer key_buffer = new StringBuffer();
		final Random r = new Random();

		for (int i = 0; i < size; i++) {
			key_buffer.append((char) (r.nextInt(93) + 33));
		}

		return key_buffer.toString();
	}

	/**
	 * Chiffre la chaine
	 * 
	 * @param str
	 *            Chaine chiffrer
	 * @return Chaine chiffree
	 */
	public static String encrypt(KeyEncrypt key, String str) {
		return XOR(key, str);
	}

	/**
	 * Déchiffre la chaine à partir d'une clé
	 * 
	 * @param key
	 *            Clé
	 * @return Chaine déchiffrée
	 */
	public static String decrypt(KeyEncrypt key, String str) {
		return XOR(key, str);
	}

	/**
	 * Algorithme de chiffrement/déchiffrement type XOR
	 * 
	 * @param key
	 *            Clé à utiliser
	 * @param str
	 *            Chaine à traiter
	 * @return Chaine claire/chiffrée
	 */
	private static String XOR(KeyEncrypt key, String str) {
		if (str == null)
			return null;
		StringBuffer str_buffer = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			final int code = str.charAt(i) ^ key.charAt(i % key.length());
			str_buffer.append((char) code);
		}
		return str_buffer.toString();
	}

	public static String sha1(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");

			md.update(text.getBytes("UTF-8"), 0, text.length());

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
		return key;
	}
}
