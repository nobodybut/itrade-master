package com.trade.common.infrastructure.util.mail;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Properties;

public class MailSenderInfo {

	/** =============== field =============== */
	/**
	 * 发送邮件的服务器IP
	 */
	private String mailServerHost = "smtp.jindouyun.com";

	/**
	 * 发送邮件的服务器端口
	 */
	private String mailServerPort = "25";

	/**
	 * 是否需要身份验证
	 */
	private boolean validate = true;

	/**
	 * 登陆邮件发送服务器用户名
	 */
	private String userName;

	/**
	 * 登陆邮件发送服务器密码
	 */
	private String password;

	/**
	 * 邮件发送者的地址
	 */
	private String fromAddress;

	/**
	 * 邮件接收者的地址
	 */
	private String toAddress;

	/**
	 * 邮件主题
	 */
	private String subject;

	/**
	 * 邮件的文本内容
	 */
	private String content;

	/**
	 * 邮件附件的文件名
	 */
	private String[] attachFileNames;

	/** =============== readonly method =============== */
	/**
	 * 获得邮件会话属性
	 */
	public Properties getProperties() {
		Properties p = new Properties();
		p.put("mail.smtp.host", this.mailServerHost);
		p.put("mail.smtp.port", this.mailServerPort);
		p.put("mail.smtp.auth", validate ? "true" : "false");
		return p;
	}

	/**
	 * =============== get/set ===============
	 */
	public String getMailServerHost() {
		return mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort() {
		return mailServerPort;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String[] getAttachFileNames() {
		return attachFileNames;
	}

	public void setAttachFileNames(String[] attachFileNames) {
		this.attachFileNames = attachFileNames;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}