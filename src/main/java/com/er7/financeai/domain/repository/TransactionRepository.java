package com.er7.financeai.domain.repository;

import com.er7.financeai.api.model.EstatisticsBalanceResponse;
import com.er7.financeai.domain.model.Transaction;
import com.er7.financeai.domain.repository.projection.TotalExpensePerCategory;
import com.er7.financeai.domain.repository.projection.TransactionBalance;
import com.er7.financeai.domain.repository.projection.TransactionsReportAi;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

@Where(clause = "deleted = false")
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserSubOrderByIdDesc(String userId);

    List<Transaction> findByUserSub(String sub);

    @Query("""
        select t.category.name as categoria, sum(t.amount) as soma from Transaction
        t where t.user.id = :userId group by t.category.name order by soma desc limit :top""")
    List<TotalExpensePerCategory> totalExpensePerCategory(@Param("userId") Long userId, @Param("top") int top);

    @Query("select t.type as type, sum(t.amount) as total from Transaction t where t.user.id = :userId group by t.type")
    List<TransactionBalance> balance(@Param("userId") Long userId);

    @Query("""
    select new com.er7.financeai.api.model.EstatisticsBalanceResponse(
        coalesce(sum(case when t.type = 'DEPOSIT' then t.amount else 0 end), 0),
        coalesce(sum(case when t.type = 'EXPENSE' then t.amount else 0 end), 0),
        coalesce(sum(case when t.type = 'INVESTMENT' then t.amount else 0 end), 0),
        coalesce(sum(case when t.type = 'DEPOSIT' then t.amount else 0 end), 0) -
        coalesce(sum(case when t.type = 'EXPENSE' then t.amount else 0 end), 0) -
        coalesce(sum(case when t.type = 'INVESTMENT' then t.amount else 0 end), 0) )
    from Transaction t 
    where t.user.id = :userId
""")
    EstatisticsBalanceResponse balanceObject(@Param("userId") Long userId);

    List<TransactionsReportAi> findByUserSubAndDateBetween(String userId, OffsetDateTime dateAfter, OffsetDateTime dateBefore);

    @Query("SELECT t FROM Transaction t join fetch t.category join fetch t.paymentMethod WHERE t.group.id = :groupId")
    List<Transaction> findAllByGroupId(Long groupId);

    List<Transaction> findAllByUserSub(String userId);

    @Modifying
    @Query("UPDATE Transaction t SET t.group.id = :groupId WHERE t.user.id IN :userIds")
    void updateTrasactionsByGroupIdAndUsers(Long groupId, List<Long> userIds);


}
