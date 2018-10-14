package com.itrade.common.infrastructure.util.string;

public class CustomStringBuilder {

	final String separator = System.getProperty("line.separator");

	private StringBuilder builder = new StringBuilder();

	public CustomStringBuilder appendLine(String str) {
		builder.append(str + separator);
		return this;
	}

	public CustomStringBuilder append(Object obj) {
		builder.append(String.valueOf(obj));
		return this;
	}

	public CustomStringBuilder append(String str) {
		builder.append(str);
		return this;
	}

	public String toString() {
		return this.builder.toString();
	}
}
