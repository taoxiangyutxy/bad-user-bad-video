package com.ttt.one.fileServer.utils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @ClassName
 * @Description MinIo工具类
 * @Author
 * @Emial_AND_OICQ
 * @Date 2021/8/23
 * @Version 2.0  如有方法不能正常使用，请对照最新MINIO官网API修改
 **/
@Component
public class MinIoUtils implements InitializingBean {

    @Value("${minio.endpoint}")
    private String urlValue;
    private static String url;

    @Value("${minio.accessKey}")
    private String accessKeyValue;
    private static String accessKey;

    @Value("${minio.secretKey}")
    private String secretKeyValue;
    private static String secretKey;

    @Value("${minio.chunkBucKet}")
    private String chunkBucKetValue;
    private static String chunkBucKet;

    private static MinioClient minioClient;

    /**
     * 排序
     */
    public final static boolean SORT = true;
    /**
     * 不排序
     */
    public final static boolean NOT_SORT = false;
    /**
     * 删除分片
     */
    public final static boolean DELETE_CHUNK_OBJECT = true;
    /**
     * 不删除分片
     */
    public final static boolean NOT_DELETE_CHUNK_OBJECT = false;
    /**
     * 默认过期时间(分钟)
     */
    private final static Integer DEFAULT_EXPIRY = 60;

    @Override
    public void afterPropertiesSet() throws Exception {
        minioClient = MinioClient.builder().endpoint(url).credentials(accessKey,secretKey)
                .build();
        //方便管理分片文件，则单独创建一个分片文件的存储桶
        if (!isBucketExist(chunkBucKet)){
            createBucket(chunkBucKet);
        }
    }

    /**
     * 初始化MinIo对象
     * 非此工具类请勿使用该方法
     */
    @PostConstruct
    public void init(){
        url = this.urlValue;
        accessKey = this.accessKeyValue;
        secretKey = this.secretKeyValue;
        chunkBucKet = this.chunkBucKetValue;
    }

    /**
     * 存储桶是否存在
     * @param bucketName 存储桶名称
     * @return true/false
     */
    @SneakyThrows
    public static boolean isBucketExist(String bucketName){
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 创建存储桶
     * @param bucketName 存储桶名称
     * @return true/false
     */
    @SneakyThrows
    public static boolean createBucket(String bucketName){
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        return true;
    }

    /**
     * 获取访问对象的外链地址
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expiry 过期时间(分钟) 最大为7天 超过7天则默认最大值
     * @return viewUrl
     */
    @SneakyThrows
    public static String getObjectUrl(String bucketName,String objectName,Integer expiry){
        Map<String, String> reqParams = new HashMap<String, String>();
        //预览视频 关键  设置类型
        reqParams.put("response-content-type", "video/mp4");
       // reqParams.put("response-content-disposition", "video/mpeg4");
        expiry = expiryHandle(expiry);
        String objectUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .extraQueryParams(reqParams)
                        .expiry(expiry)
                        .build()
        );
        return objectUrl;
    }

    /**
     * 创建上传文件对象的外链
     * @param bucketName 存储桶名称
     * @param objectName 欲上传文件对象的名称
     * @param expiry 过期时间(分钟) 最大为7天 超过7天则默认最大值
     * @return uploadUrl
     */
    @SneakyThrows
    public static String createUploadUrl(String bucketName,String objectName,Integer expiry){
        expiry = expiryHandle(expiry);
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(expiry)
                        .build()
        );
    }

    /**
     * 创建上传文件对象的外链
     * @param bucketName 存储桶名称
     * @param objectName 欲上传文件对象的名称
     * @return uploadUrl
     */
    public static String createUploadUrl(String bucketName,String objectName){
        return createUploadUrl(bucketName,objectName,DEFAULT_EXPIRY);
    }

    /**
     * 批量创建分片上传外链
     * @param bucketName 存储桶名称
     * @param objectMD5 欲上传分片文件主文件的MD5
     * @param chunkCount 分片数量
     * @return uploadChunkUrls
     */
    public static List<String> createUploadChunkUrlList(String bucketName,String objectMD5,Integer chunkCount){
        if (null == bucketName){
            bucketName = chunkBucKet;
        }
        if (null == objectMD5){
            return null;
        }
        objectMD5 += "/";
        if(null == chunkCount || 0 == chunkCount){
            return null;
        }
        List<String> urlList = new ArrayList<>(chunkCount);
        for (int i = 1; i <= chunkCount; i++){
            String objectName = objectMD5 + i + ".chunk";
            urlList.add(createUploadUrl(bucketName,objectName,DEFAULT_EXPIRY));
        }
        return urlList;
    }

    /**
     * 创建指定序号的分片文件上传外链
     * @param bucketName 存储桶名称
     * @param objectMD5 欲上传分片文件主文件的MD5
     * @param partNumber 分片序号
     * @return uploadChunkUrl
     */
    public static String createUploadChunkUrl(String bucketName,String objectMD5,Integer partNumber){
        if (null == bucketName){
            bucketName = chunkBucKet;
        }
        if (null == objectMD5){
            return null;
        }
        objectMD5 += "/" + partNumber + ".chunk";
        return createUploadUrl(bucketName,objectMD5,DEFAULT_EXPIRY);
    }

    /**
     * 获取对象文件名称列表
     * @param bucketName 存储桶名称
     * @param prefix 对象名称前缀
     * @param sort 是否排序(升序)
     * @return objectNames
     */
    @SneakyThrows
    public static List<String> listObjectNames(String bucketName,String prefix,Boolean sort){
        ListObjectsArgs listObjectsArgs;
        if(null == prefix){
            listObjectsArgs = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(true)
                    .build();
        }else {
            listObjectsArgs = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(true)
                    .build();
        }
        Iterable<Result<Item>> chunks = minioClient.listObjects(listObjectsArgs);
        List<String> chunkPaths = new ArrayList<>();
        for (Result<Item> item : chunks){
            chunkPaths.add(item.get().objectName());
        }
        if (sort){
            chunkPaths.sort(new Str2IntComparator(false));
        }
        return chunkPaths;
    }

    /**
     * 获取对象文件名称列表
     * @param bucketName 存储桶名称
     * @param prefix 对象名称前缀
     * @return objectNames
     */
    public static List<String> listObjectNames(String bucketName,String prefix){
        return listObjectNames(bucketName, prefix, NOT_SORT);
    }

    /**
     * 获取分片文件名称列表
     * @param bucketName 存储桶名称
     * @param ObjectMd5 对象Md5
     * @return objectChunkNames
     */
    public static List<String> listChunkObjectNames(String bucketName,String ObjectMd5){
        if (null == bucketName){
            bucketName = chunkBucKet;
        }
        if (null == ObjectMd5){
            return null;
        }
        return listObjectNames(bucketName,ObjectMd5,SORT);
    }

    /**
     * 获取分片名称地址HashMap key=分片序号 value=分片文件地址
     * @param bucketName 存储桶名称
     * @param ObjectMd5 对象Md5
     * @return objectChunkNameMap
     */
    public static Map<Integer,String> mapChunkObjectNames(String bucketName, String ObjectMd5){
        if (null == bucketName){
            bucketName = chunkBucKet;
        }
        if (null == ObjectMd5){
            return null;
        }
        List<String> chunkPaths = listObjectNames(bucketName,ObjectMd5);
        if (null == chunkPaths || chunkPaths.size() == 0){
            return null;
        }
        Map<Integer,String> chunkMap = new HashMap<>(chunkPaths.size());
        for (String chunkName : chunkPaths) {
            Integer partNumber = Integer.parseInt(chunkName.substring(chunkName.indexOf("/") + 1,chunkName.lastIndexOf(".")));
            chunkMap.put(partNumber,chunkName);
        }
        return chunkMap;
    }

    /**
     * 删除对象
     * @param bucketName
     * @param objectName
     * @return true/false
     */
    @SneakyThrows
    public static boolean removeObject(String bucketName,String objectName){
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
        return true;
    }

    /**
     * 批量删除对象
     * @param bucketName
     * @param objectNames
     * @return true/false
     */
    public static boolean removeObjects(String bucketName,List<String> objectNames){
        List<DeleteObject> objects = new LinkedList<>();
        for (String objectName : objectNames){
            objects.add(new DeleteObject(objectName));
        }
        Iterable<Result<DeleteError>> results =
                minioClient.removeObjects(
                        RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());
        for (Result<DeleteError> result : results) {
            DeleteError error = null;
            try {
                error = result.get();
            } catch (ErrorResponseException e) {
                e.printStackTrace();
            } catch (InsufficientDataException e) {
                e.printStackTrace();
            } catch (InternalException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidResponseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (XmlParserException e) {
                e.printStackTrace();
            }
            System.out.println(
                    "Error in deleting object " + error.objectName() + "; " + error.message());
        }
        return true;
    }


    /**
     * 合并分片文件成对象文件
     * @param chunkBucKetName 分片文件所在存储桶名称
     * @param composeBucketName 合并后的对象文件存储的存储桶名称
     * @param chunkNames 分片文件名称集合
     * @param objectName 合并后的对象文件名称
     * @return true/false
     */
    @SneakyThrows
    public static boolean composeObject(String chunkBucKetName,String composeBucketName,List<String> chunkNames,String objectName,boolean isDeleteChunkObject){
        if (null == chunkBucKetName){
            chunkBucKetName = chunkBucKet;
        }
        List<ComposeSource> sourceObjectList = new ArrayList<>(chunkNames.size());
        for (String chunk : chunkNames){
            sourceObjectList.add(
                    ComposeSource.builder()
                            .bucket(chunkBucKetName)
                            .object(chunk)
                            .build()
            );
        }
        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(composeBucketName)
                        .object(objectName)
                        .sources(sourceObjectList)
                        .build()
        );
        if(isDeleteChunkObject){
            removeObjects(chunkBucKetName,chunkNames);
        }
        return true;
    }

    /**
     * 合并分片文件成对象文件
     * @param bucketName 存储桶名称
     * @param chunkNames 分片文件名称集合
     * @param objectName 合并后的对象文件名称
     * @return true/false
     */
    public static boolean composeObject(String bucketName,List<String> chunkNames,String objectName){
        return composeObject(chunkBucKet,bucketName,chunkNames,objectName,NOT_DELETE_CHUNK_OBJECT);
    }

    /**
     * 合并分片文件成对象文件
     * @param bucketName 存储桶名称
     * @param chunkNames 分片文件名称集合
     * @param objectName 合并后的对象文件名称
     * @return true/false
     */
    public static boolean composeObject(String bucketName,List<String> chunkNames,String objectName,boolean isDeleteChunkObject){
        return composeObject(chunkBucKet,bucketName,chunkNames,objectName,isDeleteChunkObject);
    }

    /**
     * 合并分片文件，合并成功后删除分片文件
     * @param bucketName 存储桶名称
     * @param chunkNames 分片文件名称集合
     * @param objectName 合并后的对象文件名称
     * @return true/false
     */
    public static boolean composeObjectAndRemoveChunk(String bucketName,List<String> chunkNames,String objectName){
        return composeObject(chunkBucKet,bucketName,chunkNames,objectName,DELETE_CHUNK_OBJECT);
    }


    /**
     * 将分钟数转换为秒数
     * @param expiry 过期时间(分钟数)
     * @return expiry
     */
    private static int expiryHandle(Integer expiry){
        expiry = expiry * 60;
        if (expiry > 604800){
            return 604800;
        }
        return expiry;
    }

    static class Str2IntComparator implements Comparator<String> {
        private final boolean reverseOrder; // 是否倒序
        public Str2IntComparator(boolean reverseOrder) {
            this.reverseOrder = reverseOrder;
        }
        @Override
        public int compare(String arg0, String arg1) {
            Integer intArg0 = Integer.parseInt(arg0.substring(arg0.indexOf("/") + 1,arg0.lastIndexOf(".")));
            Integer intArg1 = Integer.parseInt(arg1.substring(arg1.indexOf("/") + 1,arg1.lastIndexOf(".")));
            if(reverseOrder) {
                return intArg1 - intArg0;
            } else {
                return intArg0 - intArg1;
            }
        }
    }

    /**
     * 上传文件到服务器
     * @param file
     * @param objectName 文件夹+文件名称
     * @throws Exception
     */
    public static void uploadFileMinIo(MultipartFile file, String objectName) throws Exception {

        InputStream imgInputStream = file.getInputStream();
        minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(chunkBucKet)
                        .object(objectName)
                        .stream(imgInputStream,imgInputStream.available(),-1)
                        .contentType(file.getContentType())
                        .build());
        imgInputStream.close();
    }
}
