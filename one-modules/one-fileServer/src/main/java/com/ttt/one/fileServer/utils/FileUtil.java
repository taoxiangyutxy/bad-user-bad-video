package com.ttt.one.fileServer.utils;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtil {

    /**
     * 截取图片
     * @param
     * @param i
     * @param filename
     * @return
     */
    public static List<String> coverImage(MultipartFile sourceFile, Integer i, String filename){
        long start = System.currentTimeMillis();
        // File picFile = null;
        try {
            // picFile  = File.createTempFile(i*25+identifier, ".jpg");
            //  FileUtil.imageFrame(identifier + "/" + filename, i*25,picFile);
            List<File> files = FileUtil.randomGrabberFFmpegImage(sourceFile, i, 0L);
            List<String> strs = new ArrayList<>();
            for (File file : files) {
                log.info("aaa:{}",file.getName());
                MultipartFile mFile = FileUtil.fileTurnMulti(file);
                MinIoUtils.uploadFileMinIo(mFile,"images/"+file.getName()+".jpg");
                String previewUrl = MinIoUtils.getObjectPreviewUrl("uploadtest",
                        "images/" + file.getName()+".jpg", 60 * 24 * 7, "image/jpeg");
                //file.deleteOnExit();
                strs.add(previewUrl);
            }
            long end = System.currentTimeMillis();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(end-start);
            log.info("封面图片上传服务器方法共耗时:{}秒;:{}毫秒",seconds,(end-start));
            return strs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<File> randomGrabberFFmpegImage(MultipartFile sourceFile, int count, long userId) {
        long start = System.currentTimeMillis();
        log.info("开始获取封面");


        FFmpegFrameGrabber grabber = null;
        //String path = FileTypeEnum.filePath();
        try {
            List<File> images = new ArrayList<>(count);

            long end3 = System.currentTimeMillis();
            long seconds3 = TimeUnit.MILLISECONDS.toSeconds(end3-start);
            log.info("获取inputStream流共耗时:{}秒;:{}毫秒",seconds3,(end3-start));

            grabber = new FFmpegFrameGrabber(sourceFile.getInputStream());

            long end4 = System.currentTimeMillis();
            long seconds4 = TimeUnit.MILLISECONDS.toSeconds(end4-start);
            log.info("new一个grabber共耗时:{}秒;:{}毫秒",seconds4,(end4-start));

            grabber.start();

            long end = System.currentTimeMillis();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(end-start);
            log.info("获取grabber共耗时:{}秒;:{}毫秒",seconds,(end-start));

            // 获取视频总帧数
            // int lengthInVideoFrames = grabber.getLengthInVideoFrames();
            // 获取视频时长， / 1000000 将单位转换为秒
            long delayedTime = grabber.getLengthInTime() / 1000000;
            log.info("视频时长秒:{}",delayedTime);

            Random random = SecureRandom.getInstanceStrong();
            int[] timeList = new int[count];
            for (int i = 0; i < count; i++) {
                timeList[i] = random.nextInt((int)delayedTime - 1) + 1;
            }
            // 让截图按时间线排列
            Arrays.sort(timeList);
            for (int i : timeList) {
                // 跳转到响应时间
                grabber.setTimestamp(i * 1000000L);
                Frame f = grabber.grabImage();
                //如果是无效帧  再抓取一次
                if(f == null){
                    grabber.setTimestamp((i+4) * 1000000L);
                    f = grabber.grabImage();
                }
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bi = converter.getBufferedImage(f);
                String imageName = i+"test2.jpg";
                File out = new File(imageName);
                ImageIO.write(bi, "jpg", out);
               // FileTableEntity fileTable = FileUtils.createFileTableEntity(imageName, SUFFIX, path, f.image.length, WebConstant.SYSTEM_CREATE_SCREENSHOT, userId, FileTypeEnum.VIDEO_PHOTO.getCode());
                images.add(out);
            }

            long end2 = System.currentTimeMillis();
            long seconds2 = TimeUnit.MILLISECONDS.toSeconds(end2-start);
            log.info("截取封面方法共耗时:{}秒;:{}毫秒",seconds2,(end2-start));

            return images;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                }
            } catch (FFmpegFrameGrabber.Exception e) {
                log.error("getVideoInfo grabber.release failed 获取文件信息失败：{}", e.getMessage());
            }
        }
    }

    /**
     * 将inputStream转化为file
     * @param is
     * @param file 要输出的文件目录
     */
    public static void inputStream2File (InputStream is, File file) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[8192];

            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } finally {
            os.close();
            is.close();
        }
    }
    /**
     * 随机获取视频截图
     * 返回截图集合
     * 所有逻辑都要修改、
     * 前端去库里找有没有封面 有直接返回
     *
     * @param
     * @param count 输出截图数量
     * @return 截图列表
     * */
    public static List<File> randomGrabberFFmpegImage2(String filename, int count, long userId) {
        long start = System.currentTimeMillis();
        log.info("开始获取封面");
        FFmpegFrameGrabber grabber = null;
        try {
            List<File> images = new ArrayList<>(count);
            InputStream inputStream = null;
            inputStream = MinIoUtils.getFileStream( filename);
            grabber = new FFmpegFrameGrabber(inputStream,0);

            grabber.setFormat("h264");
            // 设置读取的最大数据，单位字节
            grabber.setOption("probesize", "10000");
            // 设置分析的最长时间，单位微秒
            grabber.setOption("analyzeduration", "100000");
            grabber.setOption("rtsp_transport", "tcp");

            grabber.start();

            long end = System.currentTimeMillis();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(end-start);
            log.info("获取grabber共耗时:{}秒;:{}毫秒",seconds,(end-start));
            // 获取视频总帧数
            // int lengthInVideoFrames = grabber.getLengthInVideoFrames();
            // 获取视频时长， / 1000000 将单位转换为秒
            long delayedTime =    grabber.getLengthInTime() / 1000000;
            log.info("视频时长秒:{}",delayedTime);

            Java2DFrameConverter java2dFrameConverter = new Java2DFrameConverter();
            while(true) {
                Frame frame = grabber.grabImage();
                BufferedImage image = java2dFrameConverter.convert(frame);
                if(frame != null){
                    String imageName ="test"+1+".jpg";
                        File out = new File(imageName);
                        ImageIO.write(image, "jpg", out);
                        log.info("file size:{}",out.getPath());
                        images.add(out);

                    long end2 = System.currentTimeMillis();
                    long seconds2 = TimeUnit.MILLISECONDS.toSeconds(end2-start);
                    log.info("截取封面方法共耗时:{}秒;:{}毫秒",seconds2,(end2-start));
                    return images;
                }else {
                    grabber.stop();
                    grabber.close();
                    throw new IOException("The connection is broken");
                }
            }

           /* Random random = new Random();
            for (int i = 0; i < count; i++) {
                // 跳转到响应时间
                long l = (random.nextInt((int) delayedTime - 1) + 1) * 1000000L;
                grabber.setTimestamp(l);
                log.info("截取视频哪秒:{}",l / 1000000);
                Frame f = grabber.grabImage();
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bi = converter.getBufferedImage(f);
                String imageName ="test"+i+".jpg";
                if(f!=null){
                    File out = new File(imageName);
                    ImageIO.write(bi, "jpg", out);
                    log.info("file size:{}",out.getPath());
                    images.add(out);
                }
            }*/



        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                }
            } catch (FFmpegFrameGrabber.Exception e) {
                log.error("getVideoInfo grabber.release failed 获取文件信息失败：{}", e.getMessage());
            }
        }
    }

    /**
     * 返回视频截图
     * @param filename
     * @param size 截取哪一帧
     * @return
     */
    public static void imageFrame(String filename,Integer size,File picFile){
        InputStream inputStream = null;
        inputStream = MinIoUtils.getFileStream( filename);
        // 设置maximumSize为0，禁用 seek 回调，从而减少启动时间
        FFmpegFrameGrabber  ff = new FFmpegFrameGrabber(inputStream,0);
        try {
            // 设置采集器构造超时时间(单位微秒，1秒=1000000微秒)
            ff.setOption("stimeout", "2000000");
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
