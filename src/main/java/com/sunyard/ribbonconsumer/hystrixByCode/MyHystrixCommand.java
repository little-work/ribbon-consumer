package com.sunyard.ribbonconsumer.hystrixByCode;


import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import org.springframework.web.client.RestTemplate;

/**
 * 需要继承HystrixCommand 方法
 */
public class MyHystrixCommand extends HystrixCommand<String> {

    //通过构造函数实例化这个对账
    private RestTemplate restTemplate;

    /**
     * 构造函数  创建这个编程式执行
     * @param restTemplate
     */
    public MyHystrixCommand(RestTemplate restTemplate) {
        //设置命令名称、分组以及线程池划分   如果没有配置线程池默认的线程划分根据分组划分
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CommandGroupKey"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("CommandKey"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ThreadPoolKey")));
        this.restTemplate=restTemplate;
    }

    @Override
    protected String run() {
        return restTemplate.getForEntity("http://eureka-client/hello",String.class).getBody();
    }

    /**
     * 降级服务 回调函数
     * @return
     */
    @Override
    protected String getFallback(){
        return "代码熔断器错误";
    }


    /**
     * 开启缓存功能 根据getCacheKey返回值来区分是否重复请求  如果是的话  那么第一次是真实的调用关系  之后都是在缓存中取得值
     * @return
     */
    @Override
    protected String getCacheKey(){
        //根据返回值作为键置入缓存中
        return "dqwddwq";
    }

    /**
     * 由于读操作是不需要考虑缓存内容是都正确  但是在更新操作的时候  就需要我们在更新之前 清理缓存 这样能保证下一次读的
     * 是更新之后 的数据
     *
     * 对缓存的清理  可以在对服务调用的RUN方法之后  使用这个方法让之前的缓存失效
     */
    public static void flushCache(){
        //刷新缓存 根据id清理
        HystrixRequestCache.getInstance(HystrixCommandKey.Factory.asKey("CommandKey"),
                HystrixConcurrencyStrategyDefault.getInstance()).clear("dqwddwq");
    }
}
