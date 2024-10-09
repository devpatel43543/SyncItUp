package com.dalhousie.FundFusion.user.controller;

import com.dalhousie.FundFusion.user.entity.UserTransaction;
import com.dalhousie.FundFusion.user.service.UserTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user/transaction")
@RequiredArgsConstructor
@RestController
public class UserTransactionController {

    private final UserTransactionService userTransactionService;

    @GetMapping("/test")
    public String test(){
        return "Hello";
    }

    @PostMapping("/logTransaction")
    public void logTransaction(@RequestBody UserTransaction userTransaction){
        userTransactionService.logTransaction(userTransaction);
    }

}
