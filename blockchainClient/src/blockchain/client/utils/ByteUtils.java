package blockchain.client.utils;

import java.math.BigInteger;

public class ByteUtils {
	
	
	public static int bytesToInt(byte [] value, int size) throws ByteConversionException {
		if (value.length != size) {
			throw new ByteConversionException(String.format("impossible to convert byte [] to int, wrong size %d, with length %d", value.length, size));
		}
		int res = 0;
		for (int i = 0 ; i < value.length ; i ++) {
			int shift = (value.length - 1 - i)*8;
			int toAdd = (((int) (value[i] & 0xFF)) << shift);
			if (toAdd < 0) {
				System.err.println("going to negatives values in byte to int conversion + " + toAdd
						+ " when shifting byte by : " + shift);
			}
			res = res + toAdd;
		}
		return res;
	}
	
	// full int
	public static int bytesToInt(byte [] value) throws ByteConversionException{
		return ByteUtils.bytesToInt(value, 4);	
	}
	
	public static byte [] intToByteArray(int value) {
		return ByteUtils.intToByteArrayNLength(value, 4);
	}
	
	public static long bytesToLong(byte [] value) throws ByteConversionException{
		if (value.length != 8) {
			throw new ByteConversionException(String.format("impossible to convert byte [] to long, wrong size %d", value.length));
		}
		long res = 0;
		for (int i = 0 ; i < value.length ; i ++) {
			int shift = (value.length - 1 - i)*8;
			long toAdd = (((long) (value[i] & 0xFF)) << shift);
			if (toAdd < 0) {
				System.err.println("going to negatives values in byte to long conversion " + toAdd
						+ " when shifting byte by : " + shift);
			}
			res = res + toAdd;
		}
		return res;
	}
	
	public static BigInteger bytesToBigInteger (byte [] value) {
		return new BigInteger(value);
	}
	
	public static byte [] longToByteArray(long value) {
		byte [] res = new byte [8];
		for (int i = 0 ; i < res.length; i ++) {
			res[i] = (byte) ((value >> (res.length - 1 - i) * 8) & 0xFF);
		}
		return res;
	}
	
	public static byte [] intToByteArrayNLength(int value, int size) {
		// pay attention if v is bigger than short.max than it is trimed 
		byte [] res = new byte [size];
		for (int i = 0 ; i < res.length; i ++) {
			res[i] = (byte) ((value >> (res.length - 1 - i) * 8) & 0xFF);
		}
		return res;
	}
	
	public static class ByteConversionException extends Exception {
		/**
		 * generated
		 */
		private static final long serialVersionUID = 1L;

		public ByteConversionException (String message) {
			super(message);
		}
	}

}
