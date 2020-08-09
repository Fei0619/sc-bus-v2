package com.test.server.controller

import com.test.common.result.Res
import com.test.server.dto.AutoSubscribeDto
import com.test.server.mongo.document.ServiceDetails
import com.test.server.service.SubscribeService
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/2 17:39
 */
@RestController
@RequestMapping("/subscribe")
class SubscribeController(private val subscribeService: SubscribeService) {

  /**
   * 自动订阅
   * @param dto AutoSubscribeDto - 自动订阅信息
   * @return
   * @author 费世程
   * @date 2020/8/10 0:28
   */
  @PostMapping("/autoSubscription")
  fun autoSubscribe(dto: AutoSubscribeDto): Mono<Res<ServiceDetails>> {
    return subscribeService.autoSubscribe(dto)
  }

}