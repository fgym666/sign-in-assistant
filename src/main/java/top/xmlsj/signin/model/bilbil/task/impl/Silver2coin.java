package top.xmlsj.signin.model.bilbil.task.impl;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import top.xmlsj.signin.model.bilbil.apiquery.ApiList;
import top.xmlsj.signin.model.bilbil.apiquery.oftenAPI;
import top.xmlsj.signin.model.bilbil.utils.HttpUtil;
import top.xmlsj.signin.task.SigninTask;

/**
 * 银瓜子换硬币
 *
 * @authorForkManTou
 * @since 2020-11-22 5:25
 */
@Slf4j
public class Silver2coin implements SigninTask {

    @Override
    public void run() {

        JsonObject queryStatus = HttpUtil.doGet(ApiList.getSilver2coinStatus).get("data").getAsJsonObject();
        //银瓜子兑换硬币汇率
        final int exchangeRate = 700;
        int silverNum = queryStatus.get("silver").getAsInt();

        if (silverNum < exchangeRate) {
            log.info("当前银瓜子余额为:{},不足700,不进行兑换", silverNum);
        } else {
            JsonObject resultJson = HttpUtil.doGet(ApiList.silver2coin);
            System.out.println(resultJson);
            System.out.println("-================================");
            int responseCode = resultJson.get(TaskInfoHolder.STATUS_CODE_STR).getAsInt();
            if (responseCode == 0) {
                log.info("银瓜子兑换硬币成功");

                double coinMoneyAfterSilver2Coin = oftenAPI.getCoinBalance();

                log.info("当前银瓜子余额: {}", (silverNum - exchangeRate));
                log.info("兑换银瓜子后硬币余额: {}", coinMoneyAfterSilver2Coin);

                //兑换银瓜子后，更新userInfo中的硬币值
                if (TaskInfoHolder.userInfo != null) {
                    TaskInfoHolder.userInfo.setMoney(coinMoneyAfterSilver2Coin);
                }
            } else {
                log.info("银瓜子兑换硬币失败 原因是:{}", resultJson.get("msg").getAsString());
            }
        }
    }

    @Override
    public String getName() {
        return "银瓜子换硬币";
    }

    @Override
    public int getState() {
        return 0;
    }
}
