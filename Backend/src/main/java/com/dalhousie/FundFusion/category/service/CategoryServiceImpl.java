package com.dalhousie.FundFusion.category.service;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.category.repository.CategoryRepository;
import com.dalhousie.FundFusion.category.requestEntity.CategoryRequest;
import com.dalhousie.FundFusion.category.responseEntity.CategoryResponse;
import com.dalhousie.FundFusion.exception.CategoryNotFoundException;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Override
    public Category getCategory(CategoryRequest request) {

        User activeUser = userService.getUser(request.getUserId());
        return categoryRepository.findByIsDefault(true)
                .get()
                .stream()
                .filter(category -> category.getCategoryId() == request.getCategoryId())
                .findFirst()
                .orElseGet(
                        () -> categoryRepository.findByCategoryIdAndUser(request.getCategoryId(), activeUser)
                                .orElseThrow(
                                        ()-> new CategoryNotFoundException("Category not found: "+ request.getCategoryId())
                                )
                );
    }

    @Override
    public List<CategoryResponse> getAllCategories(CategoryRequest request) {

        //Get All default categories
        List<Category> defaultCategories = categoryRepository.findByIsDefault(true)
                .orElseThrow(
                        ()-> new CategoryNotFoundException("Category not found: "+ request.getCategoryId())
                );
        //Get user specific categories
        User user = userService.getUser(request.getUserId());
        List<Category> userDefinedCategories = categoryRepository.findByUser(user)
                .orElseThrow(
                        ()-> new CategoryNotFoundException("Category not found: "+ request.getCategoryId())
                );;

        //Combining all categories
        List<Category> allCategories = new ArrayList<>(defaultCategories);
        allCategories.addAll(userDefinedCategories);

        return allCategories
                .stream()
                .map(category ->
                    CategoryResponse.builder()
                        .categoryId(category.getCategoryId())
                        .category(category.getCategoryName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse addCategory(CategoryRequest request) {

        User activeUser = userService.getUser(request.getUserId());

        Category categoryToBeAdded = Category.builder()
                        .categoryName(request.getCategory())
                        .user(activeUser)
                        .isDefault(false)
                .build();

        Category categoryAdded = categoryRepository.save(categoryToBeAdded);

        return CategoryResponse.builder()
                .category(categoryAdded.getCategoryName())
                .categoryId(categoryAdded.getCategoryId())
                .build();
    }

    @Override
    public CategoryResponse deleteCategory(CategoryRequest request) {

        Category category = categoryRepository.findByCategoryIdAndUser(
                request.getCategoryId(),
                userService.getUser(
                        request.getUserId()
                )
            )
                .orElseThrow(
                        ()-> new CategoryNotFoundException("Category not found: "+ request.getCategoryId())
                );;

        categoryRepository.delete(category);

        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .category(category.getCategoryName())
                .build();
    }

}
