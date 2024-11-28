package com.dalhousie.FundFusion.category.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {
    private Integer categoryId;
    private String category;

    /**
     * Validates the category request fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isInvalidCategory(category)) {
            throw new IllegalArgumentException("Category name is required.");
        }
    }

    private boolean isInvalidCategory(String category) {
        return category == null || category.isBlank();
    }
}
