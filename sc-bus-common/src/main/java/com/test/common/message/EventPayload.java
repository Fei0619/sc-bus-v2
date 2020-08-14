package com.test.common.message;

import org.jetbrains.annotations.NotNull;

import java.beans.Transient;

/**
 * @author 费世程
 * @date 2020/8/13 15:20
 */
public interface EventPayload {

  @NotNull
  @Transient
  String getTopic();

}
