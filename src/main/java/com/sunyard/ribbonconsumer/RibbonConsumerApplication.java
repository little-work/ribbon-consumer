package com.sunyard.ribbonconsumer;

import com.sunyard.ribbonconsumer.customRibbonStrategy.ExcludeFromComponentScan;
import com.sunyard.ribbonconsumer.customRibbonStrategy.MyConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
//熔断器
@EnableCircuitBreaker
//服务的发现
@EnableDiscoveryClient
//实现自定义负载均衡的时候添加的通过这个注解可以为服务的负载均衡进行细粒度的控制，上面的意思是说要为eureka-client,
//使用我们自定义的那个配置,而那个自定义的配置，定义了负载均衡是随机的，所以这个服务也就使用了随机负载均衡
@RibbonClient(name = "eureka-client",configuration = MyConfiguration.class)
//实现自定义负载均衡的时候添加的这个注解的作用就是排除自定义的配置是不包含在springboot的包扫描范围之内，
// 否则自定义配置会完全失效的.
@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,
		value = ExcludeFromComponentScan.class)})
public class RibbonConsumerApplication {

	//开启服务端负载均衡能力  因为springbootapplication注解中有Configuration注解  所以  这个可以作为配置类加在进
	//spring容器中
	@Bean
	@LoadBalanced
	RestTemplate  restTemplate(){
		return new RestTemplate();
	}

	public static void main(String[] args) {

	    SpringApplication.run(RibbonConsumerApplication.class, args);
	}
}

