package com.ttt.one.fileServer.utils;

/**
 * 字符串工具类
 */
public class StrUtil {
    /**
     * 从 MINIO URL中提取文件名
     * @param url 完整的URL地址
     * @return 文件名部分
     */
    public static String extractFileName(String url) {
        // 找到最后一个斜杠的位置
        int lastSlashIndex = url.lastIndexOf('/');
        // 找到问号的位置（参数开始）
        int questionMarkIndex = url.indexOf('?');

        if (lastSlashIndex != -1) {
            if (questionMarkIndex != -1) {
                // 如果有参数，截取斜杠后到问号前的部分
                return url.substring(lastSlashIndex + 1, questionMarkIndex);
            } else {
                // 如果没有参数，截取斜杠后的所有内容
                return url.substring(lastSlashIndex + 1);
            }
        }
        return null;
    }
}
