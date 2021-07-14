package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKillDaoTest {
    @Autowired
    private SuccessKillDao successKillDao;

    @Test
    public void insertSuccessKill() {
        long id = 1001L;
        long phone = 17380417669L;
        int insertCount = successKillDao.insertSuccessKill(id, phone);
        System.out.println("insertCount : " + insertCount);
    }

    @Test
    public void querybyIdWithSeckill() {
        long id = 1001L;
        long phone = 17380417669L;
        SuccessKill successKill = successKillDao.querybyIdWithSeckill(id, phone);
        System.out.println(successKill);
        System.out.println(successKill.getSeckill());
    }
}