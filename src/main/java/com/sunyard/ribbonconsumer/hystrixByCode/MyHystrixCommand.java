package com.sunyard.ribbonconsumer.hystrixByCode;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
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
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
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
}
