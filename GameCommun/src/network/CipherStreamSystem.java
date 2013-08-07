package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

public class CipherStreamSystem {
	private static final Logger logger = Logger.getLogger(CipherStreamSystem.class);
	public final static int DH_KEY_SIZE = 1024;

	private IvParameterSpec ivParameters = new IvParameterSpec(new byte[] { 12, 34, 56, 78, 90, 87, 65, 43, 34, 23, 12,
			23, 98, 01, 23, 74 });

	private int keySize = 128;
	private String algorithm = "AES";
	private String transformation = "";
	private SecretKey key;

	public CipherStreamSystem(String algorithm, int keySize) {
		this.keySize = keySize;
		this.algorithm = algorithm;
		this.transformation = algorithm + "/CFB8/NoPadding";

		/*
		 * Basically your data can only be written out in chunks of 16 bytes
		 * because you're using a block cipher like ECB or CBC. You can avoid
		 * this problem by using a stream cipher. See block ciphers modes of
		 * operation for details. You'll need to select CFB, OFB or CTR. E.g.
		 * when you get your Cipher instance:
		 * 
		 * Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
		 * 
		 * Although, I used "AES/CFB8/NoPadding"
		 */
	}

	public CipherStreamSystem(String algorithm, SecretKey key) {
		this.algorithm = algorithm;
		this.transformation = algorithm + "/CFB8/NoPadding";
		this.key = key;
	}

	/**
	 * Retourne un flux d'entrée chiffré
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public CipherInputStream getInputStream(InputStream is) throws Exception {
		Cipher decryptCipher = Cipher.getInstance(transformation);
		decryptCipher.init(Cipher.DECRYPT_MODE, key, ivParameters);

		BufferedInputStream bis = new BufferedInputStream(is);
		return new CipherInputStream(bis, decryptCipher);
	}

	/**
	 * Retourne un flux de sortie chiffré
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public CipherOutputStream getOutputSteam(OutputStream os) throws Exception {
		Cipher encryptCipher = Cipher.getInstance(transformation);
		encryptCipher.init(Cipher.ENCRYPT_MODE, key, ivParameters);

		BufferedOutputStream bos = new BufferedOutputStream(os);
		return new CipherOutputStream(bos, encryptCipher);
	}

	/**
	 * Processus d'échange des clés
	 * 
	 * @param socket
	 * @throws Exception
	 */
	public void shareKeys(Socket socket) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());

			// Send the public key
			KeyPair myKeyPair = generateDHKey();
			byte[] myPublicKeyBytes = myKeyPair.getPublic().getEncoded();
			logger.debug("Envoi d'une clé de " + myPublicKeyBytes.length);
			out.writeInt(myPublicKeyBytes.length);
			out.flush();

			logger.debug("Clé envoyé : " + myPublicKeyBytes);
			out.write(myPublicKeyBytes);
			out.flush();

			// Receive a public key.
			int keySize = in.readInt();
			byte[] itsPublicKeyBytes = new byte[keySize];
			logger.debug("Lecture d'une clé de " + keySize + " bits");

			itsPublicKeyBytes = new byte[keySize];
			in.readFully(itsPublicKeyBytes, 0, keySize);
			logger.debug("Clé reçu : " + itsPublicKeyBytes);

			KeyFactory kf = KeyFactory.getInstance("DH");
			X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(itsPublicKeyBytes);
			PublicKey itsPublicKey = kf.generatePublic(x509Spec);

			buildSharedKey(myKeyPair, itsPublicKey, algorithm);

			logger.debug("Partage des clés terminé.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Génération des paramètres liées à la spécification de l'algorithme
	 * Diffie-Hellman
	 * 
	 * @throws InvalidParameterSpecException
	 * @throws NoSuchAlgorithmException
	 */
	private DHParameterSpec generateDHParamSpec() throws InvalidParameterSpecException, NoSuchAlgorithmException {
		// Create the parameter generator for a 1024-bit DH key pair
		AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
		paramGen.init(DH_KEY_SIZE, new SecureRandom());

		// Generate the parameters
		AlgorithmParameters params = paramGen.generateParameters();
		return (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
	}

	/**
	 * Génération de la paire de de clé DH
	 * 
	 * @return
	 * @throws InvalidParameterSpecException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 */
	private KeyPair generateDHKey() throws InvalidParameterSpecException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException {

		final BigInteger p = new BigInteger(
				"f460d489678f7ec903293517e9193fd156c821b3e2b027c644eb96aedc85a54c971468cea07df15e9ecda0e2ca062161add38b9aa8aefcbd7ac18cd05a6bfb1147aaa516a6df694ee2cb5164607c618df7c65e75e274ff49632c34ce18da534ee32cfc42279e0f4c29101e89033130058d7f77744dddaca541094f19c394d485",
				16);

		final BigInteger g = new BigInteger(
				"9ce2e29b2be0ebfd7b3c58cfb0ee4e9004e65367c069f358effaf2a8e334891d20ff158111f54b50244d682b720f964c4d6234079d480fcc2ce66e0fa3edeb642b0700cd62c4c02a483c92d2361e41a23706332bd3a8aaed07fe53bba376cefbce12fa46265ad5ea5210a3d96f5260f7b6f29588f61a4798e40bdc75bbb2b457",
				16);

		DHParameterSpec dhSpec = new DHParameterSpec(p, g, 1024);

		// Create a Diffie-Hellman key pair.
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
		kpg.initialize(dhSpec);

		// Generation of the pair
		return kpg.genKeyPair();
	}

	/**
	 * Génération de la clé privée
	 * 
	 * @param keyPair
	 * @param itsPublicKey
	 * @param algorithm
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	private void buildSharedKey(KeyPair keyPair, PublicKey itsPublicKey, String string) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException {

		// Generate the secret value.
		KeyAgreement ka = KeyAgreement.getInstance("DH");
		ka.init(keyPair.getPrivate());
		ka.doPhase(itsPublicKey, true);

		byte[] sharedSecret = ka.generateSecret();
		this.key = new SecretKeySpec(sharedSecret, 0, keySize / 8, algorithm);
	}

	@Override
	protected void finalize() throws Throwable {
		this.algorithm = null;
		this.transformation = null;
		this.key = null;
		this.ivParameters = null;

		super.finalize();
	}

}
