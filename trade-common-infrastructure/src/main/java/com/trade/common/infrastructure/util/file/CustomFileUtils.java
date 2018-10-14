package com.trade.common.infrastructure.util.file;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.trade.common.infrastructure.util.date.CustomDateFormatUtils;
import com.trade.common.infrastructure.util.security.MD5Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

public class CustomFileUtils {

	/**
	 * 取得当前运行环境的服务器根目录
	 *
	 * @return
	 */
	public static String getRootPath() {
		return Thread.currentThread().getContextClassLoader().getResource("").getPath();
	}

	/**
	 * 复制文件或者目录,复制前后文件完全一样。
	 *
	 * @param resFilePath 源文件路径
	 * @param distFolder  目标文件夹
	 * @IOException 当操作发生异常时抛出
	 */
	public static void copyFile(String resFilePath, String distFolder) throws IOException {
		File resFile = new File(resFilePath);
		File distFile = new File(distFolder);
		if (resFile.isDirectory()) {
			FileUtils.copyDirectoryToDirectory(resFile, distFile);
		} else if (resFile.isFile()) {
			FileUtils.copyFileToDirectory(resFile, distFile, true);
		}
	}

	/**
	 * 删除一个文件或者目录
	 *
	 * @param targetPath 文件或者目录路径
	 * @IOException 当操作发生异常时抛出
	 */
	public static void deleteFile(String targetPath) throws IOException {
		File targetFile = new File(targetPath);
		if (targetFile.isDirectory()) {
			FileUtils.deleteDirectory(targetFile);
		} else if (targetFile.isFile()) {
			targetFile.delete();
		}
	}

	/**
	 * 重命名文件或文件夹
	 *
	 * @param resFilePath 源文件路径
	 * @param newFileName 重命名
	 * @return 操作成功标识
	 */
	public static boolean renameFile(String resFilePath, String newFileName) {
		String newFilePath = formatPath(getParentPath(resFilePath) + "/" + newFileName);
		File resFile = new File(resFilePath);
		File newFile = new File(newFilePath);
		return resFile.renameTo(newFile);
	}

	/**
	 * 读取文件或者目录的大小
	 *
	 * @param distFilePath 目标文件或者文件夹
	 * @return 文件或者目录的大小，如果获取失败，则返回-1
	 */
	public static long genFileSize(String distFilePath) {
		File distFile = new File(distFilePath);
		if (distFile.isFile()) {
			return distFile.length();
		} else if (distFile.isDirectory()) {
			return FileUtils.sizeOfDirectory(distFile);
		}
		return -1L;
	}

	/**
	 * 判断一个文件是否存在
	 *
	 * @param filePath 文件路径
	 * @return 存在返回true，否则返回false
	 */
	public static boolean isExist(String filePath) {
		return new File(filePath).exists();
	}

	/**
	 * 本地某个目录下的文件列表（不递归）
	 *
	 * @param folder ftp上的某个目录
	 * @param suffix 文件的后缀名（比如.mov.xml)
	 * @return 文件名称列表
	 */
	public static String[] listFilebySuffix(String folder, String suffix) {
		IOFileFilter fileFilter1 = new SuffixFileFilter(suffix);
		IOFileFilter fileFilter2 = new NotFileFilter(DirectoryFileFilter.INSTANCE);
		FilenameFilter filenameFilter = new AndFileFilter(fileFilter1, fileFilter2);
		return new File(folder).list(filenameFilter);
	}

	/**
	 * 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功！)
	 *
	 * @param res      原字符串
	 * @param filePath 文件路径
	 * @return 成功标记
	 */
	public static boolean string2File(String res, String filePath) {
		boolean flag = true;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		try {
			File distFile = new File(filePath);
			if (!distFile.getParentFile().exists())
				distFile.getParentFile().mkdirs();
			bufferedReader = new BufferedReader(new StringReader(res));
			bufferedWriter = new BufferedWriter(new FileWriter(distFile));
			char buf[] = new char[1024]; // 字符缓冲区
			int len;
			while ((len = bufferedReader.read(buf)) != -1) {
				bufferedWriter.write(buf, 0, len);
			}
			bufferedWriter.flush();
			bufferedReader.close();
			bufferedWriter.close();
		} catch (IOException e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 以行为单位读取文件
	 *
	 * @param fileName
	 */
	public static List<String> readFileByLines(String fileName) {
		List<String> result = Lists.newArrayList();

		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				result.add(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}

		return result;
	}

	// /**
	// * 移动文件或者目录,移动前后文件完全一样,如果目标文件夹不存在则创建。
	// *
	// * @param resFilePath 源文件路径
	// * @param distFolder 目标文件夹
	// * @IOException 当操作发生异常时抛出
	// */
	// public static void moveFile(String resFilePath, String distFolder) throws IOException {
	// File resFile = new File(resFilePath);
	// File distFile = new File(distFolder);
	// if (resFile.isDirectory()) {
	// FileUtils.moveDirectoryToDirectory(resFile, distFile, true);
	// } else if (resFile.isFile()) {
	// FileUtils.moveFileToDirectory(resFile, distFile, true);
	// }
	// }

	/**
	 * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符,并且去掉末尾的"/"符号。
	 *
	 * @param path 文件路径
	 * @return 格式化后的文件路径
	 */
	public static String formatPath(String path) {
		String reg0 = "\\\\＋";
		String reg = "\\\\＋|/＋";
		String temp = path.trim().replaceAll(reg0, "/");
		temp = temp.replaceAll(reg, "/");
		if (temp.endsWith("/")) {
			temp = temp.substring(0, temp.length() - 1);
		}
		if (System.getProperty("file.separator").equals("\\")) {
			temp = temp.replace('/', '\\');
		}
		return temp;
	}

	/**
	 * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符,并且去掉末尾的"/"符号(适用于FTP远程文件路径或者Web资源的相对路径)。
	 *
	 * @param path 文件路径
	 * @return 格式化后的文件路径
	 */
	public static String formatPath4Ftp(String path) {
		String reg0 = "\\\\＋";
		String reg = "\\\\＋|/＋";
		String temp = path.trim().replaceAll(reg0, "/");
		temp = temp.replaceAll(reg, "/");
		if (temp.endsWith("/")) {
			temp = temp.substring(0, temp.length() - 1);
		}
		return temp;
	}

	/**
	 * 获取文件父路径
	 *
	 * @param path 文件路径
	 * @return 文件父路径
	 */
	public static String getParentPath(String path) {
		return new File(path).getParent();
	}

	/**
	 * 获取相对路径
	 *
	 * @param fullPath 全路径
	 * @param rootPath 根路径
	 * @return 相对根路径的相对路径
	 */
	public static String getRelativeRootPath(String fullPath, String rootPath) {
		String relativeRootPath = null;
		String _fullPath = formatPath(fullPath);
		String _rootPath = formatPath(rootPath);

		if (_fullPath.startsWith(_rootPath)) {
			relativeRootPath = fullPath.substring(_rootPath.length());
		} else {
			throw new RuntimeException("要处理的两个字符串没有包含关系，处理失败！");
		}

		if (relativeRootPath == null) {
			return null;
		} else {
			return formatPath(relativeRootPath);
		}
	}

	/**
	 * 拆分文件路径和文件名称
	 *
	 * @param filePath
	 * @return
	 */
	public static List<String> calFolderPathAndFileName(String filePath) {
		List<String> result = Lists.newArrayList();

		if (filePath.contains("\\")) {
			filePath = filePath.replace("\\", "/");
		}

		String folderPath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

		if (!Strings.isNullOrEmpty(folderPath) && !Strings.isNullOrEmpty(fileName)) {
			result.add(folderPath);
			result.add(fileName);
		}

		return result;
	}

	/**
	 * 从文件路径计算除去文件名的文件路径
	 *
	 * @param filePath
	 * @return
	 */
	public static String calFolderPath(String filePath) {
		List<String> folderPathAndFileName = calFolderPathAndFileName(filePath);
		return (folderPathAndFileName.size() == 2) ? folderPathAndFileName.get(0) : "";
	}

	/**
	 * 从文件路径计算文件名
	 *
	 * @param filePath
	 * @return
	 */
	public static String calFileName(String filePath) {
		List<String> folderPathAndFileName = calFolderPathAndFileName(filePath);
		return (folderPathAndFileName.size() == 2) ? folderPathAndFileName.get(1) : "";
	}

	/**
	 * 读取文件后缀名
	 *
	 * @param fileName
	 * @return
	 */
	public static String getFileSuffix(String fileName) {
		if (fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf("."));
		}

		return "";
	}

	/**
	 * 读取文件标题和后缀名
	 *
	 * @param fileName
	 * @return
	 */
	public static List<String> getFileTitleAndSuffix(String fileName) {
		List<String> result = Lists.newArrayList();

		String fileSuffix = getFileSuffix(fileName);
		String fileTitle = fileName.replace(fileSuffix, "");

		if (!Strings.isNullOrEmpty(fileTitle) && !Strings.isNullOrEmpty(fileSuffix)) {
			result.add(fileTitle);
			result.add(fileSuffix);
		}

		return result;
	}

	/**
	 * 创建一个新的MD5加密的文件名称
	 *
	 * @param fileName
	 * @return
	 */
	public static String createMD5FileName(String fileName) {
		List<String> fileTitleAndSuffix = CustomFileUtils.getFileTitleAndSuffix(fileName);

		if (fileTitleAndSuffix.size() == 2 && !Strings.isNullOrEmpty(fileTitleAndSuffix.get(0)) && !Strings.isNullOrEmpty(fileTitleAndSuffix.get(1))) {
			return MD5Utils.md5_16(fileTitleAndSuffix.get(0)) + fileTitleAndSuffix.get(1).toLowerCase();
		} else {
			return "";
		}
	}

	/**
	 * 创建一个新的唯一的文件名称
	 *
	 * @param fileName
	 * @return
	 */
	public static String createUniqueFileName(String fileName) {
		List<String> fileTitleAndSuffix = CustomFileUtils.getFileTitleAndSuffix(fileName);

		if (fileTitleAndSuffix.size() == 2 && !Strings.isNullOrEmpty(fileTitleAndSuffix.get(0)) && !Strings.isNullOrEmpty(fileTitleAndSuffix.get(1))) {
			return MD5Utils.md5_16(fileTitleAndSuffix.get(0) + CustomDateFormatUtils.formatDateTime(LocalDateTime.now())) + fileTitleAndSuffix.get(1).toLowerCase();
		} else {
			return "";
		}
	}
}
