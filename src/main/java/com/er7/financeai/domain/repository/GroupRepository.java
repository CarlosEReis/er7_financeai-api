package com.er7.financeai.domain.repository;

import com.er7.financeai.domain.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Integer> {

    List<Group> findAllByOwnerId(Long userId);

    @Query(value = """
        SELECT
            g.id,
            g.name
        FROM groups g
        LEFT JOIN group_members gm on g.id = gm.group_id
        LEFT JOIN users u on gm.member_id = u.id
        WHERE u.sub = :userSub
    """, nativeQuery = true)
    List<com.er7.financeai.domain.repository.projection.Group> findAllByMembers(@Param("userSub") String userSub);
}
