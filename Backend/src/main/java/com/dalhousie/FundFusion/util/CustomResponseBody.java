package com.dalhousie.FundFusion.util;
/**
 * CustomResponseBody is a generic response wrapper used for providing consistent API responses.
 * The `Result` enum provides predefined result types (SUCCESS or FAILURE) to indicate operation outcomes clearly.
 */
public record CustomResponseBody<T>(Result result, T data, String message) {

    /**
     * Enum `Result` represents the status of an API operation.
     * It ensures that all response statuses are standardized and type-safe.
     */
    public enum Result {
        SUCCESS,
        FAILURE,
    }
}
