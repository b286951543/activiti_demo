package com.example.springboot_activiti_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// 由于Activiti 启动器提供了一个 Spring Boot 自动配置类，它使用HTTP basic身份验证保护所有 REST 端点，所以在发送请求时需要设置相关的身份信息，否则会造成401错误。提示如下：
// Using generated security password: 0a7a2af2-8309-484c-8599-33ea62ddb0b9
// This generated password is for development use only. Your security configuration must be updated before running your application in production.
// 如果我们想使用不同的安全配置而不是 HTTP Basic 身份验证，可以关闭禁用spring security，之后就可以不带验证的访问服务了。方式如下
// 该配置没效果
//@SpringBootApplication(exclude = {
//		SecurityAutoConfiguration.class,
//		ManagementWebSecurityAutoConfiguration.class
//})
@SpringBootApplication
public class SpringbootActivitiDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootActivitiDemoApplication.class, args);
	}

}
