package org.seckill.enums;

/**
 * 使用枚举类表示常量数据字段
 */
public enum SeckillStatesEnum {
    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "重复秒杀"),
    INNER_ERROR(-2, "系统异常"),
    DATA_REWRITE(-3, "数据篡改");

    // 状态
    private int state;
    // 状态标识
    private String stateInfo;

    SeckillStatesEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static SeckillStatesEnum stateOf(int index) {
        for (SeckillStatesEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }
}
