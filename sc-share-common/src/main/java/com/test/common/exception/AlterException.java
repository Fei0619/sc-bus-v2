package com.test.common.exception;

import com.test.common.result.ResMsg;

/**
 * @author 费世程
 * @date 2020/8/12 16:06
 */
public class AlterException extends RuntimeException {

  public AlterException(String message) {
    super(message);
  }

  public AlterException(ResMsg resMsg) {
    super(resMsg.message());
  }

}
