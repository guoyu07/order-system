package cn.com.xbed.app.commons.util;

public class UploadHelper {

	/**
	 * 返回扩展名,变成小写,不包括点
	 * 
	 * @param fileNameExtension
	 * @return
	 */
	public static String getFileExtLowercaseNoDot(String fileNameExtension) {
		try {
			int index = fileNameExtension.lastIndexOf(".");
			String extension = fileNameExtension.substring(index + 1);
			return extension.toLowerCase();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 返回扩展名,变成小写,包括点
	 * 
	 * @param fileNameExtension
	 * @return
	 */
	public static String getFileExtLowercaseDot(String fileNameExtension) {
		try {
			int index = fileNameExtension.lastIndexOf(".");
			String extension = fileNameExtension.substring(index);
			return extension.toLowerCase();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取文件名
	 * 
	 * @param fileNameExtension
	 * @return
	 */
	public static String getFileNameNoExt(String fileNameExtension) {
		try {
			int index = fileNameExtension.lastIndexOf(".");
			String fileName = fileNameExtension.substring(0, index);
			return fileName;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getFileNameWithExtLowercase(String fileNameExtension) {
		try {
			String fileName = getFileNameNoExt(fileNameExtension);
			String extensionWithDot = getFileExtLowercaseDot(fileNameExtension);
			return fileName + extensionWithDot;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		System.out.println(getFileNameWithExtLowercase("QA预发布号微信认证.TXT"));
	}
}
