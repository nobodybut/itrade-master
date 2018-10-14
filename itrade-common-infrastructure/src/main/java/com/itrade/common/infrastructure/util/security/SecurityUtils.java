package com.itrade.common.infrastructure.util.security;

import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;

public class SecurityUtils {

	public static ScriptEngineManager factory = new ScriptEngineManager();

	/**
	 * UTF-8编码
	 *
	 * @param str
	 * @return
	 */
	public static String encode_utf8(String str) {
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return str;
	}

	/**
	 * UTF-8解码  //IllegalArgumentException
	 *
	 * @param str
	 * @return
	 */
	public static String decode_utf8(String str) {
		try {
			return URLDecoder.decode(str, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	}

	/**
	 * GB2312编码
	 *
	 * @param str
	 * @return
	 */
	public static String encode_gb2312(String str) {
		try {
			return URLEncoder.encode(str, "gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return str;
	}

	/**
	 * GB2312解码
	 *
	 * @param str
	 * @return
	 */
	public static String decode_gb2312(String str) {
		try {
			return URLDecoder.decode(str, "gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return str;
	}

	/**
	 * unicode 解码（用于解码PHP json_encode）
	 *
	 * @param str
	 * @return
	 */
	public static String decode_unescapeJava(String str) {
		try {
			return StringEscapeUtils.unescapeJava(str);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return str;
	}

	/**
	 * 关键字 UTF8 编码
	 *
	 * @param keyword
	 * @return
	 */
	public static String keyword_encode_utf8(String keyword) {
		return encode_utf8(keyword.replace("-", " ").replace("/", " ").replace("&amp;", "").replace("&", ""));
	}

	/**
	 * 对应javascript的escape()函数, 加码后的串可直接使用javascript的unescape()进行解码
	 *
	 * @param src
	 * @return
	 */
	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);
		for (i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16)
					tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	/**
	 * 调用javascript的escape()函数, 对字符串进行编码
	 *
	 * @param src
	 * @return
	 */
	public static String javascript_escape(String src) {
		try {
			ScriptEngine engine = factory.getEngineByName("nashorn");
			return (String) engine.eval("escape('" + src + "')");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return src;
	}

	/**
	 * 调用javascript的eval()函数, 执行js代码
	 *
	 * @param src
	 * @return
	 */
	public static String javascript_eval(String src) {
		try {
			if (!Strings.isNullOrEmpty(src)) {
				ScriptEngine engine = factory.getEngineByName("nashorn");
				return engine.eval(src).toString();
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 调用javascript的unescape()函数, 对字符串进行解码
	 *
	 * @param src
	 * @return
	 */
	public static String javascript_unescape(String src) {
		try {
			ScriptEngine engine = factory.getEngineByName("nashorn");
			return (String) engine.eval("unescape('" + src + "')");
		} catch (ScriptException e) {

		}
		return src;
	}

	/**
	 * 对应javascript的unescape()函数, 可对javascript的escape()进行解码
	 *
	 * @param src
	 * @return
	 */
	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}

	/**
	 * 对传入字符进行base64位编码
	 *
	 * @param data
	 * @return
	 */
	public static String encodeBase64(String data) {
		return encodeBase64(data, "utf-8");
	}

	/**
	 * 对传入字符进行base64位编码
	 *
	 * @param data
	 * @param charsetName
	 * @return
	 */
	public static String encodeBase64(String data, String charsetName) {
		try {
			return new String(new Base64().encode(data.getBytes()), charsetName);
		} catch (UnsupportedEncodingException ex) {

		}

		return "";
	}

	/**
	 * 对传入字符进行base64位解码
	 *
	 * @param data
	 * @return
	 */
	public static String decodeBase64(String data) {
		return decodeBase64(data, "utf-8");
	}

	/**
	 * 对传入字符进行base64位解码
	 *
	 * @param data
	 * @param charsetName
	 * @return
	 */
	public static String decodeBase64(String data, String charsetName) {
		try {
			return new String(new Base64().decode(data.getBytes()), charsetName);
		} catch (UnsupportedEncodingException ex) {

		}

		return "";
	}

	/**
	 * 过滤掉超过3个字节的UTF8字符
	 *
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String filterOffUtf8Mb4(String text) {
		try {
			byte[] bytes = text.getBytes("utf-8");
			ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
			int i = 0;
			while (i < bytes.length) {
				short b = bytes[i];
				if (b > 0) {
					buffer.put(bytes[i++]);
					continue;
				}

				b += 256; // 去掉符号位

				if (((b >> 5) ^ 0x6) == 0) {
					buffer.put(bytes, i, 2);
					i += 2;
				} else if (((b >> 4) ^ 0xE) == 0) {
					buffer.put(bytes, i, 3);
					i += 3;
				} else if (((b >> 3) ^ 0x1E) == 0) {
					i += 4;
				} else if (((b >> 2) ^ 0x3E) == 0) {
					i += 5;
				} else if (((b >> 1) ^ 0x7E) == 0) {
					i += 6;
				} else {
					buffer.put(bytes[i++]);
				}
			}
			buffer.flip();
			return new String(buffer.array(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return text;
	}
}
