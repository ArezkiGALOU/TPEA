package blockchain.client.communications.elements.operations;

import blockchain.client.utils.ArraysUtils;
import blockchain.client.utils.ByteUtils;
import blockchain.client.utils.Constants;
import blockchain.client.utils.CryptoUtils;

public class BadPredecessorOperation extends AbstractOperation {

	public final static int BAD_PREDECESSOR_SIZE = 34;

	private byte[] hash;

	public BadPredecessorOperation(byte[] hash) {
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
		byte [] tag = ByteUtils.intToByteArrayNLength(Constants.BAD_PREDECESSOR, Constants.TAG_SIZE);
		return ArraysUtils.concatArray(tag, this.hash);
	}

	@Override
	public String toString() {
		return String.format("BAD PREDECESSOR OPERATION\nhash : %s\n", this.getHashAsString());
	}

	@Override
	public byte[] asByteArray() {
		return ArraysUtils.concatArray(ByteUtils.intToByteArrayNLength(BAD_PREDECESSOR, Constants.TAG_SIZE), this.hash);
	}
}
