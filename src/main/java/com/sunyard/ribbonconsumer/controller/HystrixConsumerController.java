package com.sunyard.ribbonconsumer.controller;

import com.sunyard.ribbonconsumer.service.HystrixByAnnotationService;
import com.sunyard.ribbonconsumer.service.HystrixByCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class HystrixConsumerController {
    //注解实现
    @Autowired
    HystrixByAnnotationService hystrixService;

    //编程式实现
    @Autowired
    HystrixByCodeService hystrixByCodeService;

    /**
     * 通过注解HystrixCommand 的同步执行调用
     * @return
     */
    @RequestMapping(value = "ribbon-consumer-annot1",method = RequestMethod.GET)
    public String findServerDemo1(){
       return hystrixService.helloService();
    }
    /**
     * 通过注解HystrixCommand 的异步执行调用
     * @return
     */
    @RequestMapping(value = "ribbon-consumer-annot2",method = RequestMethod.GET)
    public String findServerDemo2() throws ExecutionException, InterruptedException {
        Future<String> future=hystrixService.helloService3();
        return  future.get();
    }

    /**
     * 通过注解HystrixCommand实现HystrixObservableCommand 执行调用
     * @return
     */
    @RequestMapping(value = "ribbon-consumer-annot3",method = RequestMethod.GET)
    public String findServerDemo3(){
        String str=null;
        Observable<String> observable= hystrixService.helloService2();
        Iterator<String> iterator=observable.toBlocking().getIterator();
        while (iterator.hasNext()){
            str=iterator.next();
        }
        return  str;
    }

    /**
     * 编程式 HystrixCommand同步调用方法
     * @return
     */
    @RequestMapping(value = "ribbon-consumer-code",method = RequestMethod.GET)
    public String findServerDemo4(){
        return hystrixByCodeService.codeHystrix();
    }

    /**
     * 编程式 HystrixCommand同步调用方法
     * @return
     */
    @RequestMapping(value = "ribbon-consumer-code2",method = RequestMethod.GET)
    public String findServerDemo5() throws ExecutionException, InterruptedException {
        Future<String> future=hystrixByCodeService.codeHystrix2();
        return future.get();
    }

    /**
     * 编程式  HystrixObservableCommand方法调用
     * @return
     */
    @RequestMapping(value = "ribbon-consumer-code3",method = RequestMethod.GET)
    public String findServerDemo6(){
        return hystrixByCodeService.codeHystrix3();

    }


}
