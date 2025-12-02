package com.ttt.one.waiguagg.controller;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;

import com.ttt.one.common.exception.BizException;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import com.ttt.one.waiguagg.entity.UnmberEntity;
import com.ttt.one.waiguagg.service.UnmberService;

import javax.validation.Valid;

/**
 * 外挂账号
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-09 10:17:14
 */
@Slf4j
@RestController
@RefreshScope
@RequestMapping("waiguagg/unmber")
public class UnmberController {

 public static    ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    private UnmberService unmberService;

    @Value("${spring.ttt.user.name}")
    private String name;
    @Value("${spring.ttt.user.age}")
    private Integer age;

    @GetMapping("/test")
    public R test(@Valid @RequestBody UnmberEntity unmber){
        log.info("unmber="+unmber);
       /* String name = null;
        try {
            if(name.equals("1")){

            }else {
                throw  new BizException("用户不存在", 200);
            }
        } catch (Exception e) {
            throw  new BizException("eeee", 300);

        }*/
        return R.ok().put("name",name).put("age",age);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = unmberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		UnmberEntity unmber = unmberService.getById(id);

        return R.ok().put("unmber", unmber);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody UnmberEntity unmber){
		unmberService.saveAndInfoAndVideo(unmber);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UnmberEntity unmber){
		unmberService.updateById(unmber);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		unmberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
    public void test(String[] arg) {
        for (String string : arg) {
            System.out.println("zp is " + string);
        }
    }

    public static void invokeDemo() throws Exception {
        //获取字节码对象,这里要填好你对应对象的包的路径
        Class<UnmberController> clazz = (Class<UnmberController>) Class.forName("com.ttt.one.waiguagg.controller.UnmberController");
        //形式一：获取一个对象
//        Constructor con =  clazz.getConstructor();
//        InvokeTest m = (InvokeTest) con.newInstance();
        //形式二：直接new对象，实际上不是框架的话，自己写代码直接指定某个对象创建并调用也可以
        UnmberController m = new UnmberController();
        String[] s = new String[]{"handsome", "smart"};
        //获取Method对象
        Method method = clazz.getMethod("test", String[].class);
        //调用invoke方法来调用
        method.invoke(m, (Object) s);
    }

    public static void main(String[] args) throws Exception {
        invokeDemo();
      /*  System.out.println("111111");
        Future<Integer> submit = executorService.submit(new Callable01());
        Integer integer = submit.get();
        System.out.println("jieguo="+integer);
        System.out.println("222222");*/

    }
    public static class Callable01 implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程:"+Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果:"+i);
            return i;
        }
    }
    public static class Runnable01 implements Runnable{
        @Override
        public void run() {
            System.out.println("当前线程:"+Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果:"+i);
        }
    }

    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程:"+Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果:"+i);
        }
    }
}
