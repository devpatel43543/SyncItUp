package com.dalhousie.FundFusion.user.service;

import com.dalhousie.FundFusion.exception.UserNotFoundException;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final UserRepository userRepository;


    @Override
    public User getUser(Integer id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with ID: "+id)
        );
    }

    @Override
    public boolean checkValidUser(Integer id){
        return getUser(id) != null;
    }
}
