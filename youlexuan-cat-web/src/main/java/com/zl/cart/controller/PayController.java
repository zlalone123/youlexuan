package com.zl.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zl.entity.Result;
import com.zl.pay.service.AliPayService;
import com.zl.utils.IdWorker;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference(timeout = 30000)
    private AliPayService aliPayService;

    @RequestMapping("creatNative")
    public Map creatNative(){
        IdWorker idworker=new IdWorker();
        return aliPayService.createNative(idworker.nextId()+"",1.0);
    }


    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result=null;
        int i= 0;
        while(true){
            //调用查询接口
            Map<String, String> map = null;
            try {
                map = aliPayService.queryPayStatus(out_trade_no);

            } catch (Exception e1) {
                /*e1.printStackTrace();*/
                System.out.println("调用查询服务出错");
            }
            if(map==null){//出错
                result=new  Result(false, "支付出错");
                break;
            }
            if(map.get("tradestatus")!=null&&map.get("tradestatus").equals("TRADE_SUCCESS")){//如果成功
                result=new  Result(true, "支付成功");
                break;
            }
            if(map.get("tradestatus")!=null&&map.get("tradestatus").equals("TRADE_CLOSED")){//如果交易关闭
                result=new  Result(true, "未付款交易超时关闭，或支付完成后全额退款");
                break;
            }
            if(map.get("tradestatus")!=null&&map.get("tradestatus").equals("TRADE_FINISHED")){//如果交易结束
                result=new  Result(true, "交易结束，不可退款");
                break;
            }

            i++;
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(i>=100){
               return result=new Result(false,"支付超时");

            }

        }
        return result;
    }
}
