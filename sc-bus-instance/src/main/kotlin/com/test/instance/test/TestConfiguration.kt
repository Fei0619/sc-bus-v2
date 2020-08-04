package com.test.instance.test

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

/**
 * @Configuration 注解表明这个类是一个配置类。可以启动组件扫描，
 * 用来将带有@Bean的实体进行实例化bean等.把普通pojo实例化到spring容器中，
 * 相当于配置文件中的 <bean id="" class=""/>
 *
 * @author 费世程
 * @date 2020/7/29 20:21
 */
@Configuration
//@Component
open class TestConfiguration {

  @Bean
  open fun getCar(): Car {
    return Car()
  }

  @Bean
  open fun getDriver(): Driver {
    val driver = Driver()
    driver.car = getCar()
    return driver
  }

}