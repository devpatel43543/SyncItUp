package com.dalhousie.FundFusion.service.category;

import com.dalhousie.FundFusion.category.entity.Category;
import com.dalhousie.FundFusion.category.repository.CategoryRepository;
import com.dalhousie.FundFusion.category.requestEntity.CategoryRequest;
import com.dalhousie.FundFusion.category.responseEntity.CategoryResponse;
import com.dalhousie.FundFusion.category.service.CategoryServiceImpl;
import com.dalhousie.FundFusion.exception.CategoryNotFoundException;
import com.dalhousie.FundFusion.group.entity.Group;
import com.dalhousie.FundFusion.group.entity.PendingGroupMembers;
import com.dalhousie.FundFusion.group.repository.PendingGroupMembersRepository;
import com.dalhousie.FundFusion.group.service.GroupService;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private GroupService groupService;

    @Mock
    private PendingGroupMembersRepository pendingRepo;

    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private static final int USER_CATEGORY_ID = 2;
    private static final int NEW_CATEGORY_ID = 3;

    private User mockUser;
    private Category defaultCategory;
    private Category userCategory;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1);

        defaultCategory = new Category();
        defaultCategory.setCategoryId(1);
        defaultCategory.setCategoryName("Default Category");
        defaultCategory.setDefault(true);

        userCategory = new Category();
        userCategory.setCategoryId(USER_CATEGORY_ID);
        userCategory.setCategoryName("User Category");
        userCategory.setDefault(false);
        userCategory.setUser(mockUser);

        categoryRequest = new CategoryRequest();
        categoryRequest.setCategoryId(1);
    }


    @Test
    void testGetCategory_SuccessWithDefaultCategory() {

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(categoryRepository.findByIsDefault(true)).thenReturn(Optional.of(List.of(defaultCategory)));

        Category result = categoryService.getCategory(categoryRequest);

        assertNotNull(result);
        assertEquals(defaultCategory.getCategoryId(), result.getCategoryId());
    }

    @Test
    void testGetCategory_SuccessWithUserCategory() {

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(categoryRepository.findByIsDefault(true)).thenReturn(Optional.of(List.of()));

        int categoryId = categoryRequest.getCategoryId();
        Optional<Category> userCategoryOptional = Optional.of(userCategory);

        when(categoryRepository.findByCategoryIdAndUser(categoryId, mockUser))
                .thenReturn(userCategoryOptional);

        Category result = categoryService.getCategory(categoryRequest);

        assertNotNull(result);
        assertEquals(USER_CATEGORY_ID, result.getCategoryId());
    }

    @Test
    void testGetCategory_NotFoundException() {

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(categoryRepository.findByIsDefault(true)).thenReturn(Optional.of(List.of()));
        when(categoryRepository.findByCategoryIdAndUser(categoryRequest.getCategoryId(), mockUser))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategory(categoryRequest));
    }

    @Test
    void testGetAllCategories_Success() {

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(categoryRepository.findByIsDefault(true)).thenReturn(Optional.of(List.of(defaultCategory)));
        when(categoryRepository.findByUser(mockUser)).thenReturn(Optional.of(List.of(userCategory)));

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertEquals(USER_CATEGORY_ID, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getCategoryId().equals(defaultCategory.getCategoryId())));
        assertTrue(result.stream().anyMatch(c -> c.getCategoryId().equals(userCategory.getCategoryId())));
    }

    @Test
    void testGetAllCategories_DefaultCategoriesNotFound() {

        when(categoryRepository.findByIsDefault(true)).thenReturn(Optional.empty());
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getAllCategories());
    }

    @Test
    void testGetAllCategories_UserCategoriesNotFound() {

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(categoryRepository.findByIsDefault(true)).thenReturn(Optional.of(List.of(defaultCategory)));
        when(categoryRepository.findByUser(mockUser)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getAllCategories());
    }

    @Test
    void testAddCategory_Success() {

        when(userService.getCurrentUser()).thenReturn(mockUser);
        Category newCategory = new Category();
        newCategory.setCategoryId(NEW_CATEGORY_ID);
        newCategory.setCategoryName("New Category");
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        CategoryRequest request = new CategoryRequest();
        request.setCategory("New Category");
        CategoryResponse response = categoryService.addCategory(request);

        assertNotNull(response);
        assertEquals("New Category", response.getCategory());
        assertEquals(NEW_CATEGORY_ID, response.getCategoryId());
    }

    @Test
    void testDeleteCategory_Success() {

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(categoryRepository.findByCategoryIdAndUser(USER_CATEGORY_ID, mockUser)).thenReturn(Optional.of(userCategory));

        categoryService.deleteCategory(USER_CATEGORY_ID);
        verify(categoryRepository, times(1)).delete(userCategory);
    }

    @Test
    void testDeleteCategory_NotFoundException() {

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(categoryRepository.findByCategoryIdAndUser(USER_CATEGORY_ID, mockUser)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(USER_CATEGORY_ID));
    }

    @Test
    void testRejectPendingMember_Success() {

        String authenticatedEmail = "user@example.com";
        Integer groupId = 1;

        Group mockGroup = new Group();
        mockGroup.setId(groupId);

        PendingGroupMembers mockPendingMember = PendingGroupMembers.builder()
                .id(1)
                .email(authenticatedEmail)
                .group(mockGroup)
                .build();

        lenient().doNothing().when(pendingRepo).delete(mockPendingMember);

        groupService.rejectPendingMember(groupId);
    }
}
