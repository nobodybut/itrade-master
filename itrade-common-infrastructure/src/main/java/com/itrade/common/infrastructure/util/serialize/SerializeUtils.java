package com.itrade.common.infrastructure.util.serialize;

import java.io.*;

public class SerializeUtils {

	/**
	 * 序列化
	 *
	 * @param value
	 * @return
	 */
	public static byte[] serialize(Object value) {
		if (value == null) {
			return null;
		}
		byte[] rv = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream os = null;
		try {
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			os.writeObject(value);
			os.close();
			bos.close();
			rv = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(os);
			close(bos);
		}
		return rv;
	}

	/**
	 * 反序列化
	 *
	 * @param in
	 * @return
	 */
	public static Object deserialize(byte[] in) {
		return deserialize(in, Object.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] in, Class<T> requiredType) {
		Object rv = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream is = null;
		try {
			if (in != null) {
				bis = new ByteArrayInputStream(in);
				is = new ObjectInputStream(bis);
				rv = is.readObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(is);
			close(bis);
		}
		return (T) rv;
	}

	private static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
