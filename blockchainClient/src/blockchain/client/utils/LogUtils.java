package blockchain.client.utils;

import java.util.List;

import blockchain.client.main.Main;

public class LogUtils {
	
	private LogUtils() {
		// empty private constructor
	}
	
	public static void sendingMessage(byte [] buff) {
		System.out.print("sending message : ");
		LogUtils.print_byte_buff(buff);
	}
	
	public static void print_byte_buff(byte [] buff) {
		System.out.println("print byte array length : " + buff.length);
		for(byte b : buff) {
			System.out.print(b + " ");
		}
		System.out.println();
		System.out.flush();
	}
	
	public static void print_message(String message) {
		System.out.println(message);
		System.out.flush();
	}
	
	public static void printByteArrayAsHex(byte [] byteArray) {
		System.out.println(CryptoUtils.toHexString(byteArray));
		System.out.flush();
	}
	
	public static void printMessage(String message) {
		System.out.println(message);
		System.out.flush();
	}
	
	public static void printMessage(Object message) {
		System.out.println(message.toString());
		System.out.flush();
	}
	
	public static void printErrorMessage(String message) {
		System.err.println(message);
		System.err.flush();
	}
	
	public static <T> void printListFormat1(List<T> list) {
		System.out.println("[\n");
		list.forEach(System.out::println);
		System.out.println("]\n");
		System.out.flush();
	}
	
	public static void debug (String message) {
		if (Main.DEBUG) {
			LogUtils.printMessage(message);
		}
	}
	
	public static void debug (Object message) {
		if (Main.DEBUG) {
			LogUtils.printMessage(message);
		}
	}

}
