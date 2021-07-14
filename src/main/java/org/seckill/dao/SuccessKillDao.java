package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKill;

/**
 * @InterfaceName: SuccessKillDao
 * @Description: success_kill的接口
 * @Author: NJU
 * @Date: 2021-06-26 17:20
 * @Version: 1.0
 */
public interface SuccessKillDao {

    /**
     * 插入购买明细。可过滤重复
     * @param seckillId 秒杀商品的id
     * @param userPhone 用户手机号，作为验证
     * @return 表示插入的行数
     */
    int insertSuccessKill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据id查询SuccessKill，并携带秒杀商品对象实体Seckill
     * @param seckillId 秒杀商品的id
     * @return 购买明细（SuccessKill实体对象）
     */
    SuccessKill querybyIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
