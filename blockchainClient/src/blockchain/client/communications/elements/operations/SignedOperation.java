package blockchain.client.communications.elements.operations;

import java.security.NoSuchAlgorithmException;

import blockchain.client.utils.ArraysUtils;
import blockchain.client.utils.ArraysUtils.TrimArrayException;
import blockchain.client.utils.Constants;
import blockchain.client.utils.CryptoUtils;
import blockchain.client.utils.CryptoUtils.HexByteArrayConvertException;
import blockchain.client.utils.LogUtils;

public class SignedOperation {

	private AbstractOperation operation;
	private byte[] publicKey;
	private byte[] signature;

	public SignedOperation(AbstractOperation operation, byte[] publicKey, byte[] signature) {
		this.operation = operation;
		this.publicKey = publicKey;
		this.signature = signature;
	}
	
	public static SignedOperation createBadTimeStamp(long timestamp) throws HexByteArrayConvertException, NoSuchAlgorithmException {
		SignedOperation so = new SignedOperation();
		so.setOperation(new BadTimestampOperation(timestamp));
		so.setPublicKey(CryptoUtils.toByteArray(CryptoUtils.PUBLIC_KEY));
		so.setSignature(CryptoUtils.digestMessage(ArraysUtils.concatArray(so.getOperation().asByteArray(), so.getPublicKey())));		
		return so;
	}
	
	public static SignedOperation createBadPredecessor(byte[] hash) throws HexByteArrayConvertException, NoSuchAlgorithmException {
		SignedOperation so = new SignedOperation();
		so.setOperation(new BadPredecessorOperation(hash));
		so.setPublicKey(CryptoUtils.toByteArray(CryptoUtils.PUBLIC_KEY));
		so.setSignature(CryptoUtils.digestMessage(ArraysUtils.concatArray(so.getOperation().asByteArray(), so.getPublicKey())));		
		return so;
	}
	
	public static SignedOperation createBadStateHash(byte[] hash) throws HexByteArrayConvertException, NoSuchAlgorithmException {
		SignedOperation so = new SignedOperation();
		so.setOperation(new BadContextHashOperation(hash));
		so.setPublicKey(CryptoUtils.toByteArray(CryptoUtils.PUBLIC_KEY));
		so.setSignature(CryptoUtils.digestMessage(ArraysUtils.concatArray(so.getOperation().asByteArray(), so.getPublicKey())));		
		return so;
	}

	public SignedOperation() {

	}

	public int initPublicKey(byte[] values, int pos) {
		try {
			this.publicKey = ArraysUtils.trim(values, pos, Constants.PUBLIC_KEY_SIZE);
		} catch (TrimArrayException e) {
			LogUtils.printErrorMessage("impossible to init public key value");
			e.printStackTrace();
		}
		return pos + Constants.PUBLIC_KEY_SIZE;
	}

	public int initSignature(byte[] values, int pos) {
		try {
			this.signature = ArraysUtils.trim(values, pos, Constants.SIGNATURE_SIZE);
		} catch (TrimArrayException e) {
			LogUtils.printErrorMessage("impossible to init public signature");
			e.printStackTrace();
		}
		return pos + Constants.SIGNATURE_SIZE;
	}

	public AbstractOperation getOperation() {
		return operation;
	}

	public void setOperation(AbstractOperation operation) {
		this.operation = operation;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	@Override
	public String toString() {
		return String.format("SIGNED OPERATION\noperation : %s\npublic key : %s\nsignature : %s\n",
				this.operation.toString(), this.getPublicKeyAsHex(), this.getSignatureAsHex());
	}

	private String getPublicKeyAsHex() {
		return CryptoUtils.toHexString(this.publicKey);
	}

	private String getSignatureAsHex() {
		return CryptoUtils.toHexString(this.signature);
	}
}
