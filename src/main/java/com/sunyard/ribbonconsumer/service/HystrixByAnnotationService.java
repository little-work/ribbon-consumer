package com.sunyard.ribbonconsumer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.Future;

@Service
public class HystrixByAnnotationService {

    @Autowired
    RestTemplate restTemplate;

    /**
     * 同步返回结果  使用HystrixCommand的方式  配置了什么样的异常不会降级
     *   新增了  命令名称、分组和线程池命名注解属性配置
     * @return
     */
    @HystrixCommand(fallbackMethod = "helloFallBack",
            ignoreExceptions = {HystrixBadRequestException.class},
            commandKey = "demo",
            groupKey = "demoGroup",
            threadPoolKey ="demoThreadPoolkey")
    /**
     * 缓存的清理 必须指定commandKey  通过他来找到正确的缓存位置  就是我们指定的命令名称
     */
    @CacheRemove(commandKey = "asca")
    public String helloService(){
        return restTemplate.getForEntity("http://eureka-client/hello",String.class).getBody();
    }

    /**
     * 异步返回结果
     *
     *@CacheResult加入缓存  必须和@HystrixCommand一起使用  里面有一个cachekey的生成规则函数属性配置 cacheKeyMethod
     *  可以指定函数生成的规则函数  但是@CacheKey可以指定key的  优先级低于cacheKeyMethod 如果指定了函数
     *  那么@CacheKey不会生效的
     *
     */
    @CacheResult(cacheKeyMethod = "getCacheKey")
    @HystrixCommand(fallbackMethod = "helloFallBack")
    public Future<String> helloService3(@CacheKey("str") String str){
        return new AsyncResult<String>(){
            @Override
            public  String invoke(){
                return restTemplate.getForEntity("http://eureka-client/hello",String.class).getBody();
            }
        };
    }


    /**
     * 合并请求  batchMethod定义了批量请求的方法  @HystrixProperty定义了多少时间内的请求  合并发出
     */
    @HystrixCollapser(batchMethod = "batchMethod",collapserProperties = {
            @HystrixProperty(name="timerDelayInMilliseconds",value="100")
    })
    public String helloService4(){
        return null;
    }

    //使用HystrixObservableCommand的方式

    /**
     * LAZY参数表示使用toObservable()方式执行
     * EAGER参数表示通过observe()方式执行
     * @return
     */
    @HystrixCommand(observableExecutionMode = ObservableExecutionMode.EAGER,
            fallbackMethod = "helloFallBack")
    public Observable<String> helloService2(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    if(!subscriber.isUnsubscribed()) {
                        //调用多个请求的时候封装参数 的
                        subscriber.onNext("模拟多个请求返回结果");
                        String str= restTemplate.getForEntity("http://eureka-client/hello",String.class).getBody();
                        subscriber.onNext(String.valueOf(str));
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }



    //服务降级   可以根据异常的类型
    public String helloFallBack(Throwable e){

        return "看到这个信息的时候说明你要访问的额服务挂了Hystrix起作用了，帮你转发了";
    }

    /**
     * 自定义Key的生成规则
     * @return
     */
    public String getCacheKey(){
        return "dadas";
    }

    /**
     * 自定义批量请求的方法  对应
     * @return
     */
    public String batchMethod(){
        return restTemplate.getForEntity("http://eureka-client/hello",String.class).getBody();
    }
}
