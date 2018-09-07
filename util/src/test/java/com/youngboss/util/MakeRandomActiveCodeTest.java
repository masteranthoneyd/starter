package com.youngboss.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * @author ybd
 * @date 18-8-24
 * @contact yangbingdong1994@gmail.com
 */
public class MakeRandomActiveCodeTest {

	private static final Snowflake[] SNOWFLAKES = IntStream.rangeClosed(1, 8)
														   .mapToObj(Snowflake::create)
														   .toArray(value -> new Snowflake[8]);

	private static final AtomicLong ATOMIC_LONG = new AtomicLong(0);

	public static long getId() {
		long l = SNOWFLAKES[(int) (ATOMIC_LONG.incrementAndGet() & (1 << 3) - 1)].nextId();
		System.out.println(l);
		return l;
	}

	public static void randomSleep() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int time = random.nextInt(0, 20);
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 采用URL Base64字符，即把“+/”换成“-_”
	 */
	static private char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_=".toCharArray();

	/**
	 * Base64 编码
	 * @param data
	 * @return
	 */
	private static char[] encode(byte[] data) {
		char[] out = new char[((data.length + 2) / 3) * 4];
		boolean quad, trip;
		for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
			quad = trip = false;
			int val = (0xFF & (int) data[i]);
			val <<= 8;
			if ((i + 1) < data.length) {
				val |= (0xFF & (int) data[i + 1]);
				trip = true;
			}
			val <<= 8;
			if ((i + 2) < data.length) {
				val |= (0xFF & (int) data[i + 2]);
				quad = true;
			}
			out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 1] = alphabet[val & 0x3F];
			val >>= 6;
			out[index] = alphabet[val & 0x3F];
		}
		return out;
	}

	/**
	 * 转成字节
	 * @return
	 * @param uuid
	 */
	private static byte[] toBytes(UUID uuid) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		byte[] buffer = new byte[16];

		for (int i = 0; i < 8; i++) {
			buffer[i] = (byte) ((msb >>> 8 * (7 - i)) & 0xFF);
			buffer[i + 8] = (byte) ((lsb >>> 8 * (7 - i)) & 0xFF);
		}
		return buffer;
	}

	public static String getUUID(UUID uuid) {
		char[] res = encode(toBytes(uuid));
		return new String(res, 0, res.length - 2);
	}

	public static void main(String[] args) {
		UUID uuid1 = UUID.randomUUID();

		long s1 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			System.out.println(getUUID(uuid1));
		}
		long e1 = System.currentTimeMillis();
		System.out.println(e1-s1);

	}

}
