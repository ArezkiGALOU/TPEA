package blockchain.client.utils;

import blockchain.client.communications.elements.operations.AbstractOperation;
import blockchain.client.communications.elements.operations.BadContextHashOperation;
import blockchain.client.communications.elements.operations.BadOperationHashOperation;
import blockchain.client.communications.elements.operations.BadPredecessorOperation;
import blockchain.client.communications.elements.operations.BadSignatureOperation;
import blockchain.client.communications.elements.operations.BadTimestampOperation;
import blockchain.client.communications.elements.operations.SignedOperation;

public class MessageUtils {
	public static byte [] GET_CURRENT_HEAD = new byte [] {(byte) 0 , (byte) 2, (byte) 0, (byte) 1};
	
	public static byte [] getCurrentHead() {
		return MessageUtils.GET_CURRENT_HEAD;
	}
	
	public static byte [] getBlock(int level) {
		return MessageUtils.levelDependantMessages(level, Constants.GET_BLOCK);
	}
	
	public static byte [] getBlockOperation(int level) {
		return MessageUtils.levelDependantMessages(level, Constants.GET_BLOCK_OPERATIONS);
	}
	
	public static byte [] getState(int level) {
		return MessageUtils.levelDependantMessages(level, Constants.GET_BLOCK_STATE);
	}
	
	public static byte [] injectOperation(SignedOperation so) {
		// rush so not enough time to do better --> use instance of 
		AbstractOperation o = so.getOperation();
		int fixedPartSize = Constants.FIXED_PART_SIGNED_OPERATION_SIZE + Constants.TAG_SIZE;
		byte [] sizeAndTag = null;
		if (o instanceof BadPredecessorOperation) {
			sizeAndTag = MessageUtils.sizeAndTag(fixedPartSize + Constants.BAD_PREDECESSOR_SIZE, Constants.INJECT_OPERATION);
			byte [] oTag = ByteUtils.intToByteArrayNLength(Constants.BAD_PREDECESSOR, Constants.TAG_SIZE);
			byte [] ob = ArraysUtils.concatArray(oTag, ((BadPredecessorOperation) o).getHash());
			return ArraysUtils.concatArray(sizeAndTag, ob);
		}
		if (o instanceof BadTimestampOperation) {
			sizeAndTag = MessageUtils.sizeAndTag(fixedPartSize + Constants.BAD_TIMESTAMP_SIZE, Constants.INJECT_OPERATION);
			byte [] oTag = ByteUtils.intToByteArrayNLength(Constants.BAD_TIMESTAMP_SIZE, Constants.TAG_SIZE);
			byte [] ob = ArraysUtils.concatArray(oTag, ByteUtils.longToByteArray(((BadTimestampOperation) o).getTimestamp()));
			return ArraysUtils.concatArray(sizeAndTag, ob);
		}
		if (o instanceof BadOperationHashOperation) {
			sizeAndTag = MessageUtils.sizeAndTag(fixedPartSize + Constants.BAD_OPERATION_HASH_SIZE, Constants.INJECT_OPERATION);
			byte [] oTag = ByteUtils.intToByteArrayNLength(Constants.BAD_OPERATIONS_HASH, Constants.TAG_SIZE);
			byte [] ob = ArraysUtils.concatArray(oTag, ((BadOperationHashOperation) o).getHash());
			return ArraysUtils.concatArray(sizeAndTag, ob);
		}
		if (o instanceof BadContextHashOperation) {
			sizeAndTag = MessageUtils.sizeAndTag(fixedPartSize + Constants.BAD_CONTEXT_HASH_SIZE, Constants.INJECT_OPERATION);
			byte [] oTag = ByteUtils.intToByteArrayNLength(Constants.BAD_CONTEXT_HASH, Constants.TAG_SIZE);
			byte [] ob = ArraysUtils.concatArray(oTag, ((BadContextHashOperation) o).getHash());
			return ArraysUtils.concatArray(sizeAndTag, ob);
		}
		if (o instanceof BadSignatureOperation) {
			sizeAndTag = MessageUtils.sizeAndTag(fixedPartSize + Constants.BAD_SIGNATURE_SIZE, Constants.INJECT_OPERATION);
			byte [] oTag = ByteUtils.intToByteArrayNLength(Constants.BAD_SIGNATURE, Constants.TAG_SIZE);
			// BadSignatureOperation contains nothing so only tag
			return ArraysUtils.concatArray(sizeAndTag, oTag);
		}
		// impossible case ! 
		throw new IllegalArgumentException("signed operation has unknown operation");
		
	}
	
	public static byte [] levelDependantMessages(int level, int tag) {
		byte [] sizeAndTag = MessageUtils.sizeAndTag(Constants.GET_LEVEL_DEPENDANT_MESSAGE_SIZE, tag);
		byte [] levelB = ByteUtils.intToByteArray(level);
		LogUtils.debug("size and tag size : " + sizeAndTag.length);
		LogUtils.debug("level size : " + levelB.length);
		return ArraysUtils.concatArray(sizeAndTag, levelB);
	}
	
	public static byte [] sizeAndTag(int size, int tag) {
		byte [] sizeB = ByteUtils.intToByteArrayNLength(Constants.GET_LEVEL_DEPENDANT_MESSAGE_SIZE, Constants.SIZE_SIZE);
		byte [] tagB = ByteUtils.intToByteArrayNLength(tag, Constants.TAG_SIZE);
		return ArraysUtils.concatArray(sizeB, tagB);
	}
	


}
