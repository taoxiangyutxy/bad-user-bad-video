package com.ttt.one.waiguagg.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;
import com.ttt.one.common.utils.R;
import com.ttt.one.waiguagg.entity.UnmberEntity;
import com.ttt.one.waiguagg.fegin.FileServer;
import com.ttt.one.waiguagg.fegin.ThirdPartyFeginServer;
import com.ttt.one.waiguagg.fegin.UserFeginServer;
import com.ttt.one.waiguagg.service.UnmberService;
import com.ttt.one.waiguagg.vo.FileInfoVO;
import com.ttt.one.waiguagg.vo.SysUserVO;
import com.ttt.one.waiguagg.vo.VideoPreviewVO;
import com.ttt.one.waiguagg.vo.WaiGuaInfoVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdataTime(infoEntity.getCreateTime());
        infoEntity.setReviewStatus(Constant.REVIEWSTATUS_0);
        this.save(infoEntity);
    }

    @Override
    public WaiGuaInfoVO getByIdAndUnmber(Long id) {
        WaiGuaInfoVO waiGuaInfoVO = new WaiGuaInfoVO();
        //1 根据id查询info信息
        InfoEntity infoEntity = this.getById(id);
        BeanUtils.copyProperties(infoEntity,waiGuaInfoVO);
        if(null!=infoEntity.getWaiguaType()){
            waiGuaInfoVO.setWaiguaType(infoEntity.getWaiguaType().split(","));
        }
        waiGuaInfoVO.setWaiguaInfoId(id);
        //2 根据id查出外挂账号
        UnmberEntity unmberEntity = unmberService.getById(infoEntity.getWaiguaId());
        BeanUtils.copyProperties(unmberEntity,waiGuaInfoVO);
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
                    log.error("调用文件上传远程服务报错:{}",e);
                }
                //4 根据info id删info信息
                this.removeById(infoEntity.getId());

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
    public void updateByIdAndReview(WaiGuaInfoVO waiGuaInfoVO) {
        InfoEntity infoEntity = new InfoEntity();
        infoEntity.setId(waiGuaInfoVO.getWaiguaInfoId());
        infoEntity.setReviewStatus(waiGuaInfoVO.getReviewStatus());
        //审核通过 调用第三方服务 发送短信通知
        if(waiGuaInfoVO.getReviewStatus()!= 0 && waiGuaInfoVO.getReviewStatus()!=1){
            String code = "";
            if(waiGuaInfoVO.getReviewStatus()==2){
                code="666";
            }else if(waiGuaInfoVO.getReviewStatus()==3){
                code="44";
            }
            //todo 手机号关联用户id 查   没钱发短信啦
          //  R r = thirdPartyFeginServer.sendCode("18753571460", code);
            //审核通过  门户网站数据多一条  将缓存清空
            redisTemplate.delete("allWaiGuaData");
        }

        this.updateById(infoEntity);
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

    private List<WaiGuaInfoVO> getAllWaiGuaData(){
        //获取锁,10秒后自动解锁
        RLock lock = redisson.getLock("waiguaData-lock");
        lock.lock(10, TimeUnit.SECONDS);
        System.out.println("获取分布式锁成功..");
        List<WaiGuaInfoVO> collect = new ArrayList<>();
        try {
            QueryWrapper<InfoEntity> wrapper = new QueryWrapper<InfoEntity>().eq("status", Constant.STATUS_0);
            wrapper.eq("review_status",Constant.REVIEWSTATUS_2);
            List<InfoEntity> infoEntities = this.list(wrapper);
             collect = infoEntities.stream().map(infoEntity -> {
                WaiGuaInfoVO waiGuaInfoVO = new WaiGuaInfoVO();
                BeanUtils.copyProperties(infoEntity, waiGuaInfoVO);
                waiGuaInfoVO.setWaiguaType(infoEntity.getWaiguaType().split(","));
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