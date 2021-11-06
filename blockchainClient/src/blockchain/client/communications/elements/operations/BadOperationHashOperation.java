package blockchain.client.communications.elements.operations;

import blockchain.client.utils.ArraysUtils;
import blockchain.client.utils.ByteUtils;
import blockchain.client.utils.Constants;
import blockchain.client.utils.CryptoUtils;

public class BadOperationHashOperation extends AbstractOperation { // same thing as BadPredecessor didn't see it at begining	
	
	public final static int BAD_OPERATION_HASH_SIZE = 34;
	private byte [] hash;

	public BadOperationHashOperation(byte [] hash) {
		this.hash = hash;
	}

	public byte[] getHash() {
		return hash;
	}
	
	public String getHashAsString() {
		return CryptoUtils.toHexString(this.hash);
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}
	
	public byte [] hashWithTag() {
		byte [] tag = ByteUtils.intToByteArrayNLength(Constants.BAD_OPERATIONS_HASH, Constants.TAG_SIZE);
		return ArraysUtils.concatArray(tag, this.hash);
	}
	
	@Override
	public String toString() {
		return String.format("%BAD HASH OPERATION\nhash : %s\n", this.getHashAsString());
	}

	@Override
	public byte[] asByteArray() {
		return ArraysUtils.concatArray(ByteUtils.intToByteArrayNLength(BAD_OPERATION_HASH_SIZE, Constants.TAG_SIZE), this.hash);
	}
}
