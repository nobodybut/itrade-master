package com.itrade.common.infrastructure.util.enums;

public enum RequestFromEnum {

	/**
	 * 系统发起
	 */
	SYSTEM(0),

	/**
	 * PC端 web
	 */
	WEB_PC(1),

	/**
	 * iphone 手机 web
	 */
	WEB_IOS_PHONE(2),

	/**
	 * ipad 平板 web
	 */
	WEB_IOS_PAD(3),

	/**
	 * android 手机 web
	 */
	WEB_ANDROID_PHONE(4),

	/**
	 * android 平板 web
	 */
	WEB_ANDROID_PAD(5),

	/**
	 * windows mobile 手机 web
	 */
	WEB_WINMOBILE_PHONE(6),

	/**
	 * windows mobile 平板 web
	 */
	WEB_WINMOBILE_PAD(7),

	/**
	 * iphone 手机 app
	 */
	APP_IOS_PHONE(8),

	/**
	 * ipad 平板 app
	 */
	APP_IOS_PAD(9),

	/**
	 * android 手机 app
	 */
	APP_ANDROID_PHONE(10),

	/**
	 * android 平板 app
	 */
	APP_ANDROID_PAD(11),

	/**
	 * windows mobile 手机 app
	 */
	APP_WINMOBILE_PHONE(12),

	/**
	 * windows mobile 平板 app
	 */
	APP_WINMOBILE_PAD(13);

	// 构造函数
	RequestFromEnum(int id) {
		this.id = id;
	}

	private int id;

	public int getId() {
		return id;
	}
}
