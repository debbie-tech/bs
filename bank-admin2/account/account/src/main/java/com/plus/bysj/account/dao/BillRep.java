package com.plus.bysj.account.dao;

import com.plus.bysj.account.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @ProjectName: account
 * @Package: com.plus.bysj.account.dao
 * @ClassName: BillRep
 * @Author: rh
 * @Description:
 * @Date: 2021/4/24 17:09
 */
public interface BillRep extends JpaRepository<Bill,String> {
    Page<Bill> findAll(Specification<Bill> spec, Pageable pageable);

    @Modifying
    @Query("update Bill a set a.status=?2,a.mark=?4 where a.transactionId=?1 and a.userId=?3")
    void updateBillStatus(String id, String status, String userId, String mark);
}
