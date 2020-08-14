package com.test.api.publisher

import com.test.common.result.PublishResult
import com.test.common.result.Res

/**
 * @author 费世程
 * @date 2020/8/13 17:08
 */
interface EventPublisher : BasicEventPublisher<Res<PublishResult>>