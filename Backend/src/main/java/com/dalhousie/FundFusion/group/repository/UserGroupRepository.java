package com.dalhousie.FundFusion.group.repository;

import com.dalhousie.FundFusion.group.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {

    Optional<UserGroup> findByUserIdAndGroupId(Integer memberId, Integer groupId);
    void delete(UserGroup userGroup);
    List<UserGroup> findByUserId(Integer userId);
    List<UserGroup> findByGroupId(Integer groupId);

    Optional<UserGroup> findByUserEmailAndGroupId(String email, Integer groupId);
}
