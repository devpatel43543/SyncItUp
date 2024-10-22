package com.dalhousie.FundFusion.category.repository;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {

    Optional<List<Category>> findByIsDefault(boolean isDefault);
    Optional<List<Category>> findByUser(User user);
    Optional<Category> findByCategoryIdAndUser(Integer id, User user);

}
