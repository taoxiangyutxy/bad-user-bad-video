

package com.ttt.one.common.utils;

/**
 * 文件上传常量
 *
 */
public class FileUploadConstant {
	/**
	 * 上传文件类型
	 * 
	 * @author
	 * @email
	 * @date
	 */
    public enum UploadFileType {
        /**
         * 文件秒传
         */
    	FILE_SUCCESS(1000),
        /**
         * 断点续传或新文件上传
         */
        FILE_BREAKPOINT(1001),
        /**
         * 上传失败
         */
        FILE_ERROR(1002);

        private int value;

        UploadFileType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }



}
