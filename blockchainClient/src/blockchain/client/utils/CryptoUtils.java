package blockchain.client.utils;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EdECPrivateKeySpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.NamedParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import com.rfksystems.blake2b.Blake2b;
import com.rfksystems.blake2b.security.Blake2bProvider;

public class CryptoUtils {
	
	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
	
	public static int HASH_SIZE = 32;
	
	public static String PUBLIC_KEY = "ab4843ca25d80d51e1d077102fc8335937ddcae8365084b51a85454bdf936011";
	public static String PRIVATE_KEY = "c95e359e4d0e69a82fbe3d400ea2d480c3155fd3557777f68f56a19d733dac29";
	private static PublicKey DICTATOR_P_KEY = null;
	private static Signature SIGNATURE = null;
	
	
	public static byte[] digestMessage(byte[] message) throws NoSuchAlgorithmException {
		Security.addProvider(new Blake2bProvider());
		MessageDigest hashFunction = MessageDigest.getInstance(Blake2b.BLAKE2_B_256);
		hashFunction.update(message, 0, message.length);
		byte[] out = hashFunction.digest();
		return out;
	}
	
	public static byte[] encode(byte[] message)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException, HexByteArrayConvertException {
		Security.addProvider(new Blake2bProvider());
		Signature sig = Signature.getInstance("Ed25519");
		KeyFactory kf = KeyFactory.getInstance("Ed25519");
		EdECPrivateKeySpec privateK = new EdECPrivateKeySpec(NamedParameterSpec.ED25519,toByteArray(PRIVATE_KEY));
		PrivateKey privateKey = kf.generatePrivate(privateK);
		sig.initSign(privateKey);
		sig.update(message);
		byte [] signed = sig.sign();
		return signed;
	}
	
	public static boolean checkSig(byte [] message, byte [] key) {
		if (CryptoUtils.SIGNATURE == null) {
			try {
				CryptoUtils.SIGNATURE = Signature.getInstance("Ed25519");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (DICTATOR_P_KEY == null) {
			KeyFactory kf;
			try {
				kf = KeyFactory.getInstance("Ed25519");
				EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key);
				CryptoUtils.DICTATOR_P_KEY = kf.generatePublic(publicKeySpec);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			return CryptoUtils.SIGNATURE.verify(message);
		} catch (SignatureException | NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public static String toHexString(byte [] bytes) {
		char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static byte [] toByteArray(String hex) throws HexByteArrayConvertException {
		if (hex.length() % 2 != 0) {
			throw new HexByteArrayConvertException("impossible to convert hex string to byte array odd number size ! " + 
						"\nsize : " + hex.length() + 
						"\nhex string : " + hex);
		}
		int len = hex.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
	                             + Character.digit(hex.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static class HexByteArrayConvertException extends Exception {
		
		/**
		 * generated
		 */
		private static final long serialVersionUID = 1L;

		public HexByteArrayConvertException (String message) {
			super(message);
		}
	}

}
