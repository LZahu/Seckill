package org.seckill.exception;

/**
 * @ClassName: SeckillException
 * @Description: 所有秒杀相关业务的异常
 * @Author: LiuZhen
 * @Date: 2021-06-27 18:09
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
