package com.dalhousie.FundFusion.group.repository;
import com.dalhousie.FundFusion.group.entity.Group;
import com.dalhousie.FundFusion.group.entity.PendingGroupMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingGroupMembersRepository extends JpaRepository<PendingGroupMembers, Integer> {
    List<PendingGroupMembers> findByEmail(String email);
    PendingGroupMembers findByEmailAndGroup(String email, Group group);

}
