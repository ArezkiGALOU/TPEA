package blockchain.client.communications.elements.operations;

import java.sql.Timestamp;

import blockchain.client.utils.ArraysUtils;
import blockchain.client.utils.ByteUtils;
import blockchain.client.utils.Constants;

public class BadTimestampOperation extends AbstractOperation {

	public final static int BAD_TIMESTAMP_SIZE = 10;

	private long timestamp;

	public BadTimestampOperation(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return String.format("BAD TIMESTAMP OPERATION\ntimestamp : %s which is %s",
				new Timestamp((this.timestamp * 1000)), this.timestamp);
	}
	
	public byte [] hashWithTag() {
		byte [] tag = ByteUtils.intToByteArrayNLength(Constants.BAD_TIMSTAMP, Constants.TAG_SIZE);
		byte [] ts = ByteUtils.longToByteArray(this.timestamp);
		return ArraysUtils.concatArray(tag, ts);
	}

	@Override
	public byte[] asByteArray() {
		return ArraysUtils.concatArray(ByteUtils.intToByteArrayNLength(BAD_TIMSTAMP, Constants.TAG_SIZE),
				ByteUtils.longToByteArray(this.timestamp));
	}

}
