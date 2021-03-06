package com.plus.bysj.account.service.impl;

import com.plus.bysj.account.beans.QuickPrincipal;
import com.plus.bysj.account.beans.req.*;
import com.plus.bysj.account.beans.resp.TransactionResp;
import com.plus.bysj.account.beans.resp.commom.BaseResult;
import com.plus.bysj.account.dao.*;
import com.plus.bysj.account.entity.*;
import com.plus.bysj.account.exce.ServiceException;
import com.plus.bysj.account.service.AccountService;
import com.plus.bysj.account.service.TransactionService;
import com.plus.bysj.account.utils.CommonUtil;
import com.plus.bysj.account.utils.MailUtils;
import com.plus.bysj.account.utils.RedisUtil;
import com.plus.bysj.account.utils.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @ProjectName: account
 * @Package: com.plus.bysj.account.service.impl
 * @ClassName: TransactionServiceImpl
 * @Author: rh
 * @Description:
 * @Date: 2021/4/24 13:30
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRep transactionRep;
    @Autowired
    private AccountService accountService;
    @Autowired
    private QuestionRep questionRep;
    @Autowired
    private AccountCardRep accountCardRep;
    @Autowired
    private AccountUserRep accountUserRep;
    @Autowired
    private AccountRep accountRep;
    @Autowired
    private BillRep billRep;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public BaseResult transactionQuery(QueryReq req, HttpServletRequest request) {
        QuickPrincipal quickPrincipal = (QuickPrincipal)request.getUserPrincipal();
        String userId = quickPrincipal.getUserid();
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        if(!StringUtils.isEmpty(req.getId())) {
            transaction.setId(req.getId());
        }
        List<Transaction> list = transactionRep.findAll(Example.of(transaction));
        return ResultUtil.success(list);
    }

    @Override
    @Transactional
    public BaseResult transaction(TransactionReq req, HttpServletRequest request) {
        QuickPrincipal quickPrincipal = (QuickPrincipal)request.getUserPrincipal();
        String userId = quickPrincipal.getUserid();

        AccountUser user = check(req.getTransactionCardNum());
        if(user==null){
            return ResultUtil.fail("????????????????????????");
        }else{
            if(!"2".equals(user.getStatus())){
                return ResultUtil.fail("???????????????????????????");
            }
        }
        Account account = accountService.getAccount(userId,req.getAccountType());
        if(account!=null){
            Double money = account.getBalance();
            if(money>req.getMoney()) {
                Transaction transaction = new Transaction();
                BeanUtils.copyProperties(req, transaction);
                transaction.setCardNum(account.getCardNum());
                transaction.setStatus("0");
                Random rand = new Random();
                String id = CommonUtil.dateFormat(new Date()) + (rand.nextInt(9000) + 1000);
                transaction.setId(id);
                transaction.setCreateTime(new Date());
                transaction.setUpdateTime(transaction.getCreateTime());
                transaction.setCreateBy(userId);
                transaction.setUserId(userId);
                //???????????????
                Double serviceFee = getServiceFee(transaction.getCardNum(), userId, req.getMoney());
                transaction.setServiceFee(serviceFee);
                if (money < (req.getMoney() + serviceFee)) {
                    return ResultUtil.fail("????????????");
                }
                if (StringUtils.isEmpty(req.getTime())) {
                    transaction.setTime(new Date());
                } else {
                    Date time = CommonUtil.dateFormat(req.getTime());
//                    if(time.before(new Date())){
//                        ResultUtil.fail("XI");
//                    }
                    transaction.setTime(time);
                }
                if ("1".equals(req.getTimeType())) {
                    transaction.setTime(new Date());
                }
                transactionRep.save(transaction);
                //??????
                if ("1".equals(req.getTransactionType())) {
                    if(StringUtils.isEmpty(req.getQuestion())){
                        return ResultUtil.fail("??????????????????");
                    }
                    if(StringUtils.isEmpty(req.getAnswer())){
                        return ResultUtil.fail("??????????????????");
                    }
                    Question question = new Question();
                    question.setQuestion(req.getQuestion());
                    question.setTransactionId(id);
                    question.setAnswer(req.getAnswer());
                    question.setCreateTime(transaction.getCreateTime());
                    question.setUpdateTime(transaction.getCreateTime());
                    question.setCreateBy(userId);
                    questionRep.save(question);
                }
                TransactionResp resp = new TransactionResp();
                resp.setTransactionId(id);
                resp.setTransactionCardNum(req.getTransactionCardNum());
                resp.setMoney(req.getMoney() + serviceFee);
                resp.setServiceFee(serviceFee);
                resp.setName(user.getName());
                //??????
                return ResultUtil.success(resp);
            }else {
                return ResultUtil.fail("????????????");
            }
        }else {
            return ResultUtil.fail("?????????????????????");
        }
    }
   //??????
    @Override
    @Transactional
    public BaseResult transactionTwo(TransactionResp req, HttpServletRequest request) {
        QuickPrincipal quickPrincipal = (QuickPrincipal)request.getUserPrincipal();
        String userId = quickPrincipal.getUserid();
        Optional<Transaction> transactionOptional = transactionRep.findById(req.getTransactionId());
        if(transactionOptional.isPresent()){
            Transaction transaction = transactionOptional.get();
            if(!transaction.getStatus().equals("0")){
                return ResultUtil.fail("?????????????????????");
            }
            //????????????
            String cardNum = transaction.getCardNum();
            String AccoungtType = transaction.getAccountType();
            Account account = getAccount(cardNum,AccoungtType);
            if(account==null){
                return ResultUtil.fail();
            }
            account.setBalance(account.getBalance()-transaction.getMoney()-transaction.getServiceFee());
            account.setFrozenMoney(account.getFrozenMoney()+transaction.getMoney()+transaction.getServiceFee());
            accountRep.save(account);
            //??????????????????
            transaction.setStatus("2");
            transaction.setMark("?????????");
            transactionRep.save(transaction);
            //???????????????
            Bill bill = new Bill();
            bill.setTransactionId(transaction.getId());
            bill.setStatus(transaction.getStatus());
            bill.setMark(transaction.getMark());
            bill.setCreateTime(new Date());
            bill.setUpdateTime(bill.getCreateTime());
            bill.setType("1");
            bill.setMoney(transaction.getMoney()+transaction.getServiceFee());
            bill.setUserId(userId);
            bill.setAccountType(transaction.getAccountType());
            billRep.save(bill);
            //????????????
            // ???????????????
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendMail(transaction.getCardNum(),transaction.getTransactionCardNum(),transaction.getMoney(),transaction.getId());
                }
            }).start();
            return ResultUtil.success();
        }else {
            return ResultUtil.fail("?????????????????????");
        }

    }

    @Override
    @Transactional
    public BaseResult transactionFpjs(TransactionFpjsReq req, HttpServletRequest request) {
        QuickPrincipal quickPrincipal = (QuickPrincipal)request.getUserPrincipal();
        String userId = quickPrincipal.getUserid();
        Optional<Transaction> transactionOptional = transactionRep.findById(req.getTransactionId());
        if(transactionOptional.isPresent()){
            Transaction transaction = transactionOptional.get();
            //?????????????????????
            Account account = accountService.getAccount(userId,transaction.getAccountType());
            if(account.getBalance()<(transaction.getMoney()+transaction.getServiceFee())){
                return ResultUtil.fail("????????????");
            }
            transaction.setStatus("2");
            transaction.setMark("?????????");
            transactionRep.save(transaction);
            //?????????????????????
            String cardNum = transaction.getCardNum();
            String AccoungtType = transaction.getAccountType();
            account.setBalance(account.getBalance()-transaction.getMoney()-transaction.getServiceFee());
            account.setFrozenMoney(account.getFrozenMoney()+transaction.getMoney()+transaction.getServiceFee());
            accountRep.save(account);
            return ResultUtil.success();
        }else {
            return ResultUtil.fail("?????????????????????");
        }

    }

    @Override
    @Transactional
    public BaseResult transactionFpConfirm(TransactionFpjsReq req, HttpServletRequest request) {
        QuickPrincipal quickPrincipal = (QuickPrincipal)request.getUserPrincipal();
        String userId = quickPrincipal.getUserid();
        Optional<Transaction> transactionOptional = transactionRep.findById(req.getTransactionId());
        if(transactionOptional.isPresent()){
            Transaction transaction = transactionOptional.get();
            //?????????????????????
            Account account = accountService.getAccount(userId,transaction.getAccountType());
            if(account.getBalance()<(transaction.getMoney()+transaction.getServiceFee())){
                return ResultUtil.fail("????????????");
            }
            //?????????????????????
            account.setFrozenMoney(account.getFrozenMoney()-transaction.getMoney()-transaction.getServiceFee());
            accountRep.save(account);
            //?????????????????????
            String cardNum = transaction.getTransactionCardNum();
            String AccoungtType = transaction.getTransactionType();
            Account skr = getAccount(cardNum,AccoungtType);
            if(skr==null){
                return ResultUtil.fail();
            }
            skr.setBalance(skr.getBalance()+transaction.getMoney());
            accountRep.save(skr);
            transaction.setStatus("3");
            transaction.setMark("??????");
            transactionRep.save(transaction);
            //????????????????????????
            Bill bill = new Bill();
            bill.setTransactionId(transaction.getId());
            bill.setStatus(transaction.getStatus());
            bill.setMark(transaction.getMark());
            bill.setCreateTime(new Date());
            bill.setUpdateTime(bill.getCreateTime());
            bill.setType("1");
            bill.setMoney(transaction.getMoney()+transaction.getServiceFee());
            bill.setUserId(account.getUserId());
            bill.setAccountType(transaction.getAccountType());
            billRep.save(bill);
            //????????????????????????
            bill = new Bill();
            bill.setTransactionId(transaction.getId());
            bill.setStatus(transaction.getStatus());
            bill.setMark(transaction.getMark());
            bill.setCreateTime(new Date());
            bill.setUpdateTime(bill.getCreateTime());
            bill.setType("2");
            bill.setMoney(transaction.getMoney());
            bill.setUserId(skr.getUserId());
            bill.setAccountType(transaction.getTransactionAccountType());
            billRep.save(bill);
            //???????????? ?????????????????????
            // ???????????????
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendFpConfirmMail(transaction.getCardNum(),transaction.getTransactionCardNum(),transaction.getMoney(),transaction.getId());
                }
            }).start();
            return ResultUtil.success();
        }else {
            return ResultUtil.fail("?????????????????????");
        }
    }

    @Override
    public BaseResult transactionInQuery(PageReq req, HttpServletRequest request) {
        QuickPrincipal quickPrincipal = (QuickPrincipal)request.getUserPrincipal();
        String userId = quickPrincipal.getUserid();
        Pageable pageable = PageRequest.of(req.getCurrentPage()-1,req.getPageSize(), Sort.by("createTime"));
        AccountUser user = get(userId);
        String cardNum = user.getCardNum();
//        Transaction transaction = new Transaction();
//        transaction.setTransactionCardNum(cardNum);
//        Page page = transactionRep.findAll(Example.of(transaction),pageable);
        Specification<Transaction> specification = (Specification<Transaction>) (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            // ?????????userId???Bill????????????????????????userId?????????
            Predicate p1 = criteriaBuilder.equal(root.get("transactionCardNum"), cardNum);
            list.add(p1);

            Predicate p2 = criteriaBuilder.lessThanOrEqualTo(root.get("time").as(Date.class), new Date());
            list.add(p2);

            if(!StringUtils.isEmpty(req.getTransactionType())){
                Predicate p3 = criteriaBuilder.equal(root.get("transactionType"), req.getTransactionType());
                list.add(p3);
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page page =  transactionRep.findAll(specification, pageable);
        return ResultUtil.success(page);
    }

    //????????????
    @Override
    @Transactional
    public BaseResult transactionConfirm(TransactionConfirmReq req, HttpServletRequest request) {
        QuickPrincipal quickPrincipal = (QuickPrincipal)request.getUserPrincipal();
        String userId = quickPrincipal.getUserid();
        String transactionId = req.getTransactionId();
        //???????????????
        Optional<Transaction> transactionOptional  = transactionRep.findById(transactionId);
        if(transactionOptional.isPresent()){
            Question question = questionRep.findByTransactionId(req.getTransactionId());
            Transaction transaction = transactionOptional.get();
            //????????????
            if(question!=null) {
                if(!question.getAnswer().equals(req.getAnswer())){
                    String num = StringUtils.isEmpty(question.getErrorNum())?"0":question.getErrorNum();
                    Integer integer = Integer.valueOf(num);
                    if(integer>=3){
                        //TODO ??????
                        //????????????????????????
                        transaction.setStatus("-1");
                        transaction.setMark("??????,????????????????????????");
                        transactionRep.save(transaction);
                        updateBillStatus(transaction.getId(),transaction.getStatus(),transaction.getUserId(), transaction.getMark());
                        double money = transaction.getMoney()+transaction.getServiceFee();
                        String accountType = transaction.getAccountType();
                        String cardNum = transaction.getCardNum();
                        updateFailAccount(cardNum,accountType,transaction.getUserId(),money);
                        return ResultUtil.fail("????????????????????????");

                    }else {
                        question.setErrorNum(String.valueOf(integer+1));
                        return ResultUtil.fail("???????????????");
                    }
                }
            }else {
                return ResultUtil.fail("???????????????");
            }

            //?????????????????????
            if("2".equals(transaction.getStatus())){
                //TODO
                //????????????????????????
                transaction.setStatus("3");
                transaction.setMark("??????");
                transactionRep.save(transaction);
                updateBillStatus(transaction.getId(),transaction.getStatus(),transaction.getUserId(), transaction.getMark());
                double money = transaction.getMoney()+transaction.getServiceFee();
                String accountType = transaction.getAccountType();
                String cardNum = transaction.getCardNum();
                updateSuccessAccount(cardNum,accountType,transaction.getUserId(),money);
                //????????????????????????
                updateSuccessThirdAccount(transaction.getTransactionCardNum(),transaction.getTransactionAccountType(),transaction);

                //???????????? ?????????????????????
                // ???????????????
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendConfirmMail(transaction.getCardNum(),transaction.getTransactionCardNum(),transaction.getMoney(),transaction.getId());
                    }
                }).start();
                return ResultUtil.success("????????????");
            }else if("1".equals(transaction.getStatus())){
                return ResultUtil.fail("?????????????????????");
            }else {
                return ResultUtil.fail("?????????????????????");
            }
        }else {
            return ResultUtil.fail("??????????????????");
        }

    }

    //????????????
    @Override
    @Transactional
    public BaseResult transactionCancel(TransactionCancelReq req, HttpServletRequest request) {
        QuickPrincipal quickPrincipal = (QuickPrincipal)request.getUserPrincipal();
        String userId = quickPrincipal.getUserid();
        //???????????????
        Optional<Transaction> transactionOptional  = transactionRep.findById(req.getTransactionId());
        if(transactionOptional.isPresent()){
            Transaction transaction = transactionOptional.get();
            //?????????????????????
            if("2".equals(transaction.getStatus())||"0".equals(transaction.getStatus())){
//                if(!"2".equals(transaction.getTransactionType())){
//                    return ResultUtil.success("????????????");
//                }
                transaction.setStatus("1");
                transaction.setMark("??????");
                transactionRep.save(transaction);
                //?????????????????????????????????
                updateBillStatus(transaction.getId(),transaction.getStatus(),userId,transaction.getMark());
                double money = transaction.getMoney()+transaction.getServiceFee();
                String accountType = transaction.getAccountType();
                String cardNum = transaction.getCardNum();
                updateAccount(cardNum,accountType,userId,money);
                //???????????? ?????????????????????
                // ???????????????
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendCancelMail(transaction.getCardNum(),transaction.getTransactionCardNum(),transaction.getMoney(),transaction.getId());
                    }
                }).start();
                return ResultUtil.success("????????????");
            }else if("1".equals(transaction.getStatus())){
                return ResultUtil.fail("?????????????????????");
            }else {
                return ResultUtil.fail("???????????????????????????");
            }
        }else {
            return ResultUtil.fail("??????????????????");
        }
    }

    @Override
    public BaseResult transactionFp(PageReq req, HttpServletRequest request) {
        QuickPrincipal quickPrincipal = (QuickPrincipal)request.getUserPrincipal();
        String userId = quickPrincipal.getUserid();
        Pageable pageable = PageRequest.of(req.getCurrentPage()-1,req.getPageSize(), Sort.by("createTime"));
        AccountUser user = get(userId);
        String cardNum = user.getCardNum();
        Specification<Transaction> specification = (Specification<Transaction>) (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            // ?????????userId???Bill????????????????????????userId?????????
            Predicate p1 = criteriaBuilder.equal(root.get("cardNum"), cardNum);
            list.add(p1);

            Predicate p2 = criteriaBuilder.equal(root.get("status"), "0");//????????????????????????
            list.add(p2);

            if(StringUtils.isEmpty(req.getTransactionType())){
                req.setTransactionType("2");
            }
            Predicate p3 = criteriaBuilder.equal(root.get("transactionType"), req.getTransactionType());
            list.add(p3);

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page page =  transactionRep.findAll(specification, pageable);
        return ResultUtil.success(page);
    }


    private void updateBillStatus(String id, String status, String userId, String mark){
        billRep.updateBillStatus(id,status,userId,mark);
    }

    private void updateSuccessAccount(String cardNum,String accountType,String userId,double money){
        Account account = new Account();
        account.setCardNum(cardNum);
        account.setUserId(userId);
        account.setType(accountType);
        Account account1 = accountRep.findOne(Example.of(account)).get();
        account1.setFrozenMoney(account1.getFrozenMoney()-money);
        account1.setUpdateTime(new Date());
        accountRep.save(account1);
    }

    private void updateFailAccount(String cardNum,String accountType,String userId,double money){
        Account account = new Account();
        account.setCardNum(cardNum);
        account.setUserId(userId);
        account.setType(accountType);
        Account account1 = accountRep.findOne(Example.of(account)).get();
        account1.setFrozenMoney(account1.getFrozenMoney()-money);
        account1.setBalance(account1.getBalance()+money);
        account1.setUpdateTime(new Date());
        accountRep.save(account1);
    }

    private void updateSuccessThirdAccount(String cardNum,String accountType,Transaction transaction){
        Account account = new Account();
        account.setCardNum(cardNum);
        account.setType(accountType);
        Account account1 = accountRep.findOne(Example.of(account)).get();
        account1.setBalance(account1.getBalance()+transaction.getMoney());
        account1.setUpdateTime(new Date());

        String userId = account1.getUserId();
        Bill bill = new Bill();
        bill.setAccountType(accountType);
        bill.setTransactionId(transaction.getId());
        bill.setType("2");
        bill.setMark(transaction.getMark());
        bill.setStatus(transaction.getStatus());
        bill.setMoney(transaction.getMoney());
        bill.setUserId(userId);
        bill.setCreateTime(new Date());
        bill.setUpdateTime(bill.getCreateTime());
        accountRep.save(account1);
        billRep.save(bill);
    }

    private void updateAccount(String cardNum,String accountType,String userId,double money){
        Account account = new Account();
        account.setCardNum(cardNum);
        account.setUserId(userId);
        account.setType(accountType);
        Account account1 = accountRep.findOne(Example.of(account)).get();
        account1.setBalance(account1.getBalance()+money);
        account1.setFrozenMoney(account1.getFrozenMoney()-money);
        account1.setUpdateTime(new Date());
        accountRep.save(account1);
    }

    private void sendConfirmMail(String cardNum,String transactionCardNum,Double money,String transactionId)  {
        AccountUser user = check(cardNum);
        AccountUser user1 = check(transactionCardNum);
        if(user!=null&&user1!=null){
            try {
                String content = user.getName()+"("+user.getCardNum()+")transfer money to you "+money+" and successfully arrived";
                MailUtils.sendMail(user1.getEmail(),"Successful transaction notification",content);
                content = transactionId+"Successful transaction ";
                MailUtils.sendMail(user.getEmail(),"Successful transaction notification",content);
            } catch (MessagingException e) {
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        }
    }

    private void sendFpConfirmMail(String cardNum,String transactionCardNum,Double money,String transactionId)  {
        AccountUser user = check(cardNum);
        AccountUser user1 = check(transactionCardNum);
        if(user!=null&&user1!=null){
            try {
                String content = user.getName()+"("+user.getCardNum()+")'s billing("+transactionId+")successfully arrived";
                MailUtils.sendMail(user1.getEmail(),"Successful transaction notification",content);
                content = transactionId+"successful transaction";
                MailUtils.sendMail(user.getEmail(),"Successful transaction notification",content);
            } catch (MessagingException e) {
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        }
    }

    private void sendCancelMail(String cardNum,String transactionCardNum,Double money,String transactionId)  {
        AccountUser user = check(cardNum);
        AccountUser user1 = check(transactionCardNum);
        if(user!=null&&user1!=null){
            try {
                String content = user.getName()+"("+user.getCardNum()+")transfer to you"+money+"which has been cancelled.";
                MailUtils.sendMail(user1.getEmail(),"Transaction cancellation notice",content);
                content = transactionId+"Transaction canceled successfully";
                MailUtils.sendMail(user.getEmail(),"Transaction cancellation notice",content);
            } catch (MessagingException e) {
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        }
    }

    private void sendMail(String cardNum,String transactionCardNum,Double money,String transactionId)  {
        AccountUser user = check(cardNum);
        AccountUser user1 = check(transactionCardNum);
        if(user!=null&&user1!=null){
            Question anw = questionRep.findByTransactionId(transactionId);
            String content = user.getName()+"("+user.getCardNum()+")transfer to you"+money+",The money will be deposited into a check or a saving account. The question is:"+anw.getQuestion();
            try {
                MailUtils.sendMail(user1.getEmail(),"transfer notification",content);
            } catch (MessagingException e) {
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        }
    }


    public Account getAccount(String cradNum, String accountType) {
        Account account = new Account();
        account.setCardNum(cradNum);
        account.setType(accountType);
        Optional<Account> list =  accountRep.findOne(Example.of(account));
        if(list.isPresent()){
            return list.get();
        }else {
            return null;
        }

    }

    private AccountUser get(String userId){
        return accountUserRep.findById(userId).get();
    }


    //????????????????????????
    private AccountUser check(String transactionCardNum){
        AccountUser user = new AccountUser();
        user.setCardNum(transactionCardNum);
        Optional<AccountUser> accountUserOptional =accountUserRep.findOne(Example.of(user));
        if(accountUserOptional.isPresent()){
            return accountUserOptional.get();
        }else {
            return null;
        }
    }

    //?????????
    private Double getServiceFee(String cardNum, String userId, Double money){
        Transaction transaction = new Transaction();
        transaction.setCardNum(cardNum);
        Long num = transactionRep.count(Example.of(transaction));
        AccountCard accountCard = accountCardRep.findAccountCard(cardNum,userId);
        if(num<Long.parseLong(accountCard.getFreeNum())){
            return 0.0;
        }else{
            String rate = accountCard.getRate();
            Double rat = 0.0;
            if(rate.contains("%")){
                rat = Double.parseDouble(rate.replace("%",""))*0.01;
            }else {
                rat = Double.parseDouble(rate);
            }
            return money*rat;
        }

    }
}
