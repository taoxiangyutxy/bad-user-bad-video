package com.ttt.one.waiguagg.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttt.one.common.exception.RRException;
import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;
import com.ttt.one.common.utils.R;
import com.ttt.one.common.utils.constant.InfoConstant;
import com.ttt.one.waiguagg.entity.GivelikeEntity;
import com.ttt.one.waiguagg.entity.UnmberEntity;
import com.ttt.one.waiguagg.fegin.EsSearchFeginServer;
import com.ttt.one.waiguagg.fegin.FileServer;
import com.ttt.one.waiguagg.fegin.ThirdPartyFeginServer;
import com.ttt.one.waiguagg.fegin.UserFeginServer;
import com.ttt.one.waiguagg.service.GivelikeService;
import com.ttt.one.waiguagg.service.UnmberService;
import com.ttt.one.waiguagg.vo.FileInfoVO;
import com.ttt.one.waiguagg.vo.SysUserVO;
import com.ttt.one.waiguagg.vo.VideoPreviewVO;
import com.ttt.one.waiguagg.vo.WaiGuaInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ttt.one.waiguagg.dao.InfoDao;
import com.ttt.one.waiguagg.entity.InfoEntity;
import com.ttt.one.waiguagg.service.InfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("infoService")
@Slf4j
public class InfoServiceImpl extends ServiceImpl<InfoDao, InfoEntity> implements InfoService {

    @Autowired
    private UnmberService unmberService;

    @Autowired
    private UserFeginServer userFeginServer;

    @Autowired
    private FileServer fileServer;
    @Autowired
    private ThirdPartyFeginServer thirdPartyFeginServer;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redisson;
    @Autowired
    private EsSearchFeginServer esSearchFeginServer;
    @Autowired
    private GivelikeService givelikeService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<InfoEntity> page = this.page(
                new Query<InfoEntity>().getPage(params),
                new QueryWrapper<InfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageAll(Map<String, Object> params) {
        QueryWrapper<InfoEntity> wrapper = new QueryWrapper<InfoEntity>().eq("status", Constant.STATUS_0);
        String key = (String) params.get("key");
        //是否关键字查询
        if(!StringUtils.isEmpty(key)){
            wrapper.like("waigua_username",key);
        }
        IPage<InfoEntity> page =
                this.page(
                new Query<InfoEntity>().getPage(params),
                new QueryWrapper<InfoEntity>()
        );
        PageUtils pageUtils = new PageUtils(page);
        /**
         * 当前登录用户
         */
        Long currentUser = 1L;
        /**
         * 查询集合 sql拼接是否点赞
         */
        List<InfoEntity> records = this.baseMapper.findListAll(key,null,currentUser); //page.getRecords();
        /**
         * 数据汇总
         */
        List<WaiGuaInfoVO> collect = records.stream().map(infoEntity -> {
            WaiGuaInfoVO waiGuaInfoVO = new WaiGuaInfoVO();
            BeanUtils.copyProperties(infoEntity, waiGuaInfoVO);
            if(null!=infoEntity.getWaiguaType()){
                waiGuaInfoVO.setWaiguaType(infoEntity.getWaiguaType().split(","));
            }
            //查询外挂账号信息
            UnmberEntity unmberEntity = unmberService.getById(infoEntity.getWaiguaId());
            if (unmberEntity != null) {
                BeanUtils.copyProperties(unmberEntity, waiGuaInfoVO);
            }
            //远程查询举报用户名称  TODO 人人开源的远程调用 404
           /* R r = userFeginServer.infoById(infoEntity.getReportuserId());
            if (r.getCode() == 0) {
                SysUserVO user = r.getData("user", new TypeReference<SysUserVO>() {
                });
                waiGuaInfoVO.setReportuserName(user.getUsername());
            } else {
                log.error("远程查询库存信息异常:{}");
            }*/
            waiGuaInfoVO.setReportuserName(infoEntity.getReportuserId()+"");
            waiGuaInfoVO.setWaiguaInfoId(infoEntity.getId());
            /**
             *汇总缓存点赞数
             */
            Long countRelationLike = countRelationLike(infoEntity.getId());
            Long countRelationLikeDb = 0L;
            if(infoEntity.getThumbUpNumber()!=null){
                countRelationLikeDb =  Long.valueOf(infoEntity.getThumbUpNumber());
            }
            Integer coutLike = Math.toIntExact(countRelationLike + countRelationLikeDb);
            waiGuaInfoVO.setThumbUpNumber(coutLike);
            /**
             * 缓存是否点过赞了
             */
            Integer isSupport = whetherThumbUp(infoEntity.getId(), currentUser, 1);
            if(isSupport!=null){
                waiGuaInfoVO.setIsSupport(isSupport);
            }
            return waiGuaInfoVO;
        }).collect(Collectors.toList());

        pageUtils.setList(collect);
        return pageUtils;
    }
    @Transactional
    @Override
    public void saveUnmberAndInfo(WaiGuaInfoVO waiGuaInfoVO) {
        //1 外挂账号是否存在 根据名字  存在更新数据
       UnmberEntity unmberEntity =  unmberService.getByName(waiGuaInfoVO.getWaiguaUsername());
       if(unmberEntity!=null){ //更新
           unmberService.updateById(unmberEntity);
       }else{//新增
           unmberEntity = new UnmberEntity();
           BeanUtils.copyProperties(waiGuaInfoVO,unmberEntity);
           unmberService.save(unmberEntity);
       }
        //2 新增info信息
        InfoEntity infoEntity = new InfoEntity();
        BeanUtils.copyProperties(waiGuaInfoVO,infoEntity);
        infoEntity.setWaiguaId(unmberEntity.getId());
        infoEntity.setWaiguaType(StringUtils.arrayToDelimitedString(waiGuaInfoVO.getWaiguaType(),","));
        infoEntity.setStatus(Constant.STATUS_0);
        infoEntity.setThumbUpNumber(0);
        infoEntity.setReadNumber(0);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdataTime(infoEntity.getCreateTime());
        infoEntity.setReviewStatus(Constant.REVIEWSTATUS_0);
        this.save(infoEntity);
    }

    @Override
    public WaiGuaInfoVO getByIdAndUnmber(Long id) {
        WaiGuaInfoVO waiGuaInfoVO = new WaiGuaInfoVO();
        /**
         * 当前登录用户
         */
        Long currentUser = 1L;
        //1 根据id查询info信息
        InfoEntity infoEntity = this.baseMapper.getByIdAndCuser(id,currentUser);
        BeanUtils.copyProperties(infoEntity,waiGuaInfoVO);
        if(null!=infoEntity.getWaiguaType()){
            waiGuaInfoVO.setWaiguaType(infoEntity.getWaiguaType().split(","));
        }
        waiGuaInfoVO.setWaiguaInfoId(id);
        //2 根据id查出外挂账号
        UnmberEntity unmberEntity = unmberService.getById(infoEntity.getWaiguaId());
        BeanUtils.copyProperties(unmberEntity,waiGuaInfoVO);
        /**
         *汇总缓存点赞数
         */
        Long countRelationLike = countRelationLike(infoEntity.getId());
        Long countRelationLikeDb = 0L;
        if(infoEntity.getThumbUpNumber()!=null){
            countRelationLikeDb =  Long.valueOf(infoEntity.getThumbUpNumber());
        }
        Integer coutLike = Math.toIntExact(countRelationLike + countRelationLikeDb);
        waiGuaInfoVO.setThumbUpNumber(coutLike);
        /**
         * 缓存 是否点过赞了
         */
        Integer isSupport = whetherThumbUp(infoEntity.getId(), currentUser, 1);
        if(isSupport!=null){
            waiGuaInfoVO.setIsSupport(isSupport);
        }
        /**
         * 获取视频链接
         */
        R r = fileServer.videoInfo(infoEntity.getId());
        if (r.getCode() == 0) { //远程服务调用成功
            List<FileInfoVO> fileList = r.getData("fileList", new TypeReference<List<FileInfoVO>>() {
            });
            if (fileList != null && fileList.size() > 0) {
                waiGuaInfoVO.setLocation(fileList.get(0).getLocation());
            }
        } else {
            log.error("远程服务调用失败--- fileServer.videoInfo");
        }
        //3 合并返回
        return waiGuaInfoVO;
    }

    @Transactional
    @Override
    public void updateByIdAndUnmber(WaiGuaInfoVO waiGuaInfoVO) {
        InfoEntity infoEntity = new InfoEntity();
        BeanUtils.copyProperties(waiGuaInfoVO,infoEntity);
        infoEntity.setId(waiGuaInfoVO.getWaiguaInfoId());

        infoEntity.setWaiguaType(StringUtils.arrayToDelimitedString(waiGuaInfoVO.getWaiguaType(),","));
        this.updateById(infoEntity);
        UnmberEntity unmberEntity = new UnmberEntity();
        BeanUtils.copyProperties(waiGuaInfoVO,unmberEntity);
        unmberEntity.setId(waiGuaInfoVO.getWaiguaId());
        unmberService.updateById(unmberEntity);
    }

    @Override
    public void removeByIdsAllIn(List<Long> asList) {
        if(asList.size()>0){
            for (Long aLong : asList) {
                //1 查出info数据
                InfoEntity infoEntity = this.getById(aLong);
                //2 根据外挂id 删除外挂信息
                unmberService.removeById(infoEntity.getWaiguaId());
                //3 关联视频文件信息、分片表全删   远程调用
                try {
                    fileServer.deleAllIn(infoEntity.getId());
                }catch (Exception e){
                    log.error("调用文件上传远程服务fileServer.deleAllIn报错:{}",e);
                }
                //4 根据info id删info信息
                this.removeById(infoEntity.getId());
                //5 TODO 使用工具删除 MINIO服务器视频文件

                //删除后  门户网站数据少一条  将缓存清空
                redisTemplate.delete("allWaiGuaData");
            }
        }
    }

    @Override
    public PageUtils queryPageAllByReview(Map<String, Object> params, Long reviewVal) {
        //TODO  管理员是不是可以看所有状态下的呢？ 返回待审核的
        QueryWrapper<InfoEntity> wrapper = new QueryWrapper<InfoEntity>().eq("status", Constant.STATUS_0).eq("review_status",Constant.REVIEWSTATUS_0);
        String key = (String) params.get("key");
        //是否关键字查询
        if(!StringUtils.isEmpty(key)){
            wrapper.like("waigua_username",key);
        }
        IPage<InfoEntity> page = this.page(
                new Query<InfoEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        //获取查出来的记录数据
        List<InfoEntity> records = page.getRecords();
        List<WaiGuaInfoVO> collect = records.stream().map(infoEntity -> {
            WaiGuaInfoVO waiGuaInfoVO = new WaiGuaInfoVO();
            BeanUtils.copyProperties(infoEntity, waiGuaInfoVO);
            //查询外挂账号信息
            UnmberEntity unmberEntity = unmberService.getById(infoEntity.getWaiguaId());
            if (unmberEntity != null) {
                BeanUtils.copyProperties(unmberEntity, waiGuaInfoVO);
            }
            waiGuaInfoVO.setReportuserName(infoEntity.getReportuserId()+"");
            waiGuaInfoVO.setWaiguaInfoId(infoEntity.getId());
            return waiGuaInfoVO;
        }).collect(Collectors.toList());
        pageUtils.setList(collect);
        return pageUtils;
    }

    @Override
    @Transactional
    public void updateByIdAndReview(WaiGuaInfoVO waiGuaInfoVO) {
        InfoEntity infoEntity = new InfoEntity();
        infoEntity.setId(waiGuaInfoVO.getWaiguaInfoId());
        infoEntity.setReviewStatus(waiGuaInfoVO.getReviewStatus());
        //审核通过 调用第三方服务 发送短信通知
        if(waiGuaInfoVO.getReviewStatus()!= 0 && waiGuaInfoVO.getReviewStatus()!=1){
            String code = "";
            if(waiGuaInfoVO.getReviewStatus()==2){
                code="审核通过";
                //获取info信息
                WaiGuaInfoVO infoVO = this.getByIdAndUnmber(waiGuaInfoVO.getWaiguaInfoId());
                //调用远程服务 获取视频路径
                R rFile = fileServer.videoInfo(infoEntity.getId());
                if (rFile.getCode() == 0) { //远程服务调用成功
                    List<FileInfoVO> fileList = rFile.getData("fileList", new TypeReference<List<FileInfoVO>>() {
                    });
                    if (fileList != null && fileList.size() > 0) {
                        infoVO.setLocation(fileList.get(0).getLocation());
                    }
                } else {
                    log.error("远程服务调用失败--- fileServer.videoInfo");
                }
                //存入ES数据
                WaiguaEsModel waiguaEsModel = new WaiguaEsModel();
                waiguaEsModel.setInfoId(infoVO.getWaiguaInfoId());
                waiguaEsModel.setWaiguaType(StringUtils.arrayToDelimitedString(infoVO.getWaiguaType()," "));
                waiguaEsModel.setWaiguaDescribe(infoVO.getWaiguaDescribe());
                waiguaEsModel.setCreateTime(infoVO.getCreateTime());
                waiguaEsModel.setLocation(infoVO.getLocation());
                waiguaEsModel.setWaiguaUsername(infoVO.getWaiguaUsername());
                R r = esSearchFeginServer.waiguaInfoSaveES(waiguaEsModel);
                if(r.getCode() == 0){
                    //成功
                    //审核通过  门户网站数据多一条  将缓存清空
                    redisTemplate.delete("allWaiGuaData");
                    this.updateById(infoEntity);
                }else{
                    //失败
                    log.error("远程服务调用失败:{esSearchFeginServer.waiguaInfoSaveES}");
                }

            }else if(waiGuaInfoVO.getReviewStatus()==3){
                code="驳回";
                this.updateById(infoEntity);
            }
            //todo 手机号关联用户id 查   没钱发短信啦
          //  R r = thirdPartyFeginServer.sendCode("18753571460", code);

        }

    }

    @Override
    public List<VideoPreviewVO> videolistByInfoId(Long id) {
        List<VideoPreviewVO> voList = new ArrayList<>();
        R r = fileServer.videoInfo(id);
        if(r.getCode()==0){//远程服务 成功
            List<FileInfoVO> fileList = r.getData("fileList", new TypeReference<List<FileInfoVO>>() {
            });
            InfoEntity infoEntity = this.getById(id);
            for (FileInfoVO fileInfoVO : fileList) {
                VideoPreviewVO videoPreviewVO = new VideoPreviewVO();
                videoPreviewVO.setCreateTime(fileInfoVO.getCreateTime());
                videoPreviewVO.setWaiguaDescribe(infoEntity.getWaiguaDescribe());
                // 组合前台预览url   /static/video/ttt.mp4
                //videoPreviewVO.setMovie("/static/video/"+fileInfoVO.getIdentifier()+"/"+fileInfoVO.getFilename());
                videoPreviewVO.setMovie(fileInfoVO.getLocation());
                voList.add(videoPreviewVO);
            }
        }else{
            log.error("调用远程服务fileServer.videoInfo失败");
        }
        return voList;
    }

    @Override
    public  List<WaiGuaInfoVO>  pageAllWaiGua(Map<String, Object> params) {
        String s = redisTemplate.opsForValue().get("allWaiGuaData");
        if(StringUtils.isEmpty(s)){
            List<WaiGuaInfoVO> data = getAllWaiGuaData();
            return data;
        }
        System.out.println("缓存命中，直接返回");
        List<WaiGuaInfoVO> listMap = JSON.parseObject(s, new TypeReference<List<WaiGuaInfoVO>>(){});
        return listMap;
    }

    @Override
    public void giveLikeInfo(Long relationId, Long likedUserId, Integer type) {
        validateParam(relationId,likedUserId);
        log.info("点赞数据存入redis开始，relationId:{}，likedUserId:{}", relationId, likedUserId);
        likeValidate(relationId,likedUserId,type);
        synchronized (this){
            //1.用户点赞评论记录
            redisTemplate.opsForHash().put(InfoConstant.INFO_LIKED_USER_KEY,relationId+"::"+likedUserId, "1");
            //2.评论点赞数+1
            String relationLikedResult = (String) redisTemplate.opsForHash().get(InfoConstant.TOTAL_LIKE_COUNT_KEY, String.valueOf(relationId));
            Long likeCount = relationLikedResult == null ? 0L : Long.parseLong(relationLikedResult);
            redisTemplate.opsForHash().put(InfoConstant.TOTAL_LIKE_COUNT_KEY, String.valueOf(relationId), (likeCount+1L)+"");
            log.info("点赞数据存入redis结束，relationId:{}，likedUserId:{}", relationId, likedUserId);
        }
    }

    @Override
    public void unGiveLikeInfo(Long relationId, Long likedUserId, Integer type) {
        validateParam(relationId,likedUserId);
        log.info("取消点赞数据存入redis开始，relationId:{}，likedUserId:{}", relationId, likedUserId);
        unLikeValidate(relationId,likedUserId,type);
        synchronized (this){
            //1.评论点赞数-1
            String relationLikedCountResult = (String) redisTemplate.opsForHash().get(InfoConstant.TOTAL_LIKE_COUNT_KEY, String.valueOf(relationId));
            Long likeCount = relationLikedCountResult == null ? 0L :Long.parseLong(relationLikedCountResult);
            likeCount = likeCount - 1L;
            redisTemplate.opsForHash().put(InfoConstant.TOTAL_LIKE_COUNT_KEY, String.valueOf(relationId),likeCount+"");

            //2.修改用户点赞评论记录
            redisTemplate.opsForHash().put(InfoConstant.INFO_LIKED_USER_KEY,relationId+"::"+likedUserId,"0");
            log.info("取消点赞数据存入redis结束，relationId:{}，likedUserId:{}", relationId, likedUserId);
        }
    }

    @Override
    public void redisDataToMysql() {
        //1.更新评论点赞记录表
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(InfoConstant.INFO_LIKED_USER_KEY);
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String)entry.getValue();
            String[] split = key.split("::");
            GivelikeEntity givelikeEntity = new GivelikeEntity();
            givelikeEntity.setType(0);
            givelikeEntity.setRelationId(Long.parseLong(split[0]));
            givelikeEntity.setUserId(Long.parseLong(split[1]));
            QueryWrapper<GivelikeEntity> queryWrapper = new QueryWrapper<GivelikeEntity>()
                    .eq("relation_id", givelikeEntity.getRelationId()).eq("user_id", givelikeEntity.getUserId())
                    .eq("type", givelikeEntity.getType()).eq("del_flag", Constant.STATUS_0);
            Integer count  = givelikeService.getBaseMapper().selectCount(queryWrapper);
            if(value.equals("1")){//是点赞记录
                if(count==0) {//避免重复点赞
                    givelikeEntity.setDelFlag(0);
                    givelikeEntity.setCreateTime(new Date());
                    givelikeService.save(givelikeEntity);
                }
            }else{//取消点赞记录
                if(count>0){
                    givelikeService.remove(queryWrapper);
                }
            }
        }
        //2.更新评论点赞总数
        Map<Object, Object> entriesTotal = redisTemplate.opsForHash().entries(InfoConstant.TOTAL_LIKE_COUNT_KEY);
        for (Map.Entry<Object, Object> entry : entriesTotal.entrySet()) {
            String key = (String) entry.getKey();
            String value =  (String) entry.getValue();
            InfoEntity infoEntity = this.baseMapper.selectById(Long.parseLong(key));
            if(infoEntity!=null){
                infoEntity.setThumbUpNumber(infoEntity.getThumbUpNumber()+Integer.parseInt(value));
                this.updateById(infoEntity);
            }
        }
        /**
         * 结束后 清空记录数据
         */
        redisTemplate.delete(InfoConstant.INFO_LIKED_USER_KEY);
        redisTemplate.delete(InfoConstant.TOTAL_LIKE_COUNT_KEY);
    }

    /**
     *  描述: 统计评论的总点赞数
     * @param relationId:
     * @return Long
     * @author txy
     * @description
     * @date 2021/11/9 16:02
     */
    public synchronized Long countRelationLike(Long relationId) {
        validateParam(relationId);
        String relationLikedResult = (String) redisTemplate.opsForHash().get(InfoConstant.TOTAL_LIKE_COUNT_KEY, String.valueOf(relationId));
        Long likeCount = 0L;
        if(!org.apache.commons.lang3.StringUtils.isEmpty(relationLikedResult)){
            likeCount = Long.parseLong(relationLikedResult);
            if (likeCount == null) {
                return 0L;
            }
        }
        return likeCount;
    }
    /**
     *  描述: 该用户对该评论是否点过赞
     * @param relationId:
     * @param likedUserId:
     * @param type:
     * @return Integer
     * @author txy
     * @description
     * @date 2021/11/22 14:41
     */
    public  Integer whetherThumbUp(Long relationId,Long likedUserId,Integer type) {
        /**
         * 先去缓存  缓存有 直接按缓存的 return
         */
        //获取缓存值
        String value =(String) redisTemplate.opsForHash()
                .get(InfoConstant.INFO_LIKED_USER_KEY, relationId + "::" + likedUserId);
        if(!org.apache.commons.lang3.StringUtils.isEmpty(value)){
            return Integer.valueOf(value);
        }
        /**
         * 缓存没有  去数据库查
         */
        /*likeCount  = givelikeService.getBaseMapper().selectCount(new QueryWrapper<GivelikeEntity>()
                .eq("relation_id",relationId).eq("user_id",likedUserId)
                .eq("type",type).eq("del_flag",Constant.STATUS_0));*/
        return null;
    }

    /**
     *  描述: 点赞逻辑校验: 查该条记录  有则点过赞了
     * @param relationId: 被点赞对象id
     * @param likedUserId: 点赞用户
     * @param type: 点赞对象类型
     * @return void
     * @author txy
     * @description
     * @date 2021/11/9 16:03
     */
    private void likeValidate(Long relationId, Long likedUserId,Integer type) {
        /**
         * 查数据库
         */
        Integer count  = givelikeService.getBaseMapper().selectCount(new QueryWrapper<GivelikeEntity>()
        .eq("relation_id",relationId).eq("user_id",likedUserId)
                .eq("type",type).eq("del_flag",Constant.STATUS_0));
        //获取缓存值
        String value =(String) redisTemplate.opsForHash()
                .get(InfoConstant.INFO_LIKED_USER_KEY, relationId + "::" + likedUserId);
        if(count>0){//库里有
            if(!org.apache.commons.lang3.StringUtils.isEmpty(value)){
                if(value.equals("1")){//点过了
                    log.error("数据库有但是缓存取消了又点过攒了，relationId:{}，likedUserId:{}", relationId, likedUserId);
                    throw new RRException("该评论已被当前用户点赞，重复点赞!");
                }
            }else{
                log.error("数据库有，relationId:{}，likedUserId:{}", relationId, likedUserId);
                throw new RRException("该评论已被当前用户点赞，重复点赞!");
            }
        }else{
            /**
             * 查缓存
             */
            if(!org.apache.commons.lang3.StringUtils.isEmpty(value)){
                if(value.equals("1")){// 点过赞了
                    log.error("缓存有并点过了，relationId:{}，likedUserId:{}", relationId, likedUserId);
                    throw new RRException("该评论已被当前用户点赞，重复点赞!");
                }
            }
        }
    }

    /**
     *  描述: 点赞逻辑校验: 查该条记录  无则未点过赞
     * @param relationId: 被点赞对象id
     * @param likedUserId: 点赞用户
     * @param type: 点赞对象类型
     * @return void
     * @author txy
     * @description
     * @date 2021/11/9 16:03
     */
    private void unLikeValidate(Long relationId, Long likedUserId,Integer type) {
        /**
         * 查数据库
         */
        Integer count  = givelikeService.getBaseMapper().selectCount(new QueryWrapper<GivelikeEntity>()
                .eq("relation_id",relationId).eq("user_id",likedUserId)
                .eq("type",type).eq("del_flag",Constant.STATUS_0));
        String value =(String) redisTemplate.opsForHash()
                .get(InfoConstant.INFO_LIKED_USER_KEY, relationId + "::" + likedUserId);
        if(count == 0 ){
            log.error("数据库没有，relationId:{}，likedUserId:{}", relationId, likedUserId);
            /**
             * 查缓存
             */
            boolean b = redisTemplate.opsForHash()
                    .hasKey(InfoConstant.INFO_LIKED_USER_KEY, relationId + "::" + likedUserId);
            if(!b){
                log.error("缓存没有，commentId:{}，likedUserId:{}", relationId, likedUserId);
                throw new RRException("该评论未被当前用户点赞，不可以进行取消点赞操作!");
            }else {
                if(value.equals("0")){
                    log.error("缓存有但是取消了已经，commentId:{}，likedUserId:{}", relationId, likedUserId);
                    throw new RRException("该评论未被当前用户点赞，不可以进行取消点赞操作!");
                }
            }
        }else{//库里有
            if(!org.apache.commons.lang3.StringUtils.isBlank(value)){
                if(value.equals("0")){//但是缓存已经取消过了
                    throw new RRException("该评论已经取消点赞，不可以进行取消点赞操作!");
                }
            }
        }
    }

    /**
     *  描述: 入参验证
     * @param params:
     * @return void
     * @author txy
     * @description
     * @date 2021/11/9 16:03
     */
    private void validateParam(Long... params) {
        for (Long param : params) {
            if (null == param) {
                log.error("入参存在null值");
                throw new RRException("参数不能为null!");
            }
        }
    }

    private List<WaiGuaInfoVO> getAllWaiGuaData(){
        //获取锁,10秒后自动解锁
        RLock lock = redisson.getLock("waiguaData-lock");
        lock.lock(10, TimeUnit.SECONDS);
        System.out.println("获取分布式锁成功..");
        List<WaiGuaInfoVO> collect = new ArrayList<>();
        try {
            /*QueryWrapper<InfoEntity> wrapper = new QueryWrapper<InfoEntity>().eq("status", Constant.STATUS_0);
            wrapper.eq("review_status",Constant.REVIEWSTATUS_2);
            List<InfoEntity> infoEntities = this.list(wrapper);*/
            /**
             * 当前登录用户
             */
            Long currentUser = 1L;
            /**
             * 查询集合 sql拼接是否点赞
             */
            List<InfoEntity> infoEntities = this.baseMapper.findListAll(null,"2",currentUser);
             collect = infoEntities.stream().map(infoEntity -> {
                WaiGuaInfoVO waiGuaInfoVO = new WaiGuaInfoVO();
                BeanUtils.copyProperties(infoEntity, waiGuaInfoVO);
                 /**
                  * 外挂信息主键 注入
                  */
                waiGuaInfoVO.setWaiguaInfoId(infoEntity.getId());
                waiGuaInfoVO.setWaiguaType(infoEntity.getWaiguaType().split(","));
                 /**
                  *汇总缓存点赞数
                  */
                 Long countRelationLike = countRelationLike(infoEntity.getId());
                 Long countRelationLikeDb = 0L;
                 if(infoEntity.getThumbUpNumber()!=null){
                     countRelationLikeDb =  Long.valueOf(infoEntity.getThumbUpNumber());
                 }
                 Integer coutLike = Math.toIntExact(countRelationLike + countRelationLikeDb);
                 waiGuaInfoVO.setThumbUpNumber(coutLike);
                 /**
                  * 缓存是否点过赞了
                  */
                 Integer isSupport = whetherThumbUp(infoEntity.getId(), currentUser, 1);
                 if(isSupport!=null){
                     waiGuaInfoVO.setIsSupport(isSupport);
                 }
                 /**
                  * 获取视频链接
                  */
                R r = fileServer.videoInfo(infoEntity.getId());
                if (r.getCode() == 0) { //远程服务调用成功
                    List<FileInfoVO> fileList = r.getData("fileList", new TypeReference<List<FileInfoVO>>() {
                    });
                    if (fileList != null && fileList.size() > 0) {
                        waiGuaInfoVO.setLocation(fileList.get(0).getLocation());
                    }
                } else {
                    log.error("远程服务调用失败--- fileServer.videoInfo");
                }
                return waiGuaInfoVO;
            }).collect(Collectors.toList());
             //加入缓存
             String s = JSON.toJSONString(collect);
             redisTemplate.opsForValue().set("allWaiGuaData",s,1,TimeUnit.DAYS);
        }finally {
            lock.unlock();
        }
        return collect;
    }
}