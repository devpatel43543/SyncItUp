package com.dalhousie.FundFusion.controller.category;

import com.dalhousie.FundFusion.category.controller.CategoryController;
import com.dalhousie.FundFusion.category.requestEntity.CategoryRequest;
import com.dalhousie.FundFusion.category.responseEntity.CategoryResponse;
import com.dalhousie.FundFusion.category.service.CategoryService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class CategoryControllerTest {

    @Test
    void testGetAllCategoryForUser_Success() {
        CategoryService mockService = Mockito.mock(CategoryService.class);

        List<CategoryResponse> mockCategories = List.of(
                new CategoryResponse(1, "Category 1", true),
                new CategoryResponse(2, "Category 2", false)
        );

        Mockito.when(mockService.getAllCategories()).thenReturn(mockCategories);

        CategoryController controller = new CategoryController(mockService);

        ResponseEntity<CustomResponseBody<List<CategoryResponse>>> response = controller.getAllCategoryForUser();

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("All categories fetched successfully", response.getBody().message());
        Assertions.assertEquals(2, response.getBody().data().size());

        CategoryResponse category1 = response.getBody().data().get(0);
        Assertions.assertEquals(1, category1.getCategoryId());
        Assertions.assertEquals("Category 1", category1.getCategory());
        Assertions.assertTrue(category1.isDefault());

        CategoryResponse category2 = response.getBody().data().get(1);
        Assertions.assertEquals(2, category2.getCategoryId());
        Assertions.assertEquals("Category 2", category2.getCategory());
        Assertions.assertFalse(category2.isDefault());
    }

    @Test
    void testGetAllCategoryForUser_Failure() {
        CategoryService mockService = Mockito.mock(CategoryService.class);

        Mockito.when(mockService.getAllCategories()).thenThrow(new RuntimeException("Unexpected error"));

        CategoryController controller = new CategoryController(mockService);

        ResponseEntity<CustomResponseBody<List<CategoryResponse>>> response = controller.getAllCategoryForUser();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testAddCategory_Success() {
        CategoryService mockService = Mockito.mock(CategoryService.class);

        CategoryResponse mockCategory = new CategoryResponse(1, "Category 1", true);

        Mockito.when(mockService.addCategory(Mockito.any(CategoryRequest.class)))
                .thenReturn(mockCategory);

        CategoryController controller = new CategoryController(mockService);

        CategoryRequest categoryRequest = new CategoryRequest();

        ResponseEntity<CustomResponseBody<CategoryResponse>> response = controller.addCategory(categoryRequest);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Category added successfully", response.getBody().message());
    }

    @Test
    void testAddCategory_Failure() {
        CategoryService mockService = Mockito.mock(CategoryService.class);

        Mockito.when(mockService.addCategory(Mockito.any(CategoryRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        CategoryController controller = new CategoryController(mockService);

        CategoryRequest categoryRequest = new CategoryRequest();

        ResponseEntity<CustomResponseBody<CategoryResponse>> response = controller.addCategory(categoryRequest);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testDeleteCategory_Success() {
        CategoryService mockService = Mockito.mock(CategoryService.class);

        Mockito.doNothing().when(mockService).deleteCategory(Mockito.anyInt());

        CategoryController controller = new CategoryController(mockService);

        ResponseEntity<CustomResponseBody<CategoryResponse>> response = controller.deleteCategory(1);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Category deleted successfully", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testDeleteCategory_Failure() {
        CategoryService mockService = Mockito.mock(CategoryService.class);

        Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(mockService)
                .deleteCategory(Mockito.anyInt());

        CategoryController controller = new CategoryController(mockService);

        ResponseEntity<CustomResponseBody<CategoryResponse>> response = controller.deleteCategory(1);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testAddCategory_DuplicateCategory() {

        CategoryService mockService = Mockito.mock(CategoryService.class);

        Mockito.when(mockService.addCategory(Mockito.any(CategoryRequest.class)))
                .thenThrow(new IllegalArgumentException("Category already exists"));

        CategoryController controller = new CategoryController(mockService);

        CategoryRequest request = new CategoryRequest();

        ResponseEntity<CustomResponseBody<CategoryResponse>> response = controller.addCategory(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testDeleteCategory_InvalidId() {

        CategoryService mockService = Mockito.mock(CategoryService.class);

        Mockito.doThrow(new IllegalArgumentException("Invalid category ID"))
                .when(mockService)
                .deleteCategory(Mockito.anyInt());

        CategoryController controller = new CategoryController(mockService);

        ResponseEntity<CustomResponseBody<CategoryResponse>> response = controller.deleteCategory(-1);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }
}
