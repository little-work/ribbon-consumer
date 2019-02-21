package com.sunyard.ribbonconsumer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
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

    //同步返回结果  使用HystrixCommand的方式
    @HystrixCommand(fallbackMethod = "helloFallBack")
    public String helloService(){
        return restTemplate.getForEntity("http://eureka-client/hello",String.class).getBody();
    }

    //异步返回结果
    @HystrixCommand(fallbackMethod = "helloFallBack")
    public Future<String> helloService3(){
        return new AsyncResult<String>(){
            @Override
            public  String invoke(){
                return restTemplate.getForEntity("http://eureka-client/hello",String.class).getBody();
            }
        };
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



    //服务降级
    public String helloFallBack(){
        return "看到这个信息的时候说明你要访问的额服务挂了Hystrix起作用了，帮你转发了";
    }
}
