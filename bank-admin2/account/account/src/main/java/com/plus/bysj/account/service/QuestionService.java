package com.plus.bysj.account.service;

import com.plus.bysj.account.beans.req.QuestionQueryReq;
import com.plus.bysj.account.beans.resp.commom.BaseResult;

/**
 * @ProjectName: account
 * @Package: com.plus.bysj.account.service
 * @ClassName: QuestionService
 * @Author: rh
 * @Description:
 * @Date: 2021/4/25 23:25
 */
public interface QuestionService {
    BaseResult questionService(QuestionQueryReq req);
}
