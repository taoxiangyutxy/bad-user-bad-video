

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
