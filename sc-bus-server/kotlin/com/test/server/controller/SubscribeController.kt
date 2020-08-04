package com.test.server.controller

import com.test.server.service.SubscribeService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author 费世程
 * @date 2020/8/2 17:39
 */
@RestController
@RequestMapping("/subscribe")
class SubscribeController(private val subscribeService: SubscribeService) {


}