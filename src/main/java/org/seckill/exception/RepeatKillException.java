package org.seckill.exception;

/**
 * @ClassName: RepeatKillException
 * @Description: 重复秒杀异常（运行时异常）
 * @Author: LiuZhen
 * @Date: 2021-06-27 18:06
 */
public class RepeatKillException extends SeckillException {
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
