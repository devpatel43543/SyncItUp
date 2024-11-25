package com.dalhousie.FundFusion.splitBill.repository;

import com.dalhousie.FundFusion.group.entity.Group;
import com.dalhousie.FundFusion.splitBill.entity.GroupPartnership;
import com.dalhousie.FundFusion.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupPartnershipRepository extends JpaRepository<GroupPartnership,Long> {
    Optional<GroupPartnership> findByGroupAndCreditorAndDebtor(Group group, User creditor, User debtor);

    List<GroupPartnership> findByGroup(Group group);

    List<GroupPartnership> findByGroupAndDebtor(Group group, User currentUser);

    List<GroupPartnership> findByGroupAndCreditor(Group group, User currentUser);
}
