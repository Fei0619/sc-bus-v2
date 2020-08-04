package com.test.instance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author 费世程
 * @date 2020/7/29 19:47
 */
@EnableDiscoveryClient
@SpringBootApplication
public class InstanceApplication {

  public static void main(String[] args) {
    SpringApplication.run(InstanceApplication.class, args);
  }

}
