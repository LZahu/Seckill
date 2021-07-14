package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @InterfaceName: SeckillDao
 * @Description: seckill对应的接口
 * @Author: NJU
 * @Date: 2021-06-26 17:12
 * @Version: 1.0
 */
public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId 对应的秒杀商品id
     * @param killTime 秒杀时间
     * @return 表示更新的记录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀商品
     * @param seckillId 秒杀商品的id
     * @return 秒杀商品
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return 秒杀商品列表
     */
    // java没有保存形参的记录，得加注解@Param
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 使用mysql存储过程执行秒杀
     * @param paramMap 存储过程执行需要的参数
     */
    void killByProcedure(Map<String, Object> paramMap);
}
