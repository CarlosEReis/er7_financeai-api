package com.er7.financeai.domain.repository;

import com.er7.financeai.api.model.EstatisticsBalanceResponse;
import com.er7.financeai.domain.model.Transaction;
import com.er7.financeai.domain.repository.projection.TotalExpensePerCategory;
import com.er7.financeai.domain.repository.projection.TransactionBalance;
import com.er7.financeai.domain.repository.projection.TransactionListItem;
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

    @Query(value = """
        select
            t.id as id,
            t.name as name,
            u.name as registradoPor,
            u.picture as picture,
            pm.name as paymentMethodName,
            t.type as type,
            tc.name as categoria,
            g.name as grupo,
            t.amount as amount,
            t.created_at as createdAt,
            t.date_process as dateProcess
        from transactions t
        inner join transaction_category tc on tc.id = t.category_id
        inner join payment_method pm on pm.id = t.payment_method_id
        inner join groups g on g.id = t.group_id
        inner join group_members gu on gu.group_id = t.group_id
        inner join users u on u.id = t.user_id
        where gu.member_id = :userId
          and gu.status = 'ATIVO'
        order by g.name, t.created_at desc
    """, nativeQuery = true)
    List<TransactionListItem> findAllTransactionsOnUserGroupMemberIsActive(@Param("userId") Long userId);

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

    /**
     * Busca todas as transações que um determinado usuário (o allowedUser) tem permissão de leitura.
     * Esta query implementa a lógica do Modelo de Permissão Direcionada, fazendo um JOIN
     * com a tabela sharing_members (Permissões) sem precisar do group_id na transação.
     *
     * Regra: Traz todas as transações (t) cujo dono (t.user) concedeu permissão
     * para o usuário logado (allowedUserId).
     *
     * @param 'allowedUser' O ID do usuário logado (Carlos, Lara, etc.)
     * @return Lista de Transações visíveis.
     */
//    @Query("SELECT t FROM Transaction t " +
//            "JOIN SharingMember sm ON t.user = sm.targetUser " + // T.user_id = SM.target_user_id
//            "WHERE sm.allowedUser.id = :allowedUserId " +
//            "AND sm.read = TRUE") // Filtrar apenas por permissão de leitura concedida
//    List<Transaction> findAccessibleTransactionsByAllowedUserId(@Param("allowedUserId") Long allowedUserId);
}
