package com.plus.bysj.account.beans.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ProjectName: account
 * @Package: com.plus.bysj.account.beans.req
 * @ClassName: TransactionConfirmReq
 * @Author: rh
 * @Description:
 * @Date: 2021/4/24 22:46
 */
@Data
public class TransactionConfirmReq {
    @NotBlank
    private String transactionId;
    private String questionId;
    @NotBlank
    private String answer;
}
