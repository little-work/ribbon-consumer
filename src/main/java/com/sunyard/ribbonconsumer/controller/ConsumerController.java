package com.sunyard.ribbonconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {


    @Autowired
    RestTemplate restTemplate;

    //我们自定义负载均衡策略
    @Autowired
    private LoadBalancerClient loadBalancerClient;


    //默认负载均衡
    @RequestMapping(value = "ribbon-consumer",method = RequestMethod.GET)
    public String findServerDemo(){
        System.out.println("负载均衡转发");
        return  restTemplate.getForEntity("http://eureka-client/hello",String.class).getBody();
    }

    //这个是我们验证使用我们自定义的负载均衡策略
    @RequestMapping(value = "custom-ribbon-consumer",method = RequestMethod.GET)
    public String customRibbon(){
        ServiceInstance serviceInstance = this.loadBalancerClient.choose("eureka-client");
        String str="访问服务器信息001" + ":" + serviceInstance.getServiceId() + ":" +
                serviceInstance.getHost() + ":" + serviceInstance.getPort();
        System.out.println(str);
        return str;
    }


}
