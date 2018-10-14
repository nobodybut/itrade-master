package com.itrade.common.infrastructure.util.shell;

import org.apache.commons.exec.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CommandUtils {

	// 日志记录
	private static final Logger s_logger = LoggerFactory.getLogger(CommandUtils.class);

	/**
	 *	执行命令返回
	 * @param command
	 * @return
	 */
	public static int executeCommand(final String command) {
		int exitcode = -1;

		File file = null;
		OutputStream outputStream = null;
		ByteArrayOutputStream out = null;
		ByteArrayOutputStream err = null;

		try {
			file = File.createTempFile("command", ".tmp");
			outputStream = new FileOutputStream(file);
			IOUtils.write(command, outputStream);

			CommandLine commandLine = new CommandLine("bash");
			commandLine.addArgument(file.getAbsolutePath());

			out = new ByteArrayOutputStream();
			err = new ByteArrayOutputStream();
			final Executor executor = new DefaultExecutor();

			executor.setStreamHandler(new PumpStreamHandler(out, err));
			exitcode = executor.execute(commandLine);
		} catch (ExecuteException ex) {
			if (ex != null) {
				s_logger.error("ExecuteException:" + String.format("[%s] , %s ", command, ex.toString()));
			}
		} catch (Exception ex) {
			s_logger.error("Exception:" + String.format("[%s] , %s ", command, ex.toString()));
		} finally {
			closeStreamAndDeleteFile(command, file, outputStream, out, err);
		}
		return exitcode;
	}

	/**
	 * 执行命令返回输出结果
	 * @param command
	 * @return
	 */
	public static String executeCommandWithReturn(final String command) {
		File file = null;
		OutputStream outputStream = null;
		ByteArrayOutputStream out = null;
		ByteArrayOutputStream err = null;
		try {
			file = File.createTempFile("command", ".tmp");
			outputStream = new FileOutputStream(file);
			IOUtils.write(command, outputStream);

			CommandLine commandLine = new CommandLine("bash");
			commandLine.addArgument(file.getAbsolutePath());

			out = new ByteArrayOutputStream();
			err = new ByteArrayOutputStream();

			final Executor executor = new DefaultExecutor();
			executor.setStreamHandler(new PumpStreamHandler(out, err));
			int exitcode = executor.execute(commandLine);

			if (exitcode == 0) {
				return new String(out.toByteArray(), "UTF-8");
			}
		} catch (ExecuteException ex) {
			if (ex != null) {
				s_logger.error("ExecuteException:" + String.format("[%s] , %s ", command, ex.toString()));
			}
		} catch (Exception ex) {
			s_logger.error("Exception:" + String.format("[%s] , %s ", command, ex.toString()));
		} finally {
			closeStreamAndDeleteFile(command, file, outputStream, out, err);
		}
		return "";
	}

	private static void closeStreamAndDeleteFile(String command, File file, OutputStream outputStream, ByteArrayOutputStream out, ByteArrayOutputStream err) {
		if (file != null) {
			file.delete();
		}
		try {
			if (outputStream != null) {
				outputStream.close();
			}
			if (out != null) {
				out.close();
			}
			if (err != null) {
				err.close();
			}
		} catch (IOException ex) {
			s_logger.error("Exception:" + String.format("[%s] , %s ", command, ex.toString()));
		}
	}
}
