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
    public  ResponseEntity<CustomResponseBody<List<CategoryResponse>>> getAllCategoryForUser(@RequestBody CategoryRequest request){
        try{
            CustomResponseBody<List<CategoryResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    categoryService.getAllCategories(request),
                    "All categories fetched successfully"
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }
        catch (Exception e){
            log.error("Unexpected error during transaction: {}",e.getMessage());
            CustomResponseBody<List<CategoryResponse>> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    @PostMapping("/addCategory")
    public ResponseEntity<CustomResponseBody<CategoryResponse>> addCategory(@RequestBody CategoryRequest request){
        try{
            CustomResponseBody<CategoryResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    categoryService.addCategory(request),
                    "All categories fetched successfully"
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }
        catch (Exception e){
            log.error("Unexpected error during transaction: {}",e.getMessage());
            CustomResponseBody<CategoryResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

    @PostMapping("/deleteCategory")
    public ResponseEntity<CustomResponseBody<CategoryResponse>> deleteCategory(@RequestBody CategoryRequest request){
        try{
            CustomResponseBody<CategoryResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.SUCCESS,
                    categoryService.deleteCategory(request),
                    "All categories fetched successfully"
            );
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }
        catch (Exception e){
            log.error("Unexpected error during transaction: {}",e.getMessage());
            CustomResponseBody<CategoryResponse> responseBody = new CustomResponseBody<>(
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }

}
