package com.test.api.consumer.deliver.impl

import com.test.common.json.JsonUtils
import com.test.api.consumer.annotation.BusCallbackEventListener
import com.test.api.consumer.annotation.BusEventListener
import com.test.api.consumer.annotation.BusListenerBean
import com.test.api.consumer.deliver.CallbackDeliver
import com.test.api.consumer.deliver.EventDeliver
import com.test.api.consumer.deliver.context.CallbackEventContext
import com.test.api.consumer.deliver.context.EventListenerContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * @author 费世程
 * @date 2020/7/27 16:55
 */
@Component
class EventDeliverImpl(private val applicationContext: AbstractApplicationContext)
  : EventDeliver, CallbackDeliver, InitializingBean {

  private val logger = LoggerFactory.getLogger(EventDeliverImpl::class.java)

  companion object {
    private val eventHandlerMapping = HashMap<String, EventListenerContext>()
    private val callbackHandlerMapping = HashMap<String, CallbackEventContext>()
  }

  override fun afterPropertiesSet() {
    init()
  }

  /**
   * 初始化：
   * 读取项目中被【BusEventListener】、【BusCallbackEventListener】注解注释的方法信息
   */
  private fun init() {
    val beanMappings = applicationContext.getBeansWithAnnotation(BusListenerBean::class.java)
    beanMappings.values.stream().map { bean ->
      val clazz = bean.javaClass
      val methods = clazz.methods
      for (method in methods) {
        val eventListener = method.getAnnotation(BusEventListener::class.java)
        val callbackListener = method.getAnnotation(BusCallbackEventListener::class.java)
        val className = clazz.name
        val methodName = method.name

        if (eventListener != null && callbackListener != null) {
          logger.debug("${className}#${methodName} -> BusEventListener、CallbackEventListener冲突")
          continue
        }

        if (eventListener != null) {
          val topic = eventListener.topic
          if (StringUtils.isEmpty(topic)) {
            logger.debug("${className}#${methodName} -> BusEventListener的topic未指定")
            continue
          }
          val parameters = method.parameters
          if (parameters.size == 1) {
            val parameter = parameters[0]
            if (eventHandlerMapping[topic] != null) {
              logger.debug("重复的EventListener,topic= $topic ")
              continue
            } else {
              val parameterType = parameter.parameterizedType
              eventHandlerMapping[topic] = EventListenerContext(bean, method, JsonUtils.getJavaType(parameterType))
            }
          } else {
            logger.debug("${className}#${methodName} -> BusEventListener的参数过长")
            continue
          }
        }

        if (callbackListener != null) {
          val topic = callbackListener.topic
          if (StringUtils.isEmpty(topic)) {
            logger.debug("${className}#${methodName} -> CallbackEventListener的topic未指定")
            continue
          }
          val parameters = method.parameters
          if (parameters.size == 1) {
            val parameter = parameters[0]
            if (callbackHandlerMapping[topic] != null) {
              logger.debug("重复的CallbackEventListener,topic= $topic ")
              continue
            } else {
              val parameterType = parameter.parameterizedType
              callbackHandlerMapping[topic] = CallbackEventContext(bean, method, JsonUtils.getJavaType(parameterType))
            }
          } else {
            logger.debug("${className}#${methodName} -> CallbackEventListener的参数过长")
            continue
          }
        }
      }

    }
  }

  override fun deliver(topic: String, message: Any): Any {

    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}