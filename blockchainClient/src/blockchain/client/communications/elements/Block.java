package blockchain.client.communications.elements;

import java.sql.Timestamp;

import blockchain.client.utils.ByteUtils;
import blockchain.client.utils.ByteUtils.ByteConversionException;
import blockchain.client.utils.CryptoUtils;

public class Block implements Cloneable {
	
	public static int SIZE = 172;
	
	private int level;
	private byte [] predecessor;
	private long timestamp;
	private byte [] operationHash;
	private byte [] stateHash;
	private byte [] signature;
	
	public Block () {
		
	}
	
	public Block(int l, byte [] pred, long t, byte[] o, byte [] st, byte [] si) {
		this.level = l;
		this.predecessor = pred;
		this.timestamp = t;
		this.operationHash = o;
		this.stateHash = st;
		this.signature = si;
	}
	
	public Block(byte [] byteBlock) {
		// init byte arrays
		this.predecessor = new byte [32];
		this.operationHash = new byte [32];
		this.stateHash = new byte [32];
		this.signature = new byte [64];
		
		// fill values
		this.level = trimAndConvertToInt(byteBlock, 0); // first 4 bytes are for level
		System.arraycopy(byteBlock, 4, this.predecessor, 0, 32); // next 32 bytes are for predecessor
		this.timestamp = this.trimAndConvertToLong(byteBlock, 36); // next 8 bytes are for timestamp
		System.arraycopy(byteBlock, 44, this.operationHash, 0, 32); // next 32 bytes are for operation hash
		System.arraycopy(byteBlock, 76, this.stateHash, 0, 32); // next 32 bytes are for state hash
		System.arraycopy(byteBlock, 108, this.signature, 0, 32); // next 32 bytes are for signature
		// size if 4 + 32 + 8 + 32 + 32 + 64 = 172
	}
	
	private int trimAndConvertToInt(byte [] bytes, int start) {
		byte [] value = new byte[4];
		System.arraycopy(bytes, start, value, 0, 4); // 4 first byte for level
		try {
			return ByteUtils.bytesToInt(value);
		} catch (ByteConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	private long trimAndConvertToLong(byte [] bytes, int start) {
		byte [] value = new byte[8];
		System.arraycopy(bytes, start, value, 0, 8); // 4 first byte for level
		try {
			return ByteUtils.bytesToLong(value);
		} catch (ByteConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public byte[] getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(byte[] predecessor) {
		this.predecessor = predecessor;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public byte[] getOperationHash() {
		return operationHash;
	}

	public void setOperationHash(byte[] operationHash) {
		this.operationHash = operationHash;
	}

	public byte[] getStateHash() {
		return stateHash;
	}

	public void setStateHash(byte[] stateHash) {
		this.stateHash = stateHash;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
	@Override
	public String toString() {
		return String.format(	"level : %d\n" +
								"predecessor : %s\n"+
								"timestamp : %s\n"+
								"timestamp : %s\n"+
								"operation hash : %s\n" +
								"state hash : %s\n"+
								"signature : %s\n", 
								this.level, 
								CryptoUtils.toHexString(this.predecessor), 
								new Timestamp((this.timestamp * 1000)),
								this.timestamp,
								CryptoUtils.toHexString(this.operationHash), 
								CryptoUtils.toHexString(this.stateHash), 
								CryptoUtils.toHexString(this.signature));
	}
	
	@Override
	public Block clone() {
		Block clone = new Block();
		clone.level = this.level;
		clone.operationHash = new byte [this.operationHash.length];
		System.arraycopy(this.operationHash, 0, clone.operationHash, 0, clone.operationHash.length);
		clone.predecessor = new byte [this.predecessor.length];
		System.arraycopy(this.predecessor, 0, clone.predecessor, 0, clone.predecessor.length);
		clone.signature = new byte [this.predecessor.length];
		System.arraycopy(this.predecessor, 0, clone.predecessor, 0, clone.predecessor.length);
		clone.timestamp = this.timestamp;
		clone.stateHash = new byte [this.stateHash.length];
		System.arraycopy(this.stateHash, 0, clone.stateHash, 0, clone.stateHash.length);
		return clone;
	}

}
