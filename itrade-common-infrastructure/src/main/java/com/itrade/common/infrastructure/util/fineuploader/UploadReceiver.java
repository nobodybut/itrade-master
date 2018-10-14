package com.itrade.common.infrastructure.util.fineuploader;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

//commented code blocks are only used for CORS environments
public class UploadReceiver extends HttpServlet {

	private static String CONTENT_LENGTH = "Content-Length";
	private static int SUCCESS_RESPONSE_CODE = 200;

	final Logger log = LoggerFactory.getLogger(UploadReceiver.class);

	@Override
	public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
		resp.setStatus(SUCCESS_RESPONSE_CODE);
		// resp.addHeader("Access-Control-Allow-Origin", "http://192.168.130.118:8080");
		// resp.addHeader("Access-Control-Allow-Credentials", "true");
		resp.addHeader("Access-Control-Allow-Methods", "POST, DELETE");
		resp.addHeader("Access-Control-Allow-Headers", "x-requested-with, cache-control, content-type");
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		RequestParser requestParser = null;
		File uploadFolder = calUploadFolder(req);
		File fileResult = new File("");

		boolean isIframe = req.getHeader("X-Requested-With") == null || !req.getHeader("X-Requested-With").equals("XMLHttpRequest");

		try {
			// resp.setContentType(isIframe ? "text/html" : "text/plain");
			resp.setContentType("text/plain");
			resp.setStatus(SUCCESS_RESPONSE_CODE);

			// resp.addHeader("Access-Control-Allow-Origin", "http://192.168.130.118:8080");
			// resp.addHeader("Access-Control-Allow-Credentials", "true");
			// resp.addHeader("Access-Control-Allow-Origin", "*");

			if (ServletFileUpload.isMultipartContent(req)) {
				MultipartUploadParser multipartUploadParser = new MultipartUploadParser(req, calUploadFolderTemp(req), getServletContext());
				requestParser = RequestParser.getInstance(req, multipartUploadParser);
				fileResult = writeFileForMultipartRequest(requestParser, uploadFolder);
				writeResponse(resp.getWriter(), requestParser.generateError() ? "Generated error" : null, isIframe, false, requestParser, uploadFolder, fileResult);
			} else {
				requestParser = RequestParser.getInstance(req, null);

				// handle POST delete file request
				if (requestParser.getMethod() != null && requestParser.getMethod().equalsIgnoreCase("DELETE")) {
					String uuid = requestParser.getUuid();
					handleDeleteFileRequest(uuid, uploadFolder, resp);
				} else {
					fileResult = writeFileForNonMultipartRequest(req, requestParser, uploadFolder);
					writeResponse(resp.getWriter(), requestParser.generateError() ? "Generated error" : null, isIframe, false, requestParser, uploadFolder, fileResult);
				}
			}
		} catch (Exception e) {
			log.error("Problem handling upload request", e);
			if (e instanceof MergePartsException) {
				writeResponse(resp.getWriter(), e.getMessage(), isIframe, true, requestParser, uploadFolder, fileResult);
			} else {
				writeResponse(resp.getWriter(), e.getMessage(), isIframe, false, requestParser, uploadFolder, fileResult);
			}
		}
	}

	private File calUploadFolder(HttpServletRequest req) {
		return new File(this.getClass().getClassLoader().getResource("").getPath() + req.getParameter("uploadfolder"));
	}

	private File calUploadFolderTemp(HttpServletRequest req) {
		String uploadFolder = req.getParameter("uploadfolder");
		String uploadFolderTemp = String.format("%s_temp%s", uploadFolder.substring(0, uploadFolder.indexOf("/", 1)), uploadFolder.substring(uploadFolder.indexOf("/", 1)));

		return new File(this.getClass().getClassLoader().getResource("").getPath() + uploadFolderTemp);
	}

	private File writeFileForNonMultipartRequest(HttpServletRequest req, RequestParser requestParser, File uploadFolder) throws Exception {
		File result = new File("");

		File dir = new File(uploadFolder, requestParser.getUuid());
		dir.mkdirs();

		String contentLengthHeader = req.getHeader(CONTENT_LENGTH);
		long expectedFileSize = Long.parseLong(contentLengthHeader);

		if (requestParser.getPartIndex() >= 0) {
			result = writeFile(req.getInputStream(), new File(dir, requestParser.getUuid() + "_" + String.format("%05d", requestParser.getPartIndex())), null);

			if (requestParser.getTotalParts() - 1 == requestParser.getPartIndex()) {
				File[] parts = getPartitionFiles(dir, requestParser.getUuid());
				File outputFile = new File(dir, requestParser.getFilename());
				for (File part : parts) {
					mergeFiles(outputFile, part);
				}

				assertCombinedFileIsVaid(requestParser.getTotalFileSize(), outputFile, requestParser.getUuid(), uploadFolder);
				deletePartitionFiles(dir, requestParser.getUuid());
			}
		} else {
			result = writeFile(req.getInputStream(), new File(dir, requestParser.getFilename()), expectedFileSize);
		}

		return result;
	}

	private File writeFileForMultipartRequest(RequestParser requestParser, File uploadFolder) throws Exception {
		File result = new File("");

		File dir = new File(uploadFolder, requestParser.getUuid());
		dir.mkdirs();

		if (requestParser.getPartIndex() >= 0) {
			result = writeFile(requestParser.getUploadItem().getInputStream(), new File(dir, requestParser.getUuid() + "_" + String.format("%05d", requestParser.getPartIndex())), null);

			if (requestParser.getTotalParts() - 1 == requestParser.getPartIndex()) {
				File[] parts = getPartitionFiles(dir, requestParser.getUuid());
				File outputFile = new File(dir, requestParser.getOriginalFilename());
				for (File part : parts) {
					mergeFiles(outputFile, part);
				}

				assertCombinedFileIsVaid(requestParser.getTotalFileSize(), outputFile, requestParser.getUuid(), uploadFolder);
				deletePartitionFiles(dir, requestParser.getUuid());
			}
		} else {
			result = writeFile(requestParser.getUploadItem().getInputStream(), new File(dir, requestParser.getFilename()), null);
		}

		return result;
	}

	private void assertCombinedFileIsVaid(long totalFileSize, File outputFile, String uuid, File uploadFolder) throws MergePartsException {
		if (totalFileSize != outputFile.length()) {
			deletePartitionFiles(uploadFolder, uuid);
			outputFile.delete();
			throw new MergePartsException("Incorrect combined file size!");
		}

	}

	private static class PartitionFilesFilter implements FilenameFilter {
		private String filename;

		PartitionFilesFilter(String filename) {
			this.filename = filename;
		}

		@Override
		public boolean accept(File file, String s) {
			return s.matches(Pattern.quote(filename) + "_\\d+");
		}
	}

	private static File[] getPartitionFiles(File directory, String filename) {
		File[] files = directory.listFiles(new PartitionFilesFilter(filename));
		Arrays.sort(files);
		return files;
	}

	private static void deletePartitionFiles(File directory, String filename) {
		File[] partFiles = getPartitionFiles(directory, filename);
		for (File partFile : partFiles) {
			partFile.delete();
		}
	}

	private File mergeFiles(File outputFile, File partFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(outputFile, true);

		try {
			FileInputStream fis = new FileInputStream(partFile);

			try {
				IOUtils.copy(fis, fos);
			} finally {
				IOUtils.closeQuietly(fis);
			}
		} finally {
			IOUtils.closeQuietly(fos);
		}

		return outputFile;
	}

	private File writeFile(InputStream in, File out, Long expectedFileSize) throws IOException {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(out);

			IOUtils.copy(in, fos);

			if (expectedFileSize != null) {
				Long bytesWrittenToDisk = out.length();
				if (!expectedFileSize.equals(bytesWrittenToDisk)) {
					log.warn("Expected file {} to be {} bytes; file on disk is {} bytes", new Object[]{out.getAbsolutePath(), expectedFileSize, 1});
					out.delete();
					throw new IOException(String.format("Unexpected file size mismatch. Actual bytes %s. Expected bytes %s.", bytesWrittenToDisk, expectedFileSize));
				}
			}

			return out;
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	private void writeResponse(PrintWriter writer, String failureReason, boolean isIframe, boolean restartChunking, RequestParser requestParser, File uploadFolder, File fileResult) {
		if (failureReason == null) {
			String fileResultPath = fileResult.getAbsolutePath().replace("\\", "/");
			if (!fileResultPath.startsWith("/")) {
				fileResultPath = "/" + fileResultPath;
			}

			String classPath = this.getClass().getClassLoader().getResource("").getPath();
			if (!classPath.startsWith("/")) {
				classPath = "/" + classPath;
			}

			String fileResultUrl = fileResultPath.replace(classPath, "");
			if (!fileResultUrl.startsWith("/")) {
				fileResultUrl = "/" + fileResultUrl;
			}

			writer.print("{\"success\": true, \"fileUrl\": \"" + fileResultUrl + "\"}");
		} else {
			if (restartChunking) {
				writer.print("{\"error\": \"" + failureReason + "\", \"reset\": true}");
			} else {
				writer.print("{\"error\": \"" + failureReason + "\"}");
			}
		}
	}

	private class MergePartsException extends Exception {
		MergePartsException(String message) {
			super(message);
		}
	}

	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String uuid = req.getPathInfo().replaceAll("/", "");
		File uploadFolder = calUploadFolder(req);

		handleDeleteFileRequest(uuid, uploadFolder, resp);
	}

	private void handleDeleteFileRequest(String uuid, File uploadFolder, HttpServletResponse resp) throws IOException {
		FileUtils.deleteDirectory(new File(uploadFolder, uuid));

		if (new File(uploadFolder, uuid).exists()) {
			log.warn("couldn't find or delete " + uuid);
		} else {
			log.info("deleted " + uuid);
		}

		resp.setStatus(SUCCESS_RESPONSE_CODE);
	}
}
