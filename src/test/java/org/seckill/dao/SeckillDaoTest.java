package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置Spring和junit整合，为了junit启动时能够加载SpringIOC容器
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {
    // 注入Dao实现类
    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() {
        Date killTime = new Date();
        long id = 1004L;
        int updateCount = seckillDao.reduceNumber(id, killTime);
        System.out.println("updateCount : " + updateCount);
    }

    @Test
    public void queryById() {
        long id = 1004L;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill);
    }

    @Test
    public void queryAll() {
        int offset = 0;
        int limit = 4;
        List<Seckill> seckills = seckillDao.queryAll(offset, limit);
        for (Seckill s : seckills) {
            System.out.println(s);
        }
    }
}