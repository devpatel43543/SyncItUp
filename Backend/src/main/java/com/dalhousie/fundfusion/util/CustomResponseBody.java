package com.dalhousie.fundfusion.util;

public record CustomResponseBody<T>(Result result, T data, String message) {
    public enum Result {
        SUCCESS,
        FAILURE,
    }
}
