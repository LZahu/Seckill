package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatesEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: SeckillController
 * @Description: Seckill的Controller类
 * @Author: NJU
 * @Date: 2021-06-28 12:24
 * @Version: 1.0
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {
    @Autowired
    private SeckillService seckillService;

    /**
     * 获取所有秒杀商品的信息列表
     *
     * @param model model
     * @return 列表页
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        // 获取列表页
        List<Seckill> seckillList = seckillService.getSeckillList();
        model.addAttribute("seckillList", seckillList);
        // list.jsp + model = ModelAndView
        return "list";
    }

    /**
     * 获取某个商品的详情
     *
     * @param seckillId 商品id
     * @param model     model
     * @return 详情页
     */
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        System.out.println("come into detail function");
        System.out.println("seckillId : " + seckillId);
        System.out.println("model : " + model);

        // 如果商品id为空
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }

        Seckill seckill = seckillService.getById(seckillId);
        // 如果商品不存在
        if (seckill == null) {
            return "forward:seckill/list";
        }

        model.addAttribute("seckill", seckill);
        return "detail";
    }

    /**
     * 获取秒杀接口
     *
     * @param seckillId 商品id
     * @return 封装接口的json
     */
    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<>(true, exposer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = new SeckillResult<>(false, e.getMessage());
        }
        return result;
    }

    /**
     * 执行秒杀操作
     * @param seckillId 商品id
     * @param phone     用户手机，从cookie获取
     * @param md5       加密措施
     * @return 封装了秒杀结果的json
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @CookieValue(value = "killPhone", required = false) Long phone,
                                                   @PathVariable("md5") String md5) {
        if (phone == null) {
            return new SeckillResult<>(false, "用户未登录");
        }

        SeckillResult<SeckillExecution> result;
        try {
            // 存储过程的调用
            SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            result = new SeckillResult<>(true, seckillExecution);
        } catch (RepeatKillException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatesEnum.REPEAT_KILL);
            return new SeckillResult<>(true, execution);
        } catch (SeckillCloseException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatesEnum.END);
            return new SeckillResult<>(true, execution);
        } catch (Exception e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatesEnum.INNER_ERROR);
            return new SeckillResult<>(true, execution);
        }
        return result;
    }

    /**
     * 获取服务器当前的时间
     * @return 封装了当前时间的类
     */
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        System.out.println("come into time function");
        System.out.println(now);
        return new SeckillResult<>(true, now.getTime());
    }
}
