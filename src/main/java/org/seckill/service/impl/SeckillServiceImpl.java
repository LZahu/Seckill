package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKillDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKill;
import org.seckill.enums.SeckillStatesEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SeckillServiceImpl
 * @Description:
 * @Author: LiuZhen
 * @Date: 2021-06-27 18:17
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    // DAO接口
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKillDao successKillDao;
    @Autowired
    private RedisDao redisDao;
    // 日志
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    // md5的盐值字符串，用于混淆md5
    private final String slat = "uiidfuhdhsimifw3iuh2i31";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 5);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        // 优化点：缓存优化，在超时的基础上维护数据一致性
        // 1.首先访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            // 2.如果redis中没有数据，则访问数据库
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                // 3.如果数据库中也没有，说明商品不存在
                return new Exposer(false, seckillId);
            } else {
                // 3.如果存在，则放入redis
                redisDao.putSeckill(seckill);
            }
        }

        // 查询当前时间和秒杀商品对应的开始和结束时间
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        // 如果时间错误
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }

        // 转换特定字符串的过程，不可逆
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        // md5为空，或者与当前商品对应的md5不符合，说明数据被改变或者重写了
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }

        // 执行秒杀业务逻辑：减库存 + 记录购买行为
        Date nowTime = new Date();

        // 简单的优化后代码（先记录购买行为，再更新库存）
        try {
            // 记录购买行为
            int insertCount = successKillDao.insertSuccessKill(seckillId, userPhone);
            if (insertCount <= 0) {
                // 重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                // 减库存（需要拿到MySQL的行级锁）
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    // 减库存失败，rollback
                    throw new SeckillCloseException("seckill closed");
                } else {
                    // 减库存成功，commit
                    SuccessKill successKill = successKillDao.querybyIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatesEnum.SUCCESS, successKill);
                }
            }
        } catch (SeckillCloseException | RepeatKillException e1) {
            throw e1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 所有编译器异常，转化为运行期异常
            throw new SeckillException("seckill inner error: " + e.getMessage());
        }

        // 优化前代码
//        try {
//            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
//            if (updateCount <= 0) {
//                // 没有更新记录
//                throw new SeckillCloseException("seckill is closed");
//            } else {
//                // 记录购买行为
//                int insertCount = successKillDao.insertSuccessKill(seckillId, userPhone);
//                if (insertCount <= 0) {
//                    // 用户重复秒杀
//                    throw new RepeatKillException("seckill repeated");
//                } else {
//                    // 秒杀成功
//                    SuccessKill successKill = successKillDao.querybyIdWithSeckill(seckillId, userPhone);
//                    return new SeckillExecution(seckillId, SeckillStatesEnum.SUCCESS, successKill);
//                }
//            }
//        } catch (SeckillCloseException | RepeatKillException e1) {
//            throw e1;
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            // 所有编译器异常，转化为运行期异常
//            throw new SeckillException("seckill inner error: " + e.getMessage());
//        }
    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        // md5为空，或者与当前商品对应的md5不符合，说明数据被改变或者重写了
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStatesEnum.DATA_REWRITE);
        }

        // 当前的秒杀时间
        Date killTime = new Date();

        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        try {
            seckillDao.killByProcedure(map);
            // 获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKill successKill = successKillDao.querybyIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStatesEnum.SUCCESS, successKill);
            } else {
                return new SeckillExecution(seckillId, SeckillStatesEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStatesEnum.INNER_ERROR);
        }
    }

    private String getMd5(long seckillId) {
        String base = seckillId + "/" + slat;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
