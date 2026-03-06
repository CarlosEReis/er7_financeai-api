package com.er7.financeai.domain.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "group_members")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private User member;

    @Enumerated(EnumType.STRING)
    private Paper paper;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setMember(User member) {
        this.member = member;
    }

    public User getMember() {
        return member;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public MemberStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GroupMember that = (GroupMember) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}