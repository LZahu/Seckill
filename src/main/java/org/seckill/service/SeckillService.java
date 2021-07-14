package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：应站在“使用者”的角度设计接口
 * 三个方面：方法定义粒度、参数、返回类型
 * @InterfaceName: SeckillService
 * @Description: Seckill的Service层
 * @Author: LiuZhen
 * @Date: 2021-06-27 17:42
 */
public interface SeckillService {

    /**
     * 查询所有秒杀商品记录
     * @return 所有秒杀商品
     */
    List<Seckill> getSeckillList();

    /**
     * 根据id查找某个秒杀商品
     * @param seckillId 秒杀商品id
     * @return 查询的秒杀商品
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址，否则输出系统时间和秒杀时间
     * @param seckillId 秒杀商品id
     * @return 暴露秒杀接口的类
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId 秒杀商品id
     * @param userPhone 用户手机
     * @param md5 加密方式（验证方式）
     * @return 秒杀结果类
     * @throws SeckillException 秒杀相关业务的异常
     * @throws RepeatKillException 重复秒杀异常
     * @throws SeckillCloseException 秒杀关闭的异常
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;

    /**
     * 通过mysql的存储过程来执行秒杀操作
     * @param seckillId 秒杀商品id
     * @param userPhone 用户手机
     * @param md5 加密方式（验证方式）
     * @return 秒杀结果类
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);
}
