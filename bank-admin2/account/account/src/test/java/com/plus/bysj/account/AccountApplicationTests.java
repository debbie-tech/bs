package com.plus.bysj.account;

import com.plus.bysj.account.beans.resp.AccountQueryResp;
import com.plus.bysj.account.config.FilterUrlsConfig;
import com.plus.bysj.account.entity.Account;
import com.plus.bysj.account.quartz.HelpCutoffTimeJobNew;
import com.plus.bysj.account.quartz.QuartzManager;
import com.plus.bysj.account.utils.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
@Log4j2
class AccountApplicationTests {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    QuartzManager quartzManager;
    @Autowired
    FilterUrlsConfig filterUrlsConfig;

    //邮件测试
    @SneakyThrows
    @Test
    public void contextLoads() {
        MailUtils.sendMail("account20210423@163.com", "测试", "这是正文dfasdfds");
    }

    //redis测试
    @Test
    public void redisTest() {
        log.info(redisUtil.get("11"));
    }

    //定时任务测试
    @Test
    public void cronTest() {
        quartzManager.addJob("1","1", HelpCutoffTimeJobNew.class,2l,new HashMap<>());
    }

    //加解密测试
    @Test
    public void aesTest() {
        String str = AESUtil.aesEncrypt("5615");
        log.info("加密结果："+str);
        str = AESUtil.aesDecrypt(str);
        log.info("解密结果："+str);
    }
    //Entity、Bo、Vo层数据的复制测试
    @Test
    public void beanTest() {
        List list = new ArrayList<>();
        Account account = new Account();
        account.setId("1");
        account.setBalance(20d);
        account.setFrozenMoney(20d);
        account.setType("1");
        Account account2 = new Account();
        account2.setId("2");
        account2.setBalance(20d);
        account2.setFrozenMoney(20d);
        account2.setType("2");
        list.add(account);
        list.add(account2);
        List<AccountQueryResp> accountQueryResps = ColaBeanUtils.copyListProperties(list, AccountQueryResp:: new);
        log.info(accountQueryResps);
    }

    //路径匹配测试
    @Test
    public void AntPathMatcherUtilsTest() {
        Boolean  b = AntPathMatcherUtils.macthSingle("/**/test/**","http://localhost:80/aa/test");
        log.info("AntPathMatcherUtilsTest:"+b);
        Boolean  b1 = AntPathMatcherUtils.macthSingle("**/test/**","http://localhost:80/aa/test/abc");
        log.info("AntPathMatcherUtilsTest1:"+b1);
        Boolean  b11 = AntPathMatcherUtils.macthSingle("/**/test/**","http://localhost:80/test/abc");
        log.info("AntPathMatcherUtilsTest11:"+b11);
        Boolean  b12 = AntPathMatcherUtils.macthSingle("/test/**","http://localhost:80/test/abc");
        log.info("AntPathMatcherUtilsTest12:"+b12);
        List list = new ArrayList();
        list.add("**/test/**");
        list.add("**/api/**");
        Boolean  b2 = AntPathMatcherUtils.match(list,"http://localhost:80/aa/test11/as");
        log.info("AntPathMatcherUtilsTest2:"+b2);
        Boolean  b3 = AntPathMatcherUtils.match(list,"http://localhost:80/aa/test/as");
        log.info("AntPathMatcherUtilsTest3:"+b3);

        List list1 = filterUrlsConfig.getAnon();
        log.info("list1:"+list1);
        Boolean b4 = AntPathMatcherUtils.match(list, "/account/card/ignore/query");
        log.info("AntPathMatcherUtilsTest4:"+b4);
    }


}
