

# 外挂信息收集服务

#### 介绍
该项目首先是个练手项目。
主要流程：用户新增举报信息（账号，等级等）；上传视频待审核；管理人员进行视频及外挂信息审核；审核通过，大厅展示；
 **特点** ：上传视频实现分片上传、秒传以及断点续传的功能.


#### 软件架构

- one-common         公共的依赖，bean，工具类等
- one-fileServer     文件上传下载服务(分片上传、秒传及断点续传功能)
- one-gateway        网关服务
- one-search         搜索服务（Elasticsearch全文检索）
- one-third-party    第三方服务(短信接口)
- one-waiguagg       外挂信息服务(业务模块)
- one-admin          后台管理服务



技术选型：

- 核心框架：Spring Boot
- 注册中心：alibaba-nacos-discovery
- 配置中心：alibaba-nacos-config
- 视图框架：Spring MVC
- 持久层框架：MyBatis
- 文件服务器：Minio
- 定时任务：ShedLock
- 页面交互：Vue
- 全文检索：Elasticsearch


#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx


**新版后台管理平台**

![输入图片说明](https://gitee.com/yyyyxt/typoraImages/raw/master/images/20260109151522569.png)

**vue3.0版本上传图**

![输入图片说明](https://gitee.com/yyyyxt/typoraImages/raw/master/images/20260109152112997.png)

![输入图片说明](https://gitee.com/yyyyxt/typoraImages/raw/master/images/20260109152516014.png)

![输入图片说明](https://gitee.com/yyyyxt/typoraImages/raw/master/images/20260109152650860.png)


**AOP切面配合ES记录日志**
![输入图片说明](https://gitee.com/yyyyxt/typoraImages/raw/master/images/20260109153223758.png)

![输入图片说明](https://gitee.com/yyyyxt/typoraImages/raw/master/images/20260109153301993.png)

#### 使用说明

**第1.2版前端上传测试** 
![输入图片说明](images/%E5%BD%95%E5%83%8F12_jq_hc_%E8%BD%AC.gif)


 **门户网站** 
![输入图片说明](https://images.gitee.com/uploads/images/2021/0923/180541_35d49640_2217964.gif "录像6_jq_转.gif")


 **门户网站详情页-可点赞评论回复** 
![输入图片说明](%E5%BD%95%E5%83%8F11_%E8%BD%AC.gif)


 **搜索服务** 
![输入图片说明](https://images.gitee.com/uploads/images/2021/1006/131409_50bc523d_2217964.png "111.PNG")


 **上传视频** 

![上传视频](https://images.gitee.com/uploads/images/2021/0912/213327_02271871_2217964.gif "1.gif")



 **断点续传** 

![断点续传](https://images.gitee.com/uploads/images/2021/0912/213603_f8942566_2217964.gif "2.gif")

