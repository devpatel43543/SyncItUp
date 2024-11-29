package com.dalhousie.FundFusion.category.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {

    private Integer categoryId;
    private String category;
    private boolean isDefault;
    /**
     * Checks if the category response contains valid data.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isInvalidCategory(category)) {
            throw new IllegalArgumentException("Category name cannot be null or empty.");
        }
        if (categoryId == null || categoryId < 0) {
            throw new IllegalArgumentException("Category ID must be a positive integer.");
        }
    }

    private boolean isInvalidCategory(String category) {
        return category == null || category.isBlank();
    }
}
