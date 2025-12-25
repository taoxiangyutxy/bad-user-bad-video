package com.ttt.one.waiguagg.service.impl;

//import com.alibaba.csp.sentinel.Entry;
//import com.alibaba.csp.sentinel.SphU;
//import com.alibaba.csp.sentinel.Tracer;
//import com.alibaba.csp.sentinel.annotation.SentinelResource;
//import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ttt.one.common.exception.BizException;
import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;
import com.ttt.one.common.utils.R;
import com.ttt.one.common.utils.constant.InfoConstant;
import com.ttt.one.waiguagg.dto.InfoDTO;
import com.ttt.one.waiguagg.entity.CommentEntity;
import com.ttt.one.waiguagg.entity.GivelikeEntity;
import com.ttt.one.waiguagg.entity.UnmberEntity;
import com.ttt.one.waiguagg.feign.EsSearchFeignServer;
import com.ttt.one.waiguagg.feign.FileServer;
import com.ttt.one.waiguagg.feign.ThirdPartyFeignServer;
import com.ttt.one.waiguagg.feign.UserFeignServer;
import com.ttt.one.waiguagg.service.CommentService;
import com.ttt.one.waiguagg.service.GivelikeService;
import com.ttt.one.waiguagg.service.UnmberService;
import com.ttt.one.waiguagg.utils.GGFileUtil;
import com.ttt.one.waiguagg.vo.FileInfoVO;
import com.ttt.one.waiguagg.vo.VideoPreviewVO;
import com.ttt.one.waiguagg.vo.WaiGuaInfoVO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ttt.one.waiguagg.dao.InfoDao;
import com.ttt.one.waiguagg.entity.InfoEntity;
import com.ttt.one.waiguagg.service.InfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service("infoService")
@Slf4j
public class InfoServiceImpl extends ServiceImpl<InfoDao, InfoEntity> implements InfoService {
    private Logger logger = LoggerFactory.getLogger(InfoServiceImpl.class);
    @Autowired
    private UnmberService unmberService;

    @Qualifier("com.ttt.one.waiguagg.feign.UserFeignServer")
    @Autowired
    private UserFeignServer userFeignServer;

    @Qualifier("com.ttt.one.waiguagg.feign.FileServer")
    @Autowired
    private FileServer fileServer;
    @Qualifier("com.ttt.one.waiguagg.feign.ThirdPartyFeignServer")
    @Autowired
    private ThirdPartyFeignServer thirdPartyFeignServer;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redisson;
    @Qualifier("com.ttt.one.waiguagg.feign.EsSearchFeignServer")
    @Autowired
    private EsSearchFeignServer esSearchFeignServer;
    @Autowired
    private GivelikeService givelikeService;
    @Autowired
    private CommentService commentService;
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
        List<InfoEntity> records = this.baseMapper.findListAll(key,null,currentUser,Constant.LIKETYPE_INFO); //page.getRecords();
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
            Long countRelationLike = countRelationLike(infoEntity.getId(),Constant.LIKETYPE_INFO);
            Long countRelationLikeDb = 0L;
            if(infoEntity.getThumbUpNumber()!=null){
                countRelationLikeDb =  Long.valueOf(infoEntity.getThumbUpNumber());
            }
            Integer coutLike = Math.toIntExact(countRelationLike + countRelationLikeDb);
            waiGuaInfoVO.setThumbUpNumber(coutLike);
            /**
             * 缓存是否点过赞了
             */
            Integer isSupport = whetherThumbUp(infoEntity.getId(), currentUser, Constant.LIKETYPE_INFO);
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
//    public WaiGuaInfoVO blockHandlerttt(Long id,Long currentUser,BlockException e) {
//        log.error("tttWaiGuaInfoResource被限流了..");
//        return null;
//    }
  //  @SentinelResource(value = "tttWaiGuaInfoResource",blockHandler = "blockHandlerttt")
    @Override
    public WaiGuaInfoVO getByIdAndUnmber(Long id,Long currentUser) {
       // Entry entry = null;
        WaiGuaInfoVO waiGuaInfoVO = new WaiGuaInfoVO();
        // 务必保证 finally 会被执行
        try {
            // 资源名可使用任意有业务语义的字符串，注意数目不能太多（超过 1K），超出几千请作为参数传入而不要直接作为资源名
            // EntryType 代表流量类型（inbound/outbound），其中系统规则只对 IN 类型的埋点生效
        //    entry = SphU.entry("tttWaiGuaInfo");
            // 被保护的业务逻辑
            // do something...
            //1 根据id查询info信息  type 1 是信息
            InfoEntity infoEntity = this.baseMapper.getByIdAndCuser(id,currentUser,Constant.LIKETYPE_INFO);
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
            Long countRelationLike = countRelationLike(infoEntity.getId(),Constant.LIKETYPE_INFO);
            Long countRelationLikeDb = 0L;
            if(infoEntity.getThumbUpNumber()!=null){
                countRelationLikeDb =  Long.valueOf(infoEntity.getThumbUpNumber());
            }
            Integer coutLike = Math.toIntExact(countRelationLike + countRelationLikeDb);
            waiGuaInfoVO.setThumbUpNumber(coutLike);
            /**
             * 缓存 是否点过赞了
             */
            Integer isSupport = whetherThumbUp(infoEntity.getId(), currentUser, Constant.LIKETYPE_INFO);
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
                log.error("远程服务调用失败--- fileServer.videoInfo"+r.getMsg());
            }
            /**
             * 该info信息有多少条评论
             */
            int commentCount = commentService.count(new QueryWrapper<CommentEntity>().eq("info_id", id));
            waiGuaInfoVO.setCommentConut(commentCount);
        }
//        catch (BlockException ex) {
//            // 资源访问阻止，被限流或被降级
//            // 进行相应的处理操作
//            log.error("资源访问被阻止原因:{}",ex.getMessage());
//        }
        catch (Exception ex) {
            // 若需要配置降级规则，需要通过这种方式记录业务异常
        //    Tracer.traceEntry(ex, entry);
            ex.printStackTrace();
        }
//        finally {
//            // 务必保证 exit，务必保证每个 entry 与 exit 配对
//            if (entry != null) {
//                entry.exit();
//            }
//        }
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

    @GlobalTransactional
    //@Transactional
    @Override
    public void removeByIdsAllIn(List<Long> asList) {
        if(asList.size()>0){
            for (Long aLong : asList) {
                //1 查出info数据
                InfoEntity infoEntity = this.getById(aLong);
                //测试分布式事务 seata
                /*UserEntity user = new UserEntity();
                user.setId(4L);
                user.setUsername("taoxiangyu444");
                userFeignServer.update(user);*/
                //2 根据外挂id 删除外挂信息
                unmberService.removeByIdWithTransaction(infoEntity.getWaiguaId());
                //3 关联视频文件信息、分片表全删   远程调用
                try {
                    R r = fileServer.deleAllIn(infoEntity.getId());
                    if(r.getCode() == 0){
                        log.info("远程服务调用成功--- fileServer.deleAllIn");
                    }else {
                        log.error("远程服务调用失败--- fileServer.deleAllIn"+r.getMsg());
                        throw new BizException("调用文件上传远程服务fileServer.deleAllIn报错"+r.getMsg());
                    }
                }catch (Exception e){
                    log.error("调用文件上传远程服务fileServer.deleAllIn报错:{}",e);
                    throw new BizException("调用文件上传远程服务fileServer.deleAllIn报错"+e.getMessage());
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
                //获取info信息  这里更新用不到当前登录用户id
                WaiGuaInfoVO infoVO = this.getByIdAndUnmber(waiGuaInfoVO.getWaiguaInfoId(),-1L);
                //调用远程服务 获取视频路径
                R rFile = fileServer.videoInfo(infoEntity.getId());
                if (rFile.getCode() == 0) { //远程服务调用成功
                    List<FileInfoVO> fileList = rFile.getData("fileList", new TypeReference<List<FileInfoVO>>() {
                    });
                    if (fileList != null && fileList.size() > 0) {
                        infoVO.setLocation(fileList.get(0).getLocation());
                    }
                } else {
                    log.error("远程服务调用失败--- fileServer.videoInfo"+rFile.getMsg());
                }
                //存入ES数据
                WaiguaEsModel waiguaEsModel = new WaiguaEsModel();
                waiguaEsModel.setInfoId(infoVO.getWaiguaInfoId());
                waiguaEsModel.setWaiguaType(StringUtils.arrayToDelimitedString(infoVO.getWaiguaType()," "));
                waiguaEsModel.setWaiguaDescribe(infoVO.getWaiguaDescribe());
                waiguaEsModel.setCreateTime(infoVO.getCreateTime());
                waiguaEsModel.setLocation(infoVO.getLocation());
                waiguaEsModel.setWaiguaUsername(infoVO.getWaiguaUsername());
                R r = esSearchFeignServer.waiguaInfoSaveES(waiguaEsModel);
                if(r.getCode() == 0){
                    //成功
                    //审核通过  门户网站数据多一条  将缓存清空
                    redisTemplate.delete("allWaiGuaData");
                    this.updateById(infoEntity);
                }else{
                    //失败
                    log.error("远程服务调用失败:{esSearchFeginServer.waiguaInfoSaveES}"+r.getMsg());
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
            log.error("调用远程服务fileServer.videoInfo失败"+r.getMsg());
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
            redisTemplate.opsForHash().put(InfoConstant.INFO_LIKED_USER_KEY,relationId+"::"+likedUserId+"::"+type, "1");
            //2.评论点赞数+1
            String relationLikedResult = (String) redisTemplate.opsForHash().get(InfoConstant.TOTAL_LIKE_COUNT_KEY, relationId+"::"+type);
            Long likeCount = relationLikedResult == null ? 0L : Long.parseLong(relationLikedResult);
            redisTemplate.opsForHash().put(InfoConstant.TOTAL_LIKE_COUNT_KEY, relationId+"::"+type, (likeCount+1L)+"");
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
            String relationLikedCountResult = (String) redisTemplate.opsForHash().get(InfoConstant.TOTAL_LIKE_COUNT_KEY, relationId+"::"+type);
            Long likeCount = relationLikedCountResult == null ? 0L :Long.parseLong(relationLikedCountResult);
            likeCount = likeCount - 1L;
            redisTemplate.opsForHash().put(InfoConstant.TOTAL_LIKE_COUNT_KEY, relationId+"::"+type,likeCount+"");

            //2.修改用户点赞评论记录
            redisTemplate.opsForHash().put(InfoConstant.INFO_LIKED_USER_KEY,relationId+"::"+likedUserId+"::"+type,"0");
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
            givelikeEntity.setType(Integer.parseInt(split[2]));
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
        //2.更新评论点赞总数 根据类型更新不同表
        Map<Object, Object> entriesTotal = redisTemplate.opsForHash().entries(InfoConstant.TOTAL_LIKE_COUNT_KEY);
        for (Map.Entry<Object, Object> entry : entriesTotal.entrySet()) {
            String key = (String) entry.getKey();
            String value =  (String) entry.getValue();
            // 0是关联id   1是类型
            String[] split = key.split("::");
            if(Integer.parseInt(split[1])==Constant.LIKETYPE_INFO){//更新info表
                InfoEntity infoEntity = this.baseMapper.selectById(Long.parseLong(split[0]));
                if(infoEntity!=null){
                    infoEntity.setThumbUpNumber(infoEntity.getThumbUpNumber()+Integer.parseInt(value));
                    this.updateById(infoEntity);
                }
            }else if(Integer.parseInt(split[1])==Constant.LIKETYPE_COMMENT){//更新评论表
                CommentEntity commentEntity = commentService.getById(Long.parseLong(split[0]));
                if(commentEntity!=null){
                    commentEntity.setThumbUpNumber(commentEntity.getThumbUpNumber()+Integer.parseInt(value));
                    commentService.updateById(commentEntity);
                }
            }

        }
        /**
         * 结束后 清空记录数据
         */
        redisTemplate.delete(InfoConstant.INFO_LIKED_USER_KEY);
        redisTemplate.delete(InfoConstant.TOTAL_LIKE_COUNT_KEY);
    }
    @Transactional
    @Override
    public void saveAndUpdateFile(WaiGuaInfoVO waiGuaInfoVO) {
        // 参数校验
        if(waiGuaInfoVO == null || StringUtils.isEmpty(waiGuaInfoVO.getWaiguaUsername())){
            throw new BizException("参数不能为空");
        }
        
        // 1. 保存或更新外挂账号信息
        UnmberEntity unmberEntity = unmberService.getByName(waiGuaInfoVO.getWaiguaUsername());
        if(unmberEntity != null){ 
            // 更新：将VO的数据复制到已存在的entity中
            BeanUtils.copyProperties(waiGuaInfoVO, unmberEntity);
            unmberService.updateById(unmberEntity);
        } else {
            // 新增
            unmberEntity = new UnmberEntity();
            BeanUtils.copyProperties(waiGuaInfoVO, unmberEntity);
            unmberService.save(unmberEntity);
        }
        
        // 2. 新增info信息
        InfoEntity infoEntity = buildInfoEntity(waiGuaInfoVO, unmberEntity.getId());
        this.baseMapper.saveInfoReturnId(infoEntity);
        /**
         * 关联视频文件表
         */
        FileInfoVO fileInfoVO = new FileInfoVO();
        fileInfoVO.setWaiguaInfoId(infoEntity.getId());
        fileInfoVO.setIdentifier(waiGuaInfoVO.getIdentifier());
        fileInfoVO.setCover(waiGuaInfoVO.getCover());
        R r = fileServer.updateFileInfoByWeb(fileInfoVO);
        if(r == null || r.getCode() != 0){
            String errorMsg = r != null ? (String) r.get("msg") : "远程服务返回null";
            log.error("调用远程服务失败：updateFileInfo, 错误信息: {}", errorMsg);
            throw new BizException("文件信息更新失败: " + errorMsg);
        }
        
        log.info("调用远程服务成功：updateFileInfo");
        //清除截屏图片文件
        try {
            GGFileUtil.deleteFilesByName("test2.jpg");
        } catch (Exception e) {
            log.warn("清除截屏图片文件失败: {}", e.getMessage());
        }

    }

    @Override
    public PageUtils findListByUser(Map<String, Object> params) {

        /**
         * 查询字段 拼接
         */
        QueryWrapper<InfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        InfoDTO infoDTO = new InfoDTO();
        String reportUserId = (String) params.get("reportuserId");
        infoDTO.setReportuserId(Long.parseLong(reportUserId));
        wrapper.eq("reportuser_id",Long.parseLong(reportUserId));
        if(!StringUtils.isEmpty(key)){
            infoDTO.setWaiguaUsername(key);
            wrapper.like("waigua_username",key);
        }
        String reviewStatus = (String) params.get("reviewStatus");
        if(Optional.ofNullable(reviewStatus).isPresent()&&!StringUtils.isEmpty(reviewStatus)){
            infoDTO.setReviewStatus(Integer.parseInt(reviewStatus));
            wrapper.eq("review_status",Integer.parseInt(reviewStatus));
        }

        IPage<InfoEntity> page =
                this.page(
                        new Query<InfoEntity>().getPage(params),
                        wrapper
                );
        PageUtils pageUtils = new PageUtils(page);
        /**
         * 设置分页
         */
        infoDTO.setPageSize(pageUtils.getPageSize());
        infoDTO.setPageIndex(pageUtils.getPageSize()*(pageUtils.getCurrPage()-1));
        List<InfoEntity> infoEntities =  this.baseMapper.findListByUser(infoDTO);
        pageUtils.setList(infoEntities);
        return pageUtils;
    }

    @Override
    public List<InfoEntity> findListByUserAll(Map<String, Object> params) {
        String key = (String) params.get("key");
        InfoDTO infoDTO = new InfoDTO();
        String reportUserId = "";
        if(!ObjectUtils.isEmpty(params.get("reportuserId"))){
            reportUserId = (String) params.get("reportuserId");
            infoDTO.setReportuserId(Long.parseLong(reportUserId));
        }
        if(!StringUtils.isEmpty(key)){
            infoDTO.setWaiguaUsername(key);
        }
        String reviewStatus = (String) params.get("reviewStatus");
        if(Optional.ofNullable(reviewStatus).isPresent()&&!StringUtils.isEmpty(reviewStatus)){
            infoDTO.setReviewStatus(Integer.parseInt(reviewStatus));
        }
        List<InfoEntity> infoEntities =  this.baseMapper.findListByUser(infoDTO);
        return infoEntities;
    }

    @Override
    public PageUtils findListAll(Map<String, Object> params) {

        /**
         * 查询字段 拼接
         */
        QueryWrapper<InfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        InfoDTO infoDTO = new InfoDTO();
        if(!StringUtils.isEmpty(key)){
            infoDTO.setWaiguaUsername(key);
            wrapper.like("waigua_username",key);
        }
        String reviewStatus = (String) params.get("reviewStatus");
        if(Optional.ofNullable(reviewStatus).isPresent()&&!StringUtils.isEmpty(reviewStatus)){
            infoDTO.setReviewStatus(Integer.parseInt(reviewStatus));
            wrapper.eq("review_status",Integer.parseInt(reviewStatus));
        }

        IPage<InfoEntity> page =
                this.page(
                        new Query<InfoEntity>().getPage(params),
                        wrapper
                );
        PageUtils pageUtils = new PageUtils(page);
        /**
         * 设置分页
         */
        infoDTO.setPageSize(pageUtils.getPageSize());
        infoDTO.setPageIndex(pageUtils.getPageSize()*(pageUtils.getCurrPage()-1));
        List<InfoEntity> infoEntities =  this.baseMapper.findListByUser(infoDTO);
        pageUtils.setList(infoEntities);
        return pageUtils;
    }

    /**
     *  描述: 统计评论的总点赞数
     * @param relationId:
     * @return Long
     * @author txy
     * @description
     * @date 2021/11/9 16:02
     */
    public synchronized Long countRelationLike(Long relationId,Integer type) {
        validateParam(relationId);
        String relationLikedResult = (String) redisTemplate.opsForHash().get(InfoConstant.TOTAL_LIKE_COUNT_KEY, relationId+"::"+type);
        Long likeCount = 0L;
        if(!org.apache.commons.lang.StringUtils.isEmpty(relationLikedResult)){
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
                .get(InfoConstant.INFO_LIKED_USER_KEY, relationId + "::" + likedUserId+"::"+type);
        if(!org.apache.commons.lang.StringUtils.isEmpty(value)){
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
                .get(InfoConstant.INFO_LIKED_USER_KEY, relationId + "::" + likedUserId+"::"+type);
        if(count>0){//库里有
            if(!org.apache.commons.lang.StringUtils.isEmpty(value)){
                if(value.equals("1")){//点过了
                    log.error("数据库有但是缓存取消了又点过攒了，relationId:{}，likedUserId:{}", relationId, likedUserId);
                    throw new BizException("该评论已被当前用户点赞，重复点赞!");
                }
            }else{
                log.error("数据库有，relationId:{}，likedUserId:{}", relationId, likedUserId);
                throw new BizException("该评论已被当前用户点赞，重复点赞!");
            }
        }else{
            /**
             * 查缓存
             */
            if(!org.apache.commons.lang.StringUtils.isEmpty(value)){
                if(value.equals("1")){// 点过赞了
                    log.error("缓存有并点过了，relationId:{}，likedUserId:{}", relationId, likedUserId);
                    throw new BizException("该评论已被当前用户点赞，重复点赞!");
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
                .get(InfoConstant.INFO_LIKED_USER_KEY, relationId + "::" + likedUserId+"::"+type);
        if(count == 0 ){
            log.error("数据库没有，relationId:{}，likedUserId:{}", relationId, likedUserId);
            /**
             * 查缓存
             */
            boolean b = redisTemplate.opsForHash()
                    .hasKey(InfoConstant.INFO_LIKED_USER_KEY, relationId + "::" + likedUserId+"::"+type);
            if(!b){
                log.error("缓存没有，commentId:{}，likedUserId:{}", relationId, likedUserId);
                throw new BizException("该评论未被当前用户点赞，不可以进行取消点赞操作!");
            }else {
                if(value.equals("0")){
                    log.error("缓存有但是取消了已经，commentId:{}，likedUserId:{}", relationId, likedUserId);
                    throw new BizException("该评论未被当前用户点赞，不可以进行取消点赞操作!");
                }
            }
        }else{//库里有
            if(!org.apache.commons.lang.StringUtils.isBlank(value)){
                if(value.equals("0")){//但是缓存已经取消过了
                    throw new BizException("该评论已经取消点赞，不可以进行取消点赞操作!");
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
                throw new BizException("参数不能为null!");
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
            List<InfoEntity> infoEntities = this.baseMapper.findListAll(null,"2",currentUser,Constant.LIKETYPE_INFO);
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
                 Long countRelationLike = countRelationLike(infoEntity.getId(),Constant.LIKETYPE_INFO);
                 Long countRelationLikeDb = 0L;
                 if(infoEntity.getThumbUpNumber()!=null){
                     countRelationLikeDb =  Long.valueOf(infoEntity.getThumbUpNumber());
                 }
                 Integer coutLike = Math.toIntExact(countRelationLike + countRelationLikeDb);
                 waiGuaInfoVO.setThumbUpNumber(coutLike);
                 /**
                  * 缓存是否点过赞了
                  */
                 Integer isSupport = whetherThumbUp(infoEntity.getId(), currentUser, Constant.LIKETYPE_INFO);
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
                    log.error("远程服务调用失败--- fileServer.videoInfo"+r.getMsg());
                    throw new BizException(r.getMsg());
                }
                return waiGuaInfoVO;
            }).collect(Collectors.toList());
             //加入缓存
             String s = JSON.toJSONString(collect);
             redisTemplate.opsForValue().set("allWaiGuaData",s,1,TimeUnit.MINUTES);
            logger.error("新建缓存key-allWaiGuaData的生成时间:{}",new Date());
        }finally {
            lock.unlock();
        }
        return collect;
    }

    /**
     * 构建InfoEntity对象
     * @param waiGuaInfoVO VO对象
     * @param waiguaId 外挂账号ID
     * @return InfoEntity
     */
    private InfoEntity buildInfoEntity(WaiGuaInfoVO waiGuaInfoVO, Long waiguaId) {
        InfoEntity infoEntity = new InfoEntity();
        BeanUtils.copyProperties(waiGuaInfoVO, infoEntity);
        
        Date now = new Date();
        infoEntity.setWaiguaId(waiguaId);
        infoEntity.setWaiguaType(StringUtils.arrayToDelimitedString(waiGuaInfoVO.getWaiguaType(), ","));
        infoEntity.setStatus(Constant.STATUS_0);
        infoEntity.setThumbUpNumber(0);
        infoEntity.setReadNumber(0);
        infoEntity.setCreateTime(now);
        infoEntity.setUpdataTime(now);
        infoEntity.setReviewStatus(Constant.REVIEWSTATUS_0);
        
        // 设置上传用户ID（如果VO中没有提供则使用默认值）
        if(waiGuaInfoVO.getReportuserId() == null) {
            infoEntity.setReportuserId(4L);
        }
        
        return infoEntity;
    }
}