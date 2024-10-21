package com.dalhousie.FundFusion.exception;

import java.util.NoSuchElementException;

public class UserTransactionNotFoundException extends NoSuchElementException {

    public UserTransactionNotFoundException(String message){
        super(message);
    }
}
