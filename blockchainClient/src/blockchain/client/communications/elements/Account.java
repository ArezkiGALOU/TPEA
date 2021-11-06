package blockchain.client.communications.elements;

import blockchain.client.utils.ArraysUtils;
import blockchain.client.utils.ByteUtils;
import blockchain.client.utils.ByteUtils.ByteConversionException;
import blockchain.client.utils.Constants;
import blockchain.client.utils.CryptoUtils;
import blockchain.client.utils.LogUtils;
import blockchain.client.utils.ArraysUtils.TrimArrayException;

public class Account {
	
	public static int SIZE = 52;
	
	private byte [] publicKey;
	private int predecessorPez;
	private int timeStampPez;
	private int operationHashPez;
	private int contextHashPez;
	private int signaturePez;
	
	public Account(byte[] publicKey, int predecessorPez, int timeStampPez, int operationHashPez, int contextHashPez,
			int signaturePez) {
		this.publicKey = publicKey;
		this.predecessorPez = predecessorPez;
		this.timeStampPez = timeStampPez;
		this.operationHashPez = operationHashPez;
		this.contextHashPez = contextHashPez;
		this.signaturePez = signaturePez;
	}
	
	public Account() {}
	
	public void init (byte [] values) {
		int pos = this.initPublicKey(values, 0);
		pos = this.initPredecessorPez(values, pos);
		pos = this.initTimeStampPez(values, pos);
		pos = this.initOperationHashPez(values, pos);
		pos = this.initContextHashPez(values, pos);
		this.initSignaturePez(values, pos);
	}

	public byte[] getPublicKey() {
		return publicKey;
	}
	
	public String getPublicKeyAsString() {
		return CryptoUtils.toHexString(this.publicKey);
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public int getPredecessorPez() {
		return predecessorPez;
	}

	public void setPredecessorPez(int predecessorPez) {
		this.predecessorPez = predecessorPez;
	}

	public int getTimeStampPez() {
		return timeStampPez;
	}

	public void setTimeStampPez(int timeStampPez) {
		this.timeStampPez = timeStampPez;
	}

	public int getOperationHashPez() {
		return operationHashPez;
	}

	public void setOperationHashPez(int operationHashPez) {
		this.operationHashPez = operationHashPez;
	}

	public int getContextHashPez() {
		return contextHashPez;
	}

	public void setContextHashPez(int contextHashPez) {
		this.contextHashPez = contextHashPez;
	}

	public int getSignaturePez() {
		return signaturePez;
	}

	public void setSignaturePez(int signaturePez) {
		this.signaturePez = signaturePez;
	}
	
	private int initPublicKey(byte values[], int pos) {
		try {
			this.publicKey = ArraysUtils.trim(values, pos, Constants.PUBLIC_KEY_SIZE);
		} catch (TrimArrayException e) {
			LogUtils.printErrorMessage("impossible to init public key value");
			e.printStackTrace();
		}
		return pos + Constants.PUBLIC_KEY_SIZE;
	}
	
	private int initPredecessorPez(byte values[], int pos) {
		Integer v = this.initIntValue(values, pos, "impossible to init predecessor pez");
		if (v != null) {
			this.predecessorPez = v;
		}
		return pos + Constants.INT_SIZE;
	}
	
	private int initTimeStampPez(byte values[], int pos) {
		Integer v = this.initIntValue(values, pos, "impossible to init timestamp pez");
		if (v != null) {
			this.predecessorPez = v;
		}
		return pos + Constants.INT_SIZE;
	}
	
	private int initOperationHashPez(byte values[], int pos) {
		Integer v = this.initIntValue(values, pos, "impossible to init operation hash pez");
		if (v != null) {
			this.predecessorPez = v;
		}
		return pos + Constants.INT_SIZE;
	}
	
	private int initContextHashPez(byte values[], int pos) {
		Integer v = this.initIntValue(values, pos, "impossible to init context hash pez");
		if (v != null) {
			this.predecessorPez = v;
		}
		return pos + Constants.INT_SIZE;
	}
	
	private int initSignaturePez(byte values[], int pos) {
		Integer v = this.initIntValue(values, pos, "impossible to init signature pez");
		if (v != null) {
			this.predecessorPez = v;
		}
		return pos + Constants.INT_SIZE;
	}
	
	@Override
	public String toString () {
		return String.format("ACCOUNT:\n"
							+ "public key : %s\n"
							+ "predecessor pez : %d\n"
							+ "timestamp pez : %d\n"
							+ "operation hash pez : %d\n"
							+ "context hash pez : %d\n"
							+ "signature pez : %d\n"
							, this.getPublicKeyAsString()
							, this.predecessorPez
							, this.timeStampPez
							, this.operationHashPez
							, this.contextHashPez
							, this.signaturePez);
	}
	
	
	private Integer initIntValue (byte values [], int pos, String errorMessage ) {// print error message if exception where thrown
		try {
			return ByteUtils.bytesToInt(ArraysUtils.trim(values, pos, Constants.INT_SIZE));
		} catch (TrimArrayException | ByteConversionException e) {
			LogUtils.printErrorMessage(errorMessage);
			e.printStackTrace();
		}
		return null;
	}

}
