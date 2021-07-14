package org.seckill.dto;

import org.seckill.entity.SuccessKill;
import org.seckill.enums.SeckillStatesEnum;

/**
 * @ClassName: SeckillExecution
 * @Description: 封装秒杀执行后结果的类
 * @Author: LiuZhen
 * @Date: 2021-06-27 18:01
 */
public class SeckillExecution {
    private long seckillId;
    // 秒杀执行结果的状态
    private int state;
    // 状态标识
    private String stateInfo;
    // 秒杀成功对象
    private SuccessKill successKill;

    public SeckillExecution(long seckillId, SeckillStatesEnum statesEnum, SuccessKill successKill) {
        this.seckillId = seckillId;
        this.state = statesEnum.getState();
        this.stateInfo = statesEnum.getStateInfo();
        this.successKill = successKill;
    }

    public SeckillExecution(long seckillId, SeckillStatesEnum statesEnum) {
        this.seckillId = seckillId;
        this.state = statesEnum.getState();
        this.stateInfo = statesEnum.getStateInfo();
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKill getSuccessKill() {
        return successKill;
    }

    public void setSuccessKill(SuccessKill successKill) {
        this.successKill = successKill;
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKill=" + successKill +
                '}';
    }
}
