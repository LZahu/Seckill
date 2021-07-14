-- 秒杀执行的存储过程：优化事务行级锁的持有时间（但不要过度依赖存储过程），QPS：一个秒杀单大约6000/qps
delimiter $$ -- console ; 转换为 $$

-- 定义存储过程，in表示输入参数，out表示输出参数
create procedure `seckill`.`execute_seckill`(in v_seckill_id bigint, in v_phone bigint, in v_kill_time timestamp,
                                             out r_result int)
begin
    declare insert_count int default 0;
    start transaction;
    insert ignore into success_kill (seckill_id, user_phone, create_time) value (v_seckill_id, v_phone, v_kill_time);
    -- row_count()：返回上一级修改类型sql(delete, insert, update)的影响行数
    -- 0表示未修改数据，>0表示修改的行数，<0表示错误或者未修改sql
    select row_count() into insert_count;
    if (insert_count = 0) then
        -- 没有记录购买明细，说明重复秒杀
        rollback;
        set r_result = -1;
    elseif (insert_count < 0) then
        -- 记录购买明细出错
        rollback;
        set r_result = -2;
    else
        -- 记录购买明细成功，则进一步更新库存
        update seckill
        set number = number - 1
        where seckill_id = v_seckill_id
          and end_time > v_kill_time
          and start_time < v_kill_time
          and number > 0;
        select row_count() into insert_count;
        if (insert_count = 0) then
            rollback;
            set r_result = -1;
        elseif (insert_count < 0) then
            rollback;
            set r_result = -2;
        else
            commit;
            set r_result = 1;
        end if;
    end if;
end $$

-- 存储过程定义结束
delimiter ;

-- 以下是对存储过程的测试
select @r_result = 3;
call execute_seckill(1001, 123456789, now(), @r_result);
select @r_result;
