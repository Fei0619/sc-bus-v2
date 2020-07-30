package com.test.api.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author 费世程
 * @date 2020/7/30 10:51
 */
@Component
@ConfigurationProperties(prefix = "bus")
class BusClientProperties {

}