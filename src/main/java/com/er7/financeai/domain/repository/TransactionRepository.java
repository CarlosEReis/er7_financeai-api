package com.er7.financeai.domain.repository;

import com.er7.financeai.api.model.EstatisticsBalanceResponse;
import com.er7.financeai.domain.model.Transaction;
import com.er7.financeai.domain.repository.projection.TotalExpensePerCategory;
import com.er7.financeai.domain.repository.projection.TransactionBalance;
import com.er7.financeai.domain.repository.projection.TransactionsReportAi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByIdDesc(String userId);

    @Query("""
        select t.category.name as categoria, sum(t.amount) as soma from Transaction
        t where t.userId = :userId group by t.category.name order by soma desc limit :top""")
    List<TotalExpensePerCategory> totalExpensePerCategory(@Param("userId") String userId, @Param("top") int top);

    @Query("select t.type as type, sum(t.amount) as total from Transaction t where t.userId = :userId group by t.type")
    List<TransactionBalance> balance(@Param("userId") String userId);

    @Query("""
    select new com.er7.financeai.api.model.EstatisticsBalanceResponse(
        coalesce(sum(case when t.type = 'DEPOSIT' then t.amount else 0 end), 0),
        coalesce(sum(case when t.type = 'EXPENSE' then t.amount else 0 end), 0),
        coalesce(sum(case when t.type = 'INVESTMENT' then t.amount else 0 end), 0),
        coalesce(sum(case when t.type = 'DEPOSIT' then t.amount else 0 end), 0) -
        coalesce(sum(case when t.type = 'EXPENSE' then t.amount else 0 end), 0) -
        coalesce(sum(case when t.type = 'INVESTMENT' then t.amount else 0 end), 0) )
    from Transaction t 
    where t.userId = :userId
""")
    EstatisticsBalanceResponse balanceObject(@Param("userId") String userId);

    List<TransactionsReportAi> findByUserIdAndDateBetween(String userId, OffsetDateTime dateAfter, OffsetDateTime dateBefore);
}
