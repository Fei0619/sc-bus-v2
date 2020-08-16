package com.test.server.core.processor.publisher

import com.test.common.init.Destroyable
import com.test.common.json.JsonUtils
import com.test.common.json.toJsonString
import com.test.server.core.pojo.CallbackPushInfo
import com.test.server.core.pojo.EventPushInfo
import com.test.server.core.pojo.PublishRecord
import com.test.server.core.processor.pusher.MessagePusher
import com.test.server.service.BusCache
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.*
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

/**
 * @author 费世程
 * @date 2020/8/14 13:56
 */
class AsyncPublisher(private val busCache: BusCache,
                     private val reactiveExecutor: ExecutorService) : Publisher, ApplicationRunner, Destroyable {
  private val log = LoggerFactory.getLogger(AsyncPublisher::class.java)

  private val queueSize = 1.shl(14)
  private val threads = ArrayList<Thread>()
  private val serverLimits = ConcurrentHashMap<String, AtomicInteger>()

  //持久化文件名
  private val eventQueueFileName = "bus_event_message"
  private val eventDelayQueueFileName = "bus_delay_event_message"
  private val callbackQueueFileName = "bus_callback_message"
  private val callbackDelayQueueFileName = "bus_delay_callback_message"

  //为每个订阅方创建一个队列：serviceCode -> queue
  //事件队列
  private val eventQueueMapping = ConcurrentHashMap<String, ArrayBlockingQueue<EventPushInfo>>()
  private val eventDelayQueueMapping = ConcurrentHashMap<String, DelayQueue<DelayEventMessage>>()

  //回调队列
  private val callbackQueueMapping = ConcurrentHashMap<String, ArrayBlockingQueue<CallbackPushInfo>>()
  private val callbackDelayQueueMapping = ConcurrentHashMap<String, DelayQueue<DelayCallbackMessage>>()

  @Volatile
  private var startThread = true

  override fun publisherEvent(eventPushInfos: Flux<EventPushInfo>): Flux<PublishRecord> {
    return eventPushInfos.map { event ->
      val success = offerPushMessageToQueue(event)
      if (!success) {
        log.debug("pull EventPushInfo to queue failed -> EventPushInfo=${JsonUtils.toJsonString(event)}")
      }
      PublishRecord(success, event)
    }
  }

  override fun pushCallback(callbackPushInfo: CallbackPushInfo): Mono<Unit> {
    offerCallbackMessageToQueue(callbackPushInfo)
    return Mono.just(Unit)
  }

  override fun run(args: ApplicationArguments?) {
    readFlushedMessages()
    log.debug("AsyncPublisher successful initialized...")
  }

  override fun destroy() {
    startThread = false
    flushMessages()
    log.warn("AsyncPublisher is destroyed...")
  }
  //-------------------------------------------------- 私有方法 ---------------------------------------------------//

  /**
   * 创建Event事件消费线程
   */
  private fun createEventConsumeThread(serviceCode: String) {
    val thread = Thread {
      val queue = eventQueueMapping[serviceCode]
      if (queue != null) {
        queue.poll(2, TimeUnit.SECONDS)?.let { pushInfo ->
          val subDetails = pushInfo.subscriptionDetails
          val limit = subDetails.limit
          val pushType = subDetails.pushType
          if (limit < 1) {
            //服务端不限流
            reactiveExecutor.execute {
              MessagePusher.getMessagePusher(pushType).pushEvent(pushInfo)
            }
          } else {
            val currentNodeLimit = (limit / busCache.getNodeCount()).coerceAtLeast(1)
            val current = serverLimits.computeIfAbsent(serviceCode) { AtomicInteger(0) }
            if (currentNodeLimit >= current.decrementAndGet()) {
              reactiveExecutor.execute {
                MessagePusher.getMessagePusher(pushType).pushEvent(pushInfo).subscribe { current.decrementAndGet() }
              }
            } else {
              MessagePusher.getMessagePusher(pushType).pushEvent(pushInfo)
              current.decrementAndGet()
            }
          }
        }
      } else {
        log.debug("eventQueueMapping.get($serviceCode) return null...")
        TimeUnit.SECONDS.sleep(1)
      }
    }
    thread.start()
    threads.add(thread)
  }

  /**
   * 创建Event延迟事件消费线程
   */
  private fun createDelayEventConsumeThread(serviceCode: String) {
    val thread = Thread {
      val queue = eventDelayQueueMapping[serviceCode]
      if (queue != null) {
        queue.poll(2, TimeUnit.SECONDS)?.let { delayEvent ->
          val event = delayEvent.info
          val subDetails = event.subscriptionDetails
          val limit = subDetails.limit
          val pushType = subDetails.pushType
          if (limit < 1) {
            reactiveExecutor.execute {
              MessagePusher.getMessagePusher(pushType).pushEvent(event)
            }
          } else {
            val currentNodeLimit = (limit / busCache.getNodeCount()).coerceAtLeast(1)
            val current = serverLimits.computeIfAbsent(serviceCode) { AtomicInteger(0) }
            if (current.incrementAndGet() <= currentNodeLimit) {
              //不限流
              reactiveExecutor.execute { MessagePusher.getMessagePusher(pushType).pushEvent(event).doOnNext { current.decrementAndGet() } }
            } else {
              MessagePusher.getMessagePusher(pushType).pushEvent(event)
              current.decrementAndGet()
            }
          }
        }
      } else {
        log.warn("eventDelayQueueMapping[${serviceCode}] return null...")
        TimeUnit.SECONDS.sleep(1)
      }
    }
    thread.start()
    threads.add(thread)
  }

  /**
   * 创建回调事件消费线程
   */
  private fun createCallbackConsumeThread(serviceCode: String) {
    val thread = Thread {
      val queue = callbackQueueMapping[serviceCode]
      if (queue != null) {
        queue.poll(2, TimeUnit.SECONDS)?.let { pushInfo ->
          val serviceDetails = pushInfo.serviceDetails
          val limit = serviceDetails.limit
          val pushType = serviceDetails.pushType
          if (limit < 1) {
            reactiveExecutor.execute {
              MessagePusher.getMessagePusher(pushType).pushCallback(pushInfo)
            }
          } else {
            //限流值除以event-bus的节点数量就是当前节点的并行推送限制
            val currentNodeLimit = (limit / busCache.getNodeCount()).coerceAtLeast(1)
            val current = serverLimits.computeIfAbsent(serviceCode) { AtomicInteger(0) }
            if (current.incrementAndGet() <= currentNodeLimit) {
              reactiveExecutor.execute { MessagePusher.getMessagePusher(pushType).pushCallback(pushInfo).doOnNext { current.decrementAndGet() } }
            } else {
              //达到限流值则阻塞当前线程进行推送
              MessagePusher.getMessagePusher(pushType).pushCallback(pushInfo).block()
              current.decrementAndGet()
            }
          }
        }
      } else {
        log.warn("callbackQueueMapping[${serviceCode}] return null...")
        TimeUnit.SECONDS.sleep(1)
      }
    }
    thread.start()
    threads.add(thread)
  }

  /**
   * 创建延迟回调事件消费线程
   */
  private fun createDelayCallbackConsumeThread(serviceCode: String) {
    val thread = Thread {
      val queue = callbackDelayQueueMapping[serviceCode]
      if (queue != null) {
        queue.poll(2, TimeUnit.SECONDS)?.let { delayPushInfo ->
          val event = delayPushInfo.info
          val serviceDetail = event.serviceDetails
          val limit = serviceDetail.limit
          val pushType = serviceDetail.pushType
          if (limit < 1) {
            reactiveExecutor.execute {
              MessagePusher.getMessagePusher(pushType).pushCallback(event)
            }
          } else {
            val currentNodeLimit = (limit / busCache.getNodeCount()).coerceAtLeast(1)
            val current = serverLimits.computeIfAbsent(serviceCode) { AtomicInteger(0) }
            if (current.incrementAndGet() < currentNodeLimit) {
              reactiveExecutor.execute {
                MessagePusher.getMessagePusher(pushType).pushCallback(event).doOnNext { current.decrementAndGet() }
              }
            } else {
              MessagePusher.getMessagePusher(pushType).pushCallback(event).block()
              current.decrementAndGet()
            }
          }
        }
      } else {
        log.warn("callbackDelayQueueMapping[$serviceCode] return null...")
        TimeUnit.SECONDS.sleep(1)
      }
    }
    thread.start()
    threads.add(thread)
  }

  /**
   * EventPushInfo -> ArrayBlockingQueue
   */
  private fun offerPushMessageToQueue(pushInfo: EventPushInfo): Boolean {
    val delayMillis = pushInfo.delayMillis
    val serviceCode = pushInfo.subscriptionDetails.serviceCode
    return if (delayMillis > 0) {
      //延迟消息
      val expire = System.currentTimeMillis() + delayMillis
      val delayEventMessage = DelayEventMessage(pushInfo, expire)
      val delayQueue = eventDelayQueueMapping.computeIfAbsent(serviceCode) {
        createDelayEventConsumeThread(serviceCode)
        log.debug("create new DelayEventConsumeThread，serviceCode=$serviceCode ...")
        DelayQueue()
      }
      delayQueue.offer(delayEventMessage)
    } else {
      //即时消息
      val eventQueue = eventQueueMapping.computeIfAbsent(serviceCode) {
        createEventConsumeThread(serviceCode)
        log.debug("create new EventConsumeThread，serviceCode=$serviceCode ...")
        ArrayBlockingQueue(queueSize)
      }
      eventQueue.offer(pushInfo)
    }
  }

  /**
   * CallbackPushInfo -> ArrayBlockingQueue
   */
  private fun offerCallbackMessageToQueue(callbackPushInfo: CallbackPushInfo): Boolean {
    val delayMillis = callbackPushInfo.delayMillis
    val serviceCode = callbackPushInfo.serviceDetails.serviceCode
    if (delayMillis > 0) {
      //延迟消息
      val expire = System.currentTimeMillis() + delayMillis
      val delayCallbackMessage = DelayCallbackMessage(callbackPushInfo, expire)
      val delayQueue = callbackDelayQueueMapping.computeIfAbsent(serviceCode) {
        createDelayCallbackConsumeThread(serviceCode)
        log.debug("create new DelayCallbackConsumeThread，serviceCode=$serviceCode ...")
        DelayQueue()
      }
      val success = delayQueue.offer(delayCallbackMessage)
      if (!success) {
        log.warn("offer element delayCallbackMessage to callbackDelayQueue failed...")
      }
      return success
    } else {
      val queue = callbackQueueMapping.computeIfAbsent(serviceCode) {
        createCallbackConsumeThread(serviceCode)
        log.debug("create new callbackConsumeThread，serviceCode=$serviceCode ...")
        ArrayBlockingQueue(queueSize)
      }
      val success = queue.offer(callbackPushInfo)
      if (!success) {
        log.warn("offer element callbackPushInfo to callbackQueue failed...")
      }
      return success
    }
  }

  /**
   * 读取持久化消息到队列中
   */
  private fun readFlushedMessages() {
    readFlushedMessage(eventQueueFileName, EventPushInfo::class.java) {
      offerPushMessageToQueue(it)
    }
    readFlushedMessage(eventDelayQueueFileName, DelayEventMessage::class.java) {
      offerPushMessageToQueue(it.info)
    }
    readFlushedMessage(callbackQueueFileName, CallbackPushInfo::class.java) {
      offerCallbackMessageToQueue(it)
    }
    readFlushedMessage(callbackDelayQueueFileName, DelayCallbackMessage::class.java) {
      offerCallbackMessageToQueue(it.info)
    }
  }

  private fun <T> readFlushedMessage(fileName: String, clazz: Class<T>, block: (T) -> Unit) {
    val file = File(fileName)
    if (file.exists()) {
      FileInputStream(file).use { fis ->
        InputStreamReader(fis).use { isr ->
          BufferedReader(isr).use { br ->
            val value = br.readLine()
            val message = JsonUtils.parseJson(value, clazz)
            block.invoke(message)
          }
        }
        fis.close()
      }
      file.delete()
    }
  }

  /**
   * 将未执行的消息持久化到磁盘中
   */
  private fun flushMessages() {
    flushMessage(eventQueueMapping, eventQueueFileName) {
      log.debug("$it 条EventPushInfo保存到${eventQueueFileName}文件中")
    }
    flushMessage(eventDelayQueueMapping, eventDelayQueueFileName) {
      log.debug("$it 条DelayEventMessage保存到${eventDelayQueueFileName}文件中")
    }

    flushMessage(callbackQueueMapping, callbackQueueFileName) {
      log.debug("$it 条CallbackPushInfo保存到${callbackQueueFileName}文件中")
    }
    flushMessage(callbackDelayQueueMapping, callbackDelayQueueFileName) {
      log.debug("$it 条DelayCallbackMessage保存到${callbackDelayQueueFileName}文件中")
    }
  }

  private fun flushMessage(queueMapping: ConcurrentHashMap<String, out AbstractQueue<*>>, fileName: String, block: (Int) -> (Unit)) {
    val count = queueMapping.map { it.value.size }.sum()
    if (count > 0) {
      val file = File(fileName)
      val elements = queueMapping.map { it.value.toArray().asList() }.flatten()
      FileOutputStream(file).use { fos ->
        OutputStreamWriter(fos).use { osw ->
          BufferedWriter(osw).use { bw ->
            elements.forEach { element ->
              bw.write(element.toJsonString())
              bw.newLine()
            }
            bw.flush()
          }
        }
        fos.close()
      }
      block.invoke(count)
    }
  }

  //-------------------------------------------------- 延迟消息体 --------------------------------------------------//

  class DelayEventMessage(val info: EventPushInfo, private val expire: Long) : Delayed {
    override fun compareTo(other: Delayed): Int {
      return (this.getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS)).toInt()
    }

    override fun getDelay(unit: TimeUnit): Long {
      return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
    }
  }

  class DelayCallbackMessage(val info: CallbackPushInfo, private val expire: Long) : Delayed {
    override fun compareTo(other: Delayed): Int {
      return (this.getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS)).toInt()
    }

    override fun getDelay(unit: TimeUnit): Long {
      return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
    }

  }

}