package blockchain.client.main;

import java.io.IOException;
import java.net.UnknownHostException;

import blockchain.client.communications.Communication;
import blockchain.client.communications.Communication.ConnectionException;
import blockchain.client.communications.elements.Block;
import blockchain.client.utils.ByteUtils;
import blockchain.client.utils.CryptoUtils;
import blockchain.client.utils.CryptoUtils.HexByteArrayConvertException;
import blockchain.client.utils.ByteUtils.ByteConversionException;
import blockchain.client.utils.LogUtils;
import blockchain.client.utils.MessageUtils;

public class Main {
	
	public final static boolean DEBUG = false;

	public static void main(String... args) {
		run();
//		test();
//		test2();
//		run2();
//		anotherRun();
	}
	
	private static void test2() {
		byte b = (byte) 200;
		long s = (b & 0xFF);
		System.out.println("byte is " + b +"\nshort is "+ s);
	}

	private static void test() {
		try {
			System.out.println(ByteUtils.bytesToInt(new byte[] { (byte) 0, (byte) 1, (byte) 1, (byte) 0 }));
			LogUtils.printByteArrayAsHex(ByteUtils.intToByteArray(256));
			System.out.println(ByteUtils.bytesToLong(
					new byte[] { (byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 0 }));
			LogUtils.printByteArrayAsHex(ByteUtils.longToByteArray(256));
			LogUtils.printMessage("____________________________");
			System.out.println(ByteUtils.bytesToLong(CryptoUtils.toByteArray("e4aa000000006180")));
		} catch (ByteConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HexByteArrayConvertException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void run() {
		try {
			Communication c = new Communication();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void anotherRun() {
		try {
			Communication c = new Communication(false);
			c.sendMessage(MessageUtils.GET_CURRENT_HEAD);
			c.sendMessage(MessageUtils.getBlock(3000));
			c.socket.getInputStream().readNBytes(24);
			c.socket.getOutputStream().write(new byte [] {(byte) 1, (byte) 23, (byte) 122 , (byte) 4});
			c.socket.getOutputStream().flush();
			c.socket.getOutputStream().write(new byte [] {(byte) 1, (byte) 23, (byte) 122 , (byte) 4});
			c.socket.getOutputStream().flush();
			c.socket.getOutputStream().write(1);
			c.socket.getOutputStream().flush();
			int v = c.socket.getInputStream().read();
			System.out.println(v);
			v = c.socket.getInputStream().read();
			System.out.println(v);
			v = c.socket.getInputStream().read();
			System.out.println(v);
//			LogUtils.print_byte_buff(c.readNByte(2));
//			c.readAndPrintOnTheFly();
//			LogUtils.printByteArrayAsHex(c.receive());
//			c.readBlock();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void run2() {
		try {
			Communication c = new Communication(false);
			c.sendMessage(MessageUtils.GET_CURRENT_HEAD);
			c.receive();
			Block b = c.getBlock();
			c.sendMessage(MessageUtils.getBlockOperation(b.getLevel()));
			c.receive();
			c.sendMessage(MessageUtils.getState(b.getLevel()));
			c.receive();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
