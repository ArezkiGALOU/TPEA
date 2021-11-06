package blockchain.client.communications.elements.operations;

import blockchain.client.utils.ByteUtils;
import blockchain.client.utils.Constants;

public class BadSignatureOperation extends AbstractOperation {
	
	public final static int BAD_SIGNATURE_SIZE = Constants.TAG_SIZE;

	// as is, nothing to add 
	public BadSignatureOperation() {}

	@Override
	public byte[] asByteArray() {
		return ByteUtils.intToByteArrayNLength(BAD_SIGNATURE, Constants.TAG_SIZE);
	}
	
	public byte [] hashWithTag() {
		return ByteUtils.intToByteArrayNLength(Constants.BAD_SIGNATURE, Constants.TAG_SIZE);
	}
}
