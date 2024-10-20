package com.dalhousie.FundFusion.user.service;

import com.dalhousie.FundFusion.user.entity.User;

public interface UserService {
    User getUser(Integer id);
    boolean checkValidUser(Integer id);
}
