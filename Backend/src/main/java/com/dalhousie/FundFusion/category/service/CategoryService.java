package com.dalhousie.FundFusion.category.service;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.category.requestEntity.CategoryRequest;
import com.dalhousie.FundFusion.category.responseEntity.CategoryResponse;

import java.util.List;


public interface CategoryService {

    Category getCategory(CategoryRequest request);

    List<CategoryResponse> getAllCategories(CategoryRequest request);

    CategoryResponse addCategory(CategoryRequest request);
}
