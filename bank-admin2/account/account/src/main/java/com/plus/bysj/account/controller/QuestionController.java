package com.plus.bysj.account.controller;

import com.plus.bysj.account.beans.req.QuestionQueryReq;
import com.plus.bysj.account.beans.resp.commom.BaseResult;
import com.plus.bysj.account.service.AccountCardService;
import com.plus.bysj.account.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: account
 * @Package: com.plus.bysj.account.controller
 * @ClassName: QuestionController
 * @Author: rh
 * @Description:
 * @Date: 2021/4/25 23:24
 */
@RestController
@RequestMapping("/question")
@Api(tags = "问题控制器")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @ApiOperation(value = "问题查询",notes = "问题查询")
    @PostMapping("/questionQuery")
    public BaseResult questionQuery(@RequestBody QuestionQueryReq req){
        return  questionService.questionService(req);
    }
}
