package com.dalhousie.FundFusion.category.repository;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {

    List<Category> findByIsDefault(boolean isDefault);
    List<Category> findByUser(User user);
    Category findByCategoryIdAndUser(Integer id, User user);

}
