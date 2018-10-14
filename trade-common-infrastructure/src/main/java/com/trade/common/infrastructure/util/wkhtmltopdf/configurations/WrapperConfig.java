package com.trade.common.infrastructure.util.wkhtmltopdf.configurations;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class WrapperConfig {

	private XvfbConfig xvfbConfig;

	private String wkhtmltopdfCommand = "wkhtmltopdf";

	public WrapperConfig() {
		setWkhtmltopdfCommand(findExecutable());
	}

	public WrapperConfig(String wkhtmltopdfCommand) {
		setWkhtmltopdfCommand(wkhtmltopdfCommand);
	}

	public String getWkhtmltopdfCommand() {
		return wkhtmltopdfCommand;
	}

	public void setWkhtmltopdfCommand(String wkhtmltopdfCommand) {
		this.wkhtmltopdfCommand = wkhtmltopdfCommand;
	}

	/**
	 * Attempts to find the `wkhtmltopdf` executable in the system path.
	 *
	 * @return the wkhtmltopdf command according to the OS
	 */
	public String findExecutable() {
		try {
			String osname = System.getProperty("os.name").toLowerCase();

			String cmd = osname.contains("windows") ? "where wkhtmltopdf" : "which wkhtmltopdf";

			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();

			String text = IOUtils.toString(p.getInputStream(), "utf-8").trim();

			if (text.isEmpty())
				throw new RuntimeException();

			setWkhtmltopdfCommand(text);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return getWkhtmltopdfCommand();
	}

	public boolean isXvfbEnabled() {
		return xvfbConfig != null;
	}

	public XvfbConfig getXvfbConfig() {
		return xvfbConfig;
	}

	public void setXvfbConfig(XvfbConfig xvfbConfig) {
		this.xvfbConfig = xvfbConfig;
	}
}
