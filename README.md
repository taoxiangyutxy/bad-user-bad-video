# 外挂信息收集服务

#### 介绍
该项目首先是个练手项目。后台管理直接使用了人人开源。
主要流程：用户新增举报信息（账号，等级等）；上传悲惨视频待审核；管理人员进行视频及外挂信息审核；审核通过，大厅展示；
 **唯一特点** ：上传视频实现分片上传、秒传以及断点续传的功能.

#### 软件架构

- one-common         公共的依赖，bean，工具类等
- one-fileServer     文件上传下载服务(分片上传、秒传及断点续传功能)
- one-gateway        网关服务
- one-search         搜索服务（Elasticsearch全文检索）
- one-third-party    第三方服务(短信接口)
- one-waiguagg       外挂信息服务(业务模块)
- renren-fast        人人开源后台管理服务
- renren-fast-vue    人人开源前台页面(视频上传等页面)



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

#### 使用说明


**第1.2版前端上传测试** 
![输入图片说明](images/%E5%BD%95%E5%83%8F12_jq_hc_%E8%BD%AC.gif)


 **门户网站** 
![输入图片说明](https://images.gitee.com/uploads/images/2021/0923/180541_35d49640_2217964.gif "录像6_jq_转.gif")


 **门户网站详情页-可点赞评论回复** 
![输入图片说明](%E5%BD%95%E5%83%8F11_%E8%BD%AC.gif)


 **搜索服务** 
![输入图片说明](https://images.gitee.com/uploads/images/2021/1006/131409_50bc523d_2217964.png "111.PNG")



 **外挂举报列表** 

![外挂举报列表](https://images.gitee.com/uploads/images/2021/0912/184423_0a60dd84_2217964.png "project2.PNG")

 **举报表单** 

![举报表单](https://images.gitee.com/uploads/images/2021/0912/184149_f3bdcc5b_2217964.png "project-ttt1.PNG")

 **上传视频** 

![上传视频](https://images.gitee.com/uploads/images/2021/0912/213327_02271871_2217964.gif "1.gif")

 **断点续传** 

![断点续传](https://images.gitee.com/uploads/images/2021/0912/213603_f8942566_2217964.gif "2.gif")

 **视频预览** 

![视频预览](https://images.gitee.com/uploads/images/2021/0912/215415_8e363a18_2217964.gif "7.gif")

