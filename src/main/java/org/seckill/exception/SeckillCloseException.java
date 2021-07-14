package org.seckill.exception;

/**
 * @ClassName: SeckillCloseException
 * @Description: 秒杀关闭异常（运行时异常）
 * @Author: LiuZhen
 * @Date: 2021-06-27 18:08
 */
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
