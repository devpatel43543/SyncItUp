package com.dalhousie.FundFusion.category.service;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.category.repository.CategoryRepository;
import com.dalhousie.FundFusion.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    @Override
    public Category getCategory(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow( () -> new CategoryNotFoundException("Category not found with id: "+id
                ));
    }
}
