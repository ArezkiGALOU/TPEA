package blockchain.client.communications;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import blockchain.client.communications.elements.Account;
import blockchain.client.communications.elements.Block;
import blockchain.client.communications.elements.State;
import blockchain.client.communications.elements.operations.BadContextHashOperation;
import blockchain.client.communications.elements.operations.BadOperationHashOperation;
import blockchain.client.communications.elements.operations.BadPredecessorOperation;
import blockchain.client.communications.elements.operations.BadSignatureOperation;
import blockchain.client.communications.elements.operations.BadTimestampOperation;
import blockchain.client.communications.elements.operations.SignedOperation;
import blockchain.client.utils.ArraysUtils;
import blockchain.client.utils.ArraysUtils.TrimArrayException;
import blockchain.client.utils.ByteUtils;
import blockchain.client.utils.ByteUtils.ByteConversionException;
import blockchain.client.utils.Constants;
import blockchain.client.utils.CryptoUtils;
import blockchain.client.utils.CryptoUtils.HexByteArrayConvertException;
import blockchain.client.utils.LogUtils;
import blockchain.client.utils.MessageUtils;

public class Communication {

	public final static int SEED_SIZE = 24;
	private final static String SERVER_IP = "78.194.168.67";
	private final static int SERVER_PORT = 1337;

	private Map<Integer, Block> blocks = new HashMap<>();

	// currently working on
	private List<SignedOperation> operations = new ArrayList<>();
	private Block lastReadBlock;
	private byte [] lastReadBlockB;
	private State lastReadState;
	public Socket socket;

	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Future<?> currentRunningThread;

	/* ************************************ */
	/* body */
	/* ************************************ */
	public Communication() throws UnknownHostException, IOException, ConnectionException {
		this.socket = new Socket(SERVER_IP, SERVER_PORT);
		if (!this.authentification()) {
			throw new ConnectionException("not connected");
		}
		LogUtils.printMessage("Authentification succeeded\n\n\n");
		this.run();
	}

	public Communication(boolean runLifeCycle) throws UnknownHostException, IOException, ConnectionException {
		this.socket = new Socket(SERVER_IP, SERVER_PORT);
//		if (!this.authentification()) {
//			throw new ConnectionException("not connected");
//		}
		LogUtils.printMessage("Authentification succeeded\n\n\n");
		if (runLifeCycle) {
			this.run();
		}
	}

	public void close() throws IOException {
		this.socket.close();
	}

	public boolean isConnected() {
		return this.socket.isConnected();
	}

	public void printIsConnected() {
		System.out.println("client is connected : " + this.socket.isConnected());
	}

	/* ************************************ */
	/* life cycle */
	/* ************************************ */

	public void run() {
		LogUtils.printMessage("running life-cycle");
		try {
			this.getCurrentBlock();
			this.getCurrentState();
		} catch (IOException e) {
			System.err.println("impossible to get current at start of run");
			e.printStackTrace();
		}
		while (true) {
			if (this.lastReadBlock != null) {
				this.currentRunningThread = this.executor.submit(this::searchAndSendError);
			}
			try {
				this.receive();
				this.currentRunningThread.cancel(true);
				this.getCurrentState();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void searchAndSendError() {
		if (this.checkTimestamp()) return;
		if (this.checkSignature()) return;
		if (this.checkPredecessor()) return;
		if (this.checkOperationHash()) return;
		if (this.checkStateHash()) return;
	}

	/* ************************************ */
	/* check functions */
	/* ************************************ */

	public boolean checkTimestamp() {
		if (this.lastReadBlock.getTimestamp() - this.lastReadState.getRighteLastTimeStamp() < Constants.MIN_DURATION) {// in one sens means current.t
																						// must be > last.t
			try { // propose better ?
				this.sendMessage(MessageUtils.injectOperation(SignedOperation
						.createBadTimeStamp(this.lastReadState.getRighteLastTimeStamp() + Constants.MIN_DURATION + 1)));
			} catch (NoSuchAlgorithmException | IOException | HexByteArrayConvertException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public boolean checkPredecessor () {
		Block l = this.lastReadBlock;
		byte [] c = lastReadBlockB;
		byte [] b = null;
		try {
			this.sendMessage(MessageUtils.getBlock(this.lastReadBlock.getLevel() - 1));
			byte [] ll = this.receive();
			b = CryptoUtils.digestMessage(ArraysUtils.trim(ll, Constants.TAG_SIZE, ll.length));
		} catch (IOException | NoSuchAlgorithmException | TrimArrayException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean ret = false;
		if (!ArraysUtils.equalArrays(c, b)) {
			try {
				this.sendMessage(MessageUtils.injectOperation(SignedOperation
						.createBadPredecessor(b)));
			} catch (NoSuchAlgorithmException | IOException | HexByteArrayConvertException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ret = true;
		}
		this.lastReadBlock = l;
		this.lastReadBlockB = c;
		return ret;
	}
	
	public boolean checkOperationHash () {
		try {
			this.sendMessage(MessageUtils.getBlockOperation(this.lastReadBlock.getLevel()));
			this.receive();
			byte [] rightOpHash = this.hashOperationList(this.operations);
			return ArraysUtils.equalArrays(rightOpHash, this.lastReadBlock.getOperationHash());
		} catch (IOException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public byte [] hashOperationList(List<SignedOperation> sos) throws NoSuchAlgorithmException {
		if (sos == null || sos.isEmpty()) {
			return CryptoUtils.digestMessage(ArraysUtils.initArray(32, (byte) 0));
		}
		if (sos.size() == 1) {
			SignedOperation so = sos.get(0);
			return CryptoUtils.digestMessage(ArraysUtils.concatArray(so.getOperation().hashWithTag(), so.getPublicKey(), so.getSignature()));
		}
		SignedOperation so = sos.get(sos.size() - 1);
		byte [] hashOfLast = CryptoUtils.digestMessage(ArraysUtils.concatArray(so.getOperation().hashWithTag(), so.getPublicKey(), so.getSignature()));
		return CryptoUtils.digestMessage(ArraysUtils.concatArray(this.hashOperationList(sos.subList(0, sos.size() -1)), hashOfLast));
	}
	
	public boolean checkStateHash () {
		byte [] hash = null;
		try {
			this.sendMessage(MessageUtils.getState(this.lastReadBlock.getLevel()));
			byte [] st = this.receive();
			hash = CryptoUtils.digestMessage(ArraysUtils.trim(st, Constants.TAG_SIZE, st.length));
		}catch (IOException | NoSuchAlgorithmException | TrimArrayException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (! ArraysUtils.equalArrays(hash, this.lastReadBlock.getStateHash())) {		
			try {
				this.sendMessage(MessageUtils.injectOperation(SignedOperation.createBadStateHash(hash)));
				return true;
			} catch (NoSuchAlgorithmException | IOException | HexByteArrayConvertException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean checkSignature () {
		try {
			byte [] hash = CryptoUtils.digestMessage(ArraysUtils.trim(this.lastReadBlockB, Constants.TAG_SIZE, this.lastReadBlockB.length - Constants.SIGNATURE_SIZE));
			if (this.lastReadState == null) {
				this.sendMessage(MessageUtils.getState(this.lastReadBlock.getLevel()));
				this.receive();
			}
			return CryptoUtils.checkSig(hash, this.lastReadState.getDictatorPublicKey());
			
		} catch (NoSuchAlgorithmException | TrimArrayException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/* ************************************ */
	/* sending */
	/* ************************************ */

	public void sendMessage(String message) throws IOException {
		this.sendMessage(message.getBytes());
	}

	public void sendMessage(byte[] message) throws IOException {
		LogUtils.sendingMessage(message);
		this.socket.getOutputStream().write(message);
		this.socket.getOutputStream().flush();
	}

	/* ************************************ */
	/* receiving */
	/* ************************************ */

	public byte[] receive() throws IOException {
		try {
			int tag = this.readTag();
			LogUtils.debug("receive : read tag " + tag);
			switch (tag) {
			case Constants.CURRENT_HEAD:
				this.lastReadBlockB = this.readBlock(); // current head and block are the same ! with exception of the tag
				return this.lastReadBlockB;
			case Constants.BLOCK:
				this.lastReadBlockB = this.readBlock(); // current head and block are the same ! with exception of the tag
				return this.lastReadBlockB;
			case Constants.BLOCK_OPERATIONS:
				return this.readBlockOperations();
			case Constants.BLOCK_STATE:
				return this.readBlockState();
			default:
				System.err.println("nothing to do, tag : " + tag + " bleed remaining bytes to read");
				System.err.println("remaining : ");
				this.readAndPrintOnTheFly();
				System.err.println();
				System.err.flush();
				return null;
			}
		} catch (ByteConversionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] readMessage() throws IOException {
		try {
			int size = this.readSize();
			byte[] buff = this.socket.getInputStream().readNBytes(size);
			return buff;
		} catch (ByteConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public byte[] readAllRemaining() throws IOException {
		return this.socket.getInputStream().readAllBytes();
	}

	public byte[] readAllRemainingBytes() throws IOException {
		byte[] buff = new byte[4096];
		InputStream is = this.socket.getInputStream();
		int b = is.read();
		int i = 0;
		while (b != -1) {
			buff[i++] = (byte) b;
			b = is.read();
		}
		return buff;
	}

	public void readAndPrintOnTheFly() throws IOException {
		byte b = (byte) this.socket.getInputStream().read();
		int nbRead = 0;
		while (b != -1) {
			System.out.print("reading value ");
			System.out.print(b & 0xff);
			System.out.println();
			System.out.flush();
			b = (byte) this.socket.getInputStream().read();
			nbRead++;
		}

		LogUtils.printMessage("read and printed " + nbRead + "\n\n");
	}

	public byte[] readBlock() throws IOException {
		byte[] buff = this.socket.getInputStream().readNBytes(Constants.BLOCK_SIZE);
		Block b = new Block(buff);
		LogUtils.printMessage(b.toString());
		this.updateCurrent(b);
		return buff;
	}

	public byte[] readBlockOperations() throws IOException {
		byte[] operations = null;
		try {
			int sizeOfNextMessage = this.readSize();
			LogUtils.debug("readBlockOperations : size of next message is : " + sizeOfNextMessage);
			operations = this.socket.getInputStream().readNBytes(sizeOfNextMessage);
			LogUtils.debug("readBlockOperations : operations read");
			this.bytesToOperations(operations, 0);
			LogUtils.printMessage("OPERATIONS : ");
			LogUtils.printListFormat1(this.operations);
		} catch (ByteConversionException e) {
			LogUtils.printErrorMessage("impossible to read size due to wrong size short (2 byte integer)");
			e.printStackTrace();
		}
		return operations;
	}

	public byte[] readBlockState() throws IOException {
		byte[] pk = this.socket.getInputStream().readNBytes(Constants.PUBLIC_KEY_SIZE);
		byte[] tb = this.socket.getInputStream().readNBytes(Constants.TIMESTAMP_SIZE);
		byte[] ab = null;
		State s = new State();
		s.setDictatorPublicKey(pk);
		s.setRighteLastTimeStamp(tb);
		Map<String, Account> acc = new HashMap<>();
		try {
			int sizeOfNextMessage = this.readNByteToInt(Constants.INT_SIZE);
			LogUtils.debug("readBlockState : reading sizeOfNextMessage");
			ab = this.socket.getInputStream().readNBytes(sizeOfNextMessage);
			LogUtils.debug("readBlockState : sizeOfNextMessage : " + sizeOfNextMessage);
			int pos = 0;
			while (pos < sizeOfNextMessage) {
				Account ac = new Account();
				ac.init(ArraysUtils.trim(ab, pos, Constants.ACCOUNT_SIZE));
				acc.put(ac.getPublicKeyAsString(), ac);
				pos += Constants.ACCOUNT_SIZE;
			}
			s.setAccounts(acc);
		} catch (ByteConversionException e) {
			LogUtils.printErrorMessage("impossible to read size due to wrong size short (2 byte integer)");
			e.printStackTrace();
		} catch (TrimArrayException t) {
			LogUtils.printErrorMessage("impossible to read account due to trim array exception");
			t.printStackTrace();
		}
		this.lastReadState = s;
		LogUtils.printMessage(this.lastReadState.toString());
		return ArraysUtils.concatArray(pk, tb, ab);
	}

	public State readBlockStateAndGet() throws IOException {
		this.readAllRemaining();
		return this.lastReadState;
	}

	/* ************************************ */
	/* debug */
	/* ************************************ */

	public void justToTest2() throws NoSuchAlgorithmException {
		byte[] buf = new byte[SEED_SIZE];
		try {
			int read = this.socket.getInputStream().read(buf);
			System.err.println(read);
			for (int i = 0; i < SEED_SIZE; i++) {
				System.err.println(buf[i]);
			}
			byte[] hashed = CryptoUtils.digestMessage(buf);
			for (int i = 0; i < hashed.length; i++) {
				System.out.println(hashed[i]);
			}
			this.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* ************************************ */
	/* utility functions */
	/* ************************************ */

	private boolean authentification() {
		byte[] buf = new byte[SEED_SIZE];
		try {
			LogUtils.printMessage("authentification");
			int read = this.socket.getInputStream().read(buf);
			LogUtils.printMessage("reading seed");
			LogUtils.printMessage("seed read with size : " + read + " bytes");
			if (read != SEED_SIZE) {
				System.err.println("server giving wrong sized seed");
				System.err.println("SEED : " + CryptoUtils.toHexString(buf));
				System.err.println("size : " + read);
				System.err.flush();
			}
			byte[] hashed = CryptoUtils.digestMessage(buf);
			hashed = CryptoUtils.encode(hashed);
			byte[] publicKeyToSend = CryptoUtils.toByteArray("0020" + CryptoUtils.PUBLIC_KEY);
			byte[] hashedSized = this.addSize(hashed, (short) 64);
			this.socket.getOutputStream().write(publicKeyToSend);
			this.socket.getOutputStream().flush();
			this.socket.getOutputStream().write(hashedSized);
			this.socket.getOutputStream().flush();
			return this.socket.isConnected();
		} catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException | IOException
				| HexByteArrayConvertException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			// better handling
			e.printStackTrace();
		}
		return false;
	}

	private byte[] addSize(byte[] message, short size) {
		byte[] messageSized = new byte[message.length + 2];
		byte[] sizeArray = this.shortToByte(size);
		messageSized[0] = sizeArray[0];
		messageSized[1] = sizeArray[1];
		int i = 2;
		for (byte b : message) {
			messageSized[i++] = b;
		}
		return messageSized;
	}

	public byte[] readNByte(int n) throws IOException {
		return this.socket.getInputStream().readNBytes(n);
	}

	private int readSize() throws IOException, ByteConversionException {
		return this.readNByteToInt(Constants.SIZE_SIZE);
	}

	private int readTag() throws IOException, ByteConversionException {
		LogUtils.debug("readTag : reading tag");
		return this.readNByteToInt(Constants.TAG_SIZE);
	}

	private int readTag(byte[] values, int pos) throws ByteConversionException {
		byte[] tag = new byte[Constants.TAG_SIZE];
		System.arraycopy(values, pos, tag, 0, tag.length);
		return ByteUtils.bytesToInt(tag, Constants.TAG_SIZE);
	}

	private int readNByteToInt(int n) throws IOException, ByteConversionException {
		LogUtils.debug("readNByteToInt : reading " + n + " bytes");
		return ByteUtils.bytesToInt(this.socket.getInputStream().readNBytes(n), n);
	}

	private byte[] shortToByte(short size) {
		byte[] sizeAsArry = new byte[2];
		sizeAsArry[1] = (byte) (size & 0xff);
		sizeAsArry[0] = (byte) ((size >> 8) & 0xff);
		return sizeAsArry;
	}

	private void updateCurrent(Block b) {
		if (this.lastReadBlock != null) {
			this.blocks.put(this.lastReadBlock.getLevel(), this.lastReadBlock);
		}
		this.lastReadBlock = b;
	}

	private void getCurrentBlock() throws IOException {
		this.socket.getOutputStream().write(MessageUtils.GET_CURRENT_HEAD);
		this.receive();
	}

	public void getCurrentState() throws IOException {
		if (this.lastReadBlock != null) {
			this.getLevelsState(this.lastReadBlock.getLevel());
		}
	}

	public void getLevelsState(int level) throws IOException {
		this.socket.getOutputStream().write(MessageUtils.getState(level));
		this.receive();
	}

	private void bytesToOperations(byte[] values, int pos) {
		this.operations.clear();
		LogUtils.debug("bytesToOperations : starting at pos " + pos + " and array length  is : " + values.length);
		while (pos < values.length) {
			try {
				SignedOperation so = new SignedOperation();
				LogUtils.debug("bytesToOperations : reading tag");
				int tag = this.readTag(values, pos); // size of tag is integrated in in operation size
				LogUtils.debug("bytesToOperations : tag is : " + tag);
				switch (tag) {
				case Constants.BAD_PREDECESSOR:
					so.setOperation(new BadPredecessorOperation(
							ArraysUtils.trim(values, pos + Constants.TAG_SIZE, Constants.HASH_SIZE)));
					pos += Constants.BAD_PREDECESSOR_SIZE;
					pos = this.addPkAndSig(values, so, pos);
					this.operations.add(so);
					break;
				case Constants.BAD_TIMSTAMP:
					so.setOperation(new BadTimestampOperation(ByteUtils
							.bytesToLong(ArraysUtils.trim(values, pos + Constants.TAG_SIZE, Constants.LONG_SIZE))));
					pos += Constants.BAD_TIMESTAMP_SIZE;
					pos = this.addPkAndSig(values, so, pos);
					this.operations.add(so);
					break;
				case Constants.BAD_OPERATIONS_HASH:
					so.setOperation(new BadOperationHashOperation(
							ArraysUtils.trim(values, pos + Constants.TAG_SIZE, Constants.HASH_SIZE)));
					pos += Constants.BAD_OPERATION_HASH_SIZE;
					pos = this.addPkAndSig(values, so, pos);
					this.operations.add(so);
					break;
				case Constants.BAD_CONTEXT_HASH:
					so.setOperation(new BadContextHashOperation(
							ArraysUtils.trim(values, pos + Constants.TAG_SIZE, Constants.HASH_SIZE)));
					pos += Constants.BAD_CONTEXT_HASH_SIZE;
					pos = this.addPkAndSig(values, so, pos);
					this.operations.add(so);
					break;
				case Constants.BAD_SIGNATURE:
					so.setOperation(new BadSignatureOperation());
					pos += Constants.SIGNATURE_SIZE;
					pos = this.addPkAndSig(values, so, pos);
					this.operations.add(so);
					break;
				default:
					System.err.println("nothing to do, tag : " + tag + " bleed remaining bytes to read");
					System.err.println("remaining : ");
//					this.readAndPrintOnTheFly();
					LogUtils.printByteArrayAsHex(values);
					System.err.println();
					System.err.flush();
					return;
				}
			} catch (ByteConversionException | TrimArrayException e) {
				LogUtils.printErrorMessage("impossible to read next operation");
				e.printStackTrace();
			}
		}
	}

	private int addPkAndSig(byte[] values, SignedOperation so, int pos) {
		try {
			byte[] pk = ArraysUtils.trim(values, pos, Constants.PUBLIC_KEY_SIZE);
			byte[] sig = ArraysUtils.trim(values, pos + Constants.PUBLIC_KEY_SIZE, Constants.SIGNATURE_SIZE);
			so.setPublicKey(pk);
			so.setSignature(sig);
		} catch (TrimArrayException e) {
			LogUtils.printErrorMessage("enable to add public key and signature");
			e.printStackTrace();
		}
		return pos + (Constants.PUBLIC_KEY_SIZE + Constants.SIGNATURE_SIZE);
	}

	/* ************************** */
	/* getters setters */
	/* other getter and setter */
	/* may be elsewhere */
	/* no time to clean up code */
	/* ************************** */

	public Block getBlock() {
		return this.lastReadBlock;
	}

	public static class ConnectionException extends Exception {
		/**
		 * generated
		 */
		private static final long serialVersionUID = 1L;

		public ConnectionException(String message) {
			super(message);
		}
	}
}
