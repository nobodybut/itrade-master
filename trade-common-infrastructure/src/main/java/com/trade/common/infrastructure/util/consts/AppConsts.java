package com.trade.common.infrastructure.util.consts;

import java.util.HashMap;
import java.util.Map;

public class AppConsts {

	// web item
	public static final String DISPLAY_NONE = " style=\"display:none\"";
	public static final String DISPLAY_BLOCK = " style=\"display:block\"";

	public static final String RADIO_CHECKED = " checked=\"checked\"";
	public static final String STYLE_READONLY = " readonly=\"true\"";

	// handler view name
	public static final String VIEW_RESTFULHANDLER = "handler/restfulHandler";

	// HttpHeader
	public static final Map<String, String> baiduapisHttpHeader = new HashMap<String, String>() {
		{
			put("apikey", "58961920edaa8dab3e15cfef1e9e8b10");
		}
	};
}
