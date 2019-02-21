package com.sunyard.ribbonconsumer.customRibbonStrategy;


import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ExcludeFromComponentScan
public class MyConfiguration {

    @Bean
    public IRule ribbonRule(){
        return new RandomRule(); //这里使用随机
    }
}
