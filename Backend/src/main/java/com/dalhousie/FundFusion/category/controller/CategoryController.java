package com.dalhousie.FundFusion.category.controller;

import com.dalhousie.FundFusion.category.requestEntity.CategoryRequest;
import com.dalhousie.FundFusion.category.responseEntity.CategoryResponse;
import com.dalhousie.FundFusion.category.service.CategoryService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/getAllCategories")
    public ResponseEntity<CustomResponseBody<List<CategoryResponse>>> getAllCategoryForUser() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories();
            HttpStatus status = HttpStatus.CREATED;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "All categories fetched successfully";
            return buildResponse(status, result, categories, message);
        } catch (Exception e) {
            log.error("Unexpected error during transaction: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "Something went wrong";
            return buildResponse(status, result, null, message);
        }
    }

    @PostMapping("/addCategory")
    public ResponseEntity<CustomResponseBody<CategoryResponse>> addCategory(@RequestBody CategoryRequest request) {
        try {
            CategoryResponse category = categoryService.addCategory(request);
            HttpStatus status = HttpStatus.CREATED;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Category added successfully";
            return buildResponse(status, result, category, message);
        } catch (Exception e) {
            log.error("Unexpected error during transaction: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "Something went wrong";
            return buildResponse(status, result, null, message);
        }
    }

    @DeleteMapping("/deleteCategory")
    public ResponseEntity<CustomResponseBody<CategoryResponse>> deleteCategory(@RequestParam("id") Integer id) {
        try {
            categoryService.deleteCategory(id);
            HttpStatus status = HttpStatus.CREATED;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Category deleted successfully";
            return buildResponse(status, result, null, message);
        } catch (Exception e) {
            log.error("Unexpected error during transaction: {}", e.getMessage());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "Something went wrong";
            return buildResponse(status, result, null, message);
        }
    }

    // Helper method to build the response
    private <T> ResponseEntity<CustomResponseBody<T>> buildResponse(HttpStatus status,
                                                                    CustomResponseBody.Result result, T data, String message) {
        CustomResponseBody<T> responseBody = new CustomResponseBody<>(result, data, message);
        return ResponseEntity.status(status).body(responseBody);
    }
}
