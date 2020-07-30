package com.test.api.consumer.deliver.context;

import com.fasterxml.jackson.databind.JavaType;

import java.lang.reflect.Method;

/**
 * @author 费世程
 * @date 2020/7/24 17:43
 */
public class EventListenerContext {

  private final Object instance;
  private final Method method;
  private final JavaType javaType;

  public EventListenerContext(Object instance, Method method, JavaType javaType) {
    this.instance = instance;
    this.method = method;
    this.javaType = javaType;
  }

  public Object process(Object message) throws Exception {
    return this.method.invoke(instance, message);
  }

  public Object getInstance() {
    return instance;
  }

  public Method getMethod() {
    return method;
  }

  public JavaType getJavaType() {
    return javaType;
  }
}
