package com.ttt.one.fileServer.utils;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件工具类
 */
public class FileUtil {
    /**
     * 返回视频截图
     * @param filename
     * @param size 截取哪一帧
     * @return
     */
    public static void imageFrame(String filename,Integer size,File picFile){
        InputStream inputStream = null;
        inputStream = MinIoUtils.getFileStream( filename);
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(inputStream);
        try {
            ff.start();
            int lenght = ff.getLengthInFrames();
            Frame f = null;
            int m = 20;
            while (m < lenght){
                f = ff.grabImage();
                if(m >size && (f != null)&& (f.image != null)){
                    break;
                }
                m++;
            }
            ImageIO.write(FrameToBufferedImage(f), "jpg", picFile);
            ff.stop();
    }catch (Exception e){
        e.printStackTrace();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * file 转 Mult
     * @param file
     * @return
     */
    public static MultipartFile fileTurnMulti(File file){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);
            return multipartFile;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 将 Frame 转换为 BufferedImage
     * @param frame
     * @return
     */
    public static BufferedImage FrameToBufferedImage(Frame frame) {
        //创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        return converter.getBufferedImage(frame);
    }
}
