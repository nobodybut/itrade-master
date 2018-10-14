package com.itrade.common.infrastructure.util.image;

import com.itrade.common.infrastructure.util.collection.KeyValuePair;
import com.itrade.common.infrastructure.util.logger.LogInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CustomImageUtils {

	// 日志记录
	private static final Logger s_logger = LoggerFactory.getLogger(CustomImageUtils.class);

	/**
	 * 计算图片是否为长方形
	 *
	 * @param imageFullPath
	 * @return
	 */
	public static boolean isRectangleImage(String imageFullPath) {
		KeyValuePair<Integer, Integer> widthAndHeight = getImageWidthAndHeight(imageFullPath);
		return (widthAndHeight.getKey() >= widthAndHeight.getValue());
	}

	/**
	 * 计算图片的宽和高
	 *
	 * @param imageFullPath
	 * @return
	 */
	public static KeyValuePair<Integer, Integer> getImageWidthAndHeight(String imageFullPath) {
		BufferedImage bufImage = getBufferedImage(imageFullPath);

		if (bufImage != null) {
			return new KeyValuePair<Integer, Integer>(bufImage.getWidth(), bufImage.getHeight());
		}

		return new KeyValuePair<Integer, Integer>(0, 0);
	}

	/**
	 * 读取图片数据
	 *
	 * @param imageFullPath
	 * @return
	 */
	public static BufferedImage getBufferedImage(String imageFullPath) {
		boolean hasException = false;
		Exception ex = null;

		try {
			File picture = new File(imageFullPath);
			return ImageIO.read(new FileInputStream(picture));
		} catch (FileNotFoundException e) {
			hasException = true;
			ex = e;
		} catch (IOException e) {
			hasException = true;
			ex = e;
		}

		if (hasException) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("imageFullPath=%s", imageFullPath);

			s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		return null;
	}
}
