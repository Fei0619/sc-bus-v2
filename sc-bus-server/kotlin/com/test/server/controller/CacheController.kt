package com.test.server.controller

import com.test.common.result.Res
import com.test.server.service.CacheService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author 费世程
 * @date 2020/8/13 10:08
 */
@RestController
@RequestMapping("/cache")
class CacheController(private val cacheService: CacheService) {

  /**
   * 刷新本地事件订阅缓存
   */
  @GetMapping("/refresh")
  fun refreshCache(): Res<Void> {
    cacheService.refreshCache()
    return Res.success()
  }

}