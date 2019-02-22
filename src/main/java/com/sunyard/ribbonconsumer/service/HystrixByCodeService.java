package com.sunyard.ribbonconsumer.service;


import com.sunyard.ribbonconsumer.hystrixByCode.MyHystrixCommand;
import com.sunyard.ribbonconsumer.hystrixByCode.MyHystrixObservableCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.util.Iterator;
import java.util.concurrent.Future;


@Service
public class HystrixByCodeService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 编程式通过  EXECUTE方法执行同步调用请求操作
     * @return
     */
    public String codeHystrix(){
        MyHystrixCommand myHystrixCommand =new MyHystrixCommand(restTemplate);
        return myHystrixCommand.execute();
    }

    /**
     * 编程式通过  EXECUTE方法执行同步调用请求操作
     * @return
     */
    public Future<String> codeHystrix2(){
        MyHystrixCommand myHystrixCommand =new MyHystrixCommand(restTemplate);
        return myHystrixCommand.queue();
    }

    /**
     * 编程式通过 HystrixObservableCommand调用请求操作
     * @return
     */
    public String codeHystrix3(){
        MyHystrixObservableCommand myHystrixObservableCommand =new MyHystrixObservableCommand(restTemplate);
        String str=null;
        //hot observe
        Observable<String> observable= myHystrixObservableCommand.observe();
        //cold observe
        //Observable<String> observable2= myHystrixObservableCommand.toObservable();
        Iterator<String> iterator=observable.toBlocking().getIterator();
        while (iterator.hasNext()){
            str+=iterator.next();
        }
        return  str;
    }

}
