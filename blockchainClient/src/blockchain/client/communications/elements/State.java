package blockchain.client.communications.elements;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import blockchain.client.utils.CryptoUtils;
import blockchain.client.utils.LogUtils;
import blockchain.client.utils.ArraysUtils;
import blockchain.client.utils.ByteUtils;
import blockchain.client.utils.ByteUtils.ByteConversionException;
import blockchain.client.utils.ArraysUtils.TrimArrayException;
import blockchain.client.utils.Constants;

public class State {

	public static int FIX_SIZE = 44;

	private byte[] dictatorPublicKey;
	private long righteLastTimeStamp;
	private Map<String, Account> accounts = new HashMap<>();

	public State(byte[] dpk, long rlts, Map<String, Account> a) {
		this.dictatorPublicKey = dpk;
		this.righteLastTimeStamp = rlts;
		this.accounts = a;
	}

	public State() {
	}

	public void init(byte[] values) {
		int pos = this.initDictatorPublicKey(values, 0);
		pos = this.initTimeStamp(values, pos);
	}

	public byte[] getDictatorPublicKey() {
		return dictatorPublicKey;
	}

	public String getDictatorPublicKeyAsString() {
		return CryptoUtils.toHexString(this.dictatorPublicKey);
	}

	public void setDictatorPublicKey(byte[] dictatorPublicKey) {
		this.dictatorPublicKey = dictatorPublicKey;
	}

	public long getRighteLastTimeStamp() {
		return righteLastTimeStamp;
	}

	public void setRighteLastTimeStamp(long righteLastTimeStamp) {
		this.righteLastTimeStamp = righteLastTimeStamp;
	}
	
	public void setRighteLastTimeStamp(byte [] righteLastTimeStamp) {
		try {
			this.righteLastTimeStamp = ByteUtils.bytesToLong(righteLastTimeStamp);
		} catch (ByteConversionException e) {
			LogUtils.printErrorMessage("impossible to ini rightValue");
			e.printStackTrace();
		}
	}

	public Map<String, Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Map<String, Account> accounts) {
		this.accounts = accounts;
	}

	private String accountsToString() {
		StringBuilder sb = new StringBuilder();
		for (Account a : this.accounts.values()) {
			sb.append(a.toString()).append("\n\n");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return String.format(
				"STATE : \n" + "dictator public key : %s\nright last time stamp : %s corresponding to : %s\n"
						+ "concurants : %s\n",
				this.getDictatorPublicKeyAsString(), new Timestamp((this.getRighteLastTimeStamp() * 1000)),
				this.getRighteLastTimeStamp(), this.accountsToString());
	}

	private int initDictatorPublicKey(byte[] values, int pos) {
		try {
			this.dictatorPublicKey = ArraysUtils.trim(values, pos, Constants.PUBLIC_KEY_SIZE);
		} catch (TrimArrayException e) {
			System.err.println("impossible to init dectotor public key");
			System.err.flush();
			e.printStackTrace();
		}
		return pos += Constants.PUBLIC_KEY_SIZE;
	}

	private int initTimeStamp(byte[] values, int pos) {
		try {
			this.righteLastTimeStamp = ByteUtils.bytesToLong(ArraysUtils.trim(values, pos, Constants.TIMESTAMP_SIZE));
		} catch (ByteConversionException | TrimArrayException e) {
			System.err.println("impossible to init timestamp");
			System.err.flush();
			e.printStackTrace();
		}
		return pos + Constants.TIMESTAMP_SIZE;
	}

}
