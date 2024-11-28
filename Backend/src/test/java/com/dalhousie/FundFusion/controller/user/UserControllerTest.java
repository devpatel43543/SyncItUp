package com.dalhousie.FundFusion.controller.user;

import com.dalhousie.FundFusion.user.controller.UserController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    @Test
    void test_ReturnsTestString() {
        UserController userController = new UserController();

        String response = userController.test();

        assertEquals("test", response);
    }
}


