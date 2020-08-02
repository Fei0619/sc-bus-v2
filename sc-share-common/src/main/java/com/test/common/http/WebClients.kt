package com.test.common.http

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.util.StringUtils
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * 依赖 spring-webflux / reactor-netty
 *
 * @date 2019/9/10
 */
@Suppress("unused")
object WebClients {
  private val httpClients = ConcurrentHashMap<String, HttpClient>()

  /**
   * 创建一个[WebClient.Builder]
   * @param connectTimeOut 建立连接的超时时间
   * @param writeTimeOut 写入超时时间
   * @param readTimeOut 读取超时时间
   * @author 宋志宗
   * @date 2019/9/9
   */
  @Suppress("MemberVisibilityCanBePrivate")
  fun createWebClientBuilder(connectTimeOut: Int = 200, writeTimeOut: Long = 400, readTimeOut: Long = 400): WebClient.Builder {
    val httpClient = getHttpClient(connectTimeOut, writeTimeOut, readTimeOut)
    return WebClient.builder().clientConnector(ReactorClientHttpConnector(httpClient))
  }

  /**
   * 创建一个[WebClient]
   *
   * @param baseUrl baseUrl
   * @param connectTimeOut 建立连接的超时时间
   * @param writeTimeOut 写入超时时间
   * @param readTimeOut 读取超时时间
   * @author 宋志宗
   * @date 2019/9/9
   */
  @Suppress("MemberVisibilityCanBePrivate")
  fun createWebClient(baseUrl: String = "", connectTimeOut: Int = 200, writeTimeOut: Long = 400, readTimeOut: Long = 400): WebClient {
    val webClientBuilder = createWebClientBuilder(connectTimeOut, writeTimeOut, readTimeOut)
    if (!StringUtils.isEmpty(baseUrl)) {
      webClientBuilder.baseUrl(baseUrl)
    }
    return webClientBuilder.build()
  }

  /**
   * 创建一个[WebClient]
   *
   * @param connectTimeOut 建立连接的超时时间
   * @param writeTimeOut 写入超时时间
   * @param readTimeOut 读取超时时间
   * @author 宋志宗
   * @date 2019/9/9
   */
  fun createWebClient(connectTimeOut: Int = 200, writeTimeOut: Long = 400, readTimeOut: Long = 400): WebClient {
    return createWebClient("", connectTimeOut, writeTimeOut, readTimeOut)
  }

  private fun getHttpClient(connectTimeOut: Int = 200, writeTimeOut: Long = 400, readTimeOut: Long = 400): HttpClient {
    val key = "$connectTimeOut:$writeTimeOut:$readTimeOut"
    return httpClients.computeIfAbsent(key) {
      createHttpClient(connectTimeOut, writeTimeOut, readTimeOut)
    }
  }

  private fun createHttpClient(connectTimeOut: Int, writeTimeOut: Long, readTimeOut: Long): HttpClient {
    return HttpClient.create()
        .tcpConfiguration { client ->
          client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeOut)
              .option(ChannelOption.SO_KEEPALIVE, true)
              .option(ChannelOption.TCP_NODELAY, true)
              .doOnConnected { conn ->
                conn.addHandler(WriteTimeoutHandler(writeTimeOut, TimeUnit.MILLISECONDS))
                conn.addHandler(ReadTimeoutHandler(readTimeOut, TimeUnit.MILLISECONDS))
              }
        }
  }
}