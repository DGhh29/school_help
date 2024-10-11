package com.dg.schoolhelp;

import com.dg.schoolhelp.utils.TokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class SchoolHelpApplicationTests {

    @Autowired
    private TokenUtils tokenUtils;

    @Test
    void contextLoads() {
        Map<String, String> tokenMap = tokenUtils.tokenMap();
        System.out.println(tokenMap.get("access_token"));
        System.out.println(tokenMap.get("refresh_token"));
    }

}
