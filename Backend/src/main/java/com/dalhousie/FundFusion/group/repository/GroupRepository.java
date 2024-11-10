package com.dalhousie.FundFusion.group.repository;


import com.dalhousie.FundFusion.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    Optional<Group> findByGroupName(String groupName);
    boolean existsByGroupName(String groupName);

}
