package com.plus.bysj.account.service.impl;

import com.plus.bysj.account.beans.req.QueryReq;
import com.plus.bysj.account.beans.req.QuestionQueryReq;
import com.plus.bysj.account.beans.resp.QuestionQueryResp;
import com.plus.bysj.account.beans.resp.commom.BaseResult;
import com.plus.bysj.account.dao.QuestionRep;
import com.plus.bysj.account.entity.Question;
import com.plus.bysj.account.service.QuestionService;
import com.plus.bysj.account.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: account
 * @Package: com.plus.bysj.account.service.impl
 * @ClassName: QuestionServiceImpl
 * @Author: rh
 * @Description:
 * @Date: 2021/4/25 23:25
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionRep questionRep;
    @Override
    public BaseResult questionService(QuestionQueryReq req) {
        Question question = questionRep.findByTransactionId(req.getTransactionId());
        if(question!=null) {
            QuestionQueryResp resp = new QuestionQueryResp();
            resp.setQuestion(question.getQuestion());
            resp.setTransactionId(question.getTransactionId());
            return ResultUtil.success(resp);
        }else {
            return ResultUtil.fail("问题不存在");
        }
    }
}
