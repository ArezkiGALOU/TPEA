package blockchain.client.utils;

public class ArraysUtils {
	
	public static byte [] trim(byte [] originalArray , int startPos, int length) throws TrimArrayException {	
		if (originalArray == null) {
			throw new TrimArrayException("original array is null");
		}
		if (originalArray.length < (startPos + length)) {
			throw new TrimArrayException(String.format("impossible to get an array of size %s "
														+ " starting from position %d "
														+ " from an array of size : %d",
														length, 
														startPos, 
														originalArray.length));
		}
		byte [] res = new byte [length];
		System.arraycopy(originalArray, startPos, res, 0, length);
		return res;
	}
	
	
	public static byte [] concatArray (byte [] ... bytes) {
		LogUtils.debug("concatArray with an array of size" + bytes.length);
		int totalLength = 0;
		for (byte [] tab : bytes) {
			if (tab != null) {
				totalLength += tab.length;
			}
		}
		LogUtils.debug("total elements : " + totalLength);
		byte [] res = new byte [totalLength];
		int pos = 0;
		for (byte [] tab : bytes){
			if (tab != null) {
				for (byte v : tab) {
					res[pos++] = v;
				}
			}
		}
		return res;
	}
	
	public static byte [] initArray(int size, byte b) {
		byte [] bytes = new byte[size];
		for (int i = 0 ; i < size ; i ++) {
			bytes[i] = b;
		}
		return bytes;
	}
	
	public static boolean equalArrays(byte [] b1, byte [] b2) { // 2 nulls aren't equal !!
		if (b1 == null || b2 == null || b1.length != b2.length) {
			return false;
		}
		for (int i = 0 ; i < b1.length ; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}
	
	public static class TrimArrayException extends Exception {
		/**
		 * generated
		 */
		private static final long serialVersionUID = 1L;

		public TrimArrayException (String message) {
			super(message);
		}
	}
}
