package com.sunyard.ribbonconsumer.hystrixByCode;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

//熔断器 实现多个请求
public class MyHystrixObservableCommand extends HystrixObservableCommand<String> {

    private RestTemplate restTemplate;

    public MyHystrixObservableCommand(RestTemplate restTemplate)
    {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.restTemplate=restTemplate;
    }

    @Override
    protected Observable<String> construct() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    if(!subscriber.isUnsubscribed()) {
                        //调用多个请求的时候封装参数 的
                        subscriber.onNext("dw");
                        String str= restTemplate.getForEntity("http://eureka-client/hello",String.class).getBody();
                        subscriber.onNext(String.valueOf(str));
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 服务降级
     */
    @Override
    protected Observable<String> resumeWithFallback() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext("失败了！");
                        subscriber.onNext("找大神来排查一下吧！");
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

}
