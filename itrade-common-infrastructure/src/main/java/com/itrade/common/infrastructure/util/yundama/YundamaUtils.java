package com.itrade.common.infrastructure.util.yundama;

import com.google.common.base.Strings;
import com.itrade.common.infrastructure.util.collection.KeyValuePair;
import com.itrade.common.infrastructure.util.consts.GlobalConsts;
import com.itrade.common.infrastructure.util.date.TimerUtils;
import com.itrade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.itrade.common.infrastructure.util.httpclient.OkHttpClientUtils;
import com.itrade.common.infrastructure.util.math.CustomNumberUtils;
import com.itrade.common.infrastructure.util.string.CustomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class YundamaUtils {

	// 日志记录
	private static final Logger s_logger = LoggerFactory.getLogger(YundamaUtils.class);

	public static final String YUNDAMA_URL = "http://api.yundama.com/api.php";
	public static final String YUNDAMA_RESULT_URL = "http://api.yundama.com/api.php?cid=%s&method=result";

	public static final String username = "dolphin_86";
	public static final String password = "19860918";
	public static final String appid = "1";
	public static final String appkey = "22cc5376925e9387a23cf797cb9ba745";

	/**
	 * 1004 4位英文数字 10题分
	 * 1006 6位英文数字 15题分
	 * 2004 4位纯汉字 40题分
	 * 4006 6位纯数字 15题分
	 * 6200 九宫格 25题分
	 * 6300 加减乘除计算题 14题分
	 * 6400 4组汉字4选1 10题分
	 * 6701 8图多选 20题分
	 * 5000 不定长汉字英文数字符号 3题分一个英文或符号，10题分一个汉字
	 */
	public static final String codetype = "5000";


	//上传图片查看验证码
	public static final String POST_DATA_UPLOAD = "username=%s&password=%s&codetype=%s&appid=%s&appkey=%s&timeout=60&method=upload";

	//验证码错误报错
	public static final String POST_DATA_REPORT = "username=%s&password=%s&appid=%s&appkey=%s&cid=%s&flag=%s&method=report";

	//查询余额
	public static final String POST_DATA_BALANCE = "username=%s&password=%s&appid=%s&appkey=%s&method=balance";

	/**
	 * 确认余额信息
	 *
	 * @return
	 */
	public static boolean checkBalance() {
		String postData = String.format(POST_DATA_BALANCE, username, password, appid, appkey);
		String html = HttpClientUtils.getHTML(YUNDAMA_URL, postData, GlobalConsts.UTF_8_STR, 15000);

		int balance = CustomNumberUtils.toInt(CustomStringUtils.substringBetween(html, "balance\":", "}"));
		if (balance > 10) {
			return true;
		}
		return false;
	}

	/**
	 * 报告结果是否正确
	 *
	 * @return
	 */
	public static void report(String cid, String flag) {
		String postData = String.format(POST_DATA_REPORT, username, password, appid, appkey, cid, flag);
		HttpClientUtils.getHTML(YUNDAMA_URL, postData, GlobalConsts.UTF_8_STR, 15000);
	}

	/**
	 * 获取结果
	 *
	 * @return
	 */
	public static String getResult(String cid) {
		String html = HttpClientUtils.getHTML(String.format(YUNDAMA_RESULT_URL, cid), "", GlobalConsts.UTF_8_STR, 15000);

		return CustomStringUtils.substringBetween(html, "text\":\"", "\"");
	}

	public static KeyValuePair<String, String> getIdentifyCode(String validCodeImgUrl) {
		byte[] fileBytes = OkHttpClientUtils.downloadFileBytes(validCodeImgUrl);
		return calIdentifyCode(fileBytes);
	}

	public static String getIdentifyCodeValue(String validCodeImgUrl) {
		KeyValuePair<String, String> resultKV = getIdentifyCode(validCodeImgUrl);
		if (resultKV != null) {
			return resultKV.getValue();
		} else {
			return "";
		}
	}

	public static KeyValuePair<String, String> calIdentifyCode(byte[] fileByte) {
		String postData = String.format(POST_DATA_UPLOAD, username, password, codetype, appid, appkey);
		String html = HttpClientUtils.getHTML(YUNDAMA_URL, postData, "utf-8", 30000, "file", fileByte, "image/jpeg", "upload.jpeg");

		String code = CustomStringUtils.substringBetween(html, "text\":\"", "\"");
		String cid = CustomStringUtils.substringBetween(html, "cid\":", ",");

		//如果未出结果,需要循环调用result接口
		while (Strings.isNullOrEmpty(code)) {

			code = getResult(cid);
			if (!Strings.isNullOrEmpty(code)) {
				break;
			}

			TimerUtils.sleep(500);
		}

		return new KeyValuePair<>(cid, code);
	}


	public static void main(String[] args) throws IOException {
		byte[] fileBytes = OkHttpClientUtils.downloadFileBytes("https://accounts.douban.com/misc/captcha?id=v6ZIBIThvQXUmZzLnGBWakyo:en&size=s");

		KeyValuePair<String, String> codePair = calIdentifyCode(fileBytes);

		int length = 0;
	}
}
