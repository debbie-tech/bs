package com.plus.bysj.account.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.plus.bysj.account.beans.resp.commom.BaseResult;
import com.plus.bysj.account.dao.AccountCardRep;
import com.plus.bysj.account.entity.AccountCard;
import com.plus.bysj.account.service.AccountCardService;
import com.plus.bysj.account.utils.RedisUtil;
import com.plus.bysj.account.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ProjectName: account
 * @Package: com.plus.bysj.account.service.impl
 * @ClassName: AccountCardServiceImpl
 * @Author: rh
 * @Description:
 * @Date: 2021/4/22 23:03
 */
@Service
public class AccountCardServiceImpl implements AccountCardService {
    @Autowired
    private AccountCardRep accountCardRep;
    @Autowired
    private RedisUtil redisUtil;

    private static final String key = "accountCard";

    @Override
    public BaseResult queryCrad() {
        //AccountCard accountCard = new AccountCard();
        if(redisUtil.hasKey(key)){
            return JSONObject.parseObject(redisUtil.get(key).toString(),BaseResult.class);
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "seq");
        List<AccountCard> accountCards = accountCardRep.findAll(sort);
        redisUtil.set(key,JSONObject.toJSONString(ResultUtil.success(accountCards)));
        return ResultUtil.success(accountCards);
    }
}
