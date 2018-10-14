package com.itrade.common.infrastructure.util.net;

import com.itrade.common.infrastructure.util.timeout.AbstractTimeoutMethod;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;

public class TelnetConnect extends AbstractTimeoutMethod {

	private String proxyServer;
	private int timeOut;

	public TelnetConnect(String proxyServer, int timeOut) {
		super();
		this.proxyServer = proxyServer;
		this.timeOut = timeOut;
	}

	public TelnetConnect(String proxyServer) {
		super();
		this.proxyServer = proxyServer;
		this.timeOut = 100;
	}

	@Override
	protected String execute() {
		String[] arr = StringUtils.split(proxyServer, ":");
		if (arr.length == 2) {
			TelnetClient telnet = new TelnetClient();
			telnet.setConnectTimeout(timeOut);
			telnet.setDefaultTimeout(timeOut);

			try {
				telnet.connect(arr[0], NumberUtils.toInt(arr[1]));
				return "true";
			} catch (IOException e) {
				return "false";
			}
		}

		return "false";
	}

	/**
	 * 是否可以 telnet 连接上
	 *
	 * @return
	 */
	public boolean canConnect() {
		return BooleanUtils.toBoolean(super.execute(timeOut));
	}
}