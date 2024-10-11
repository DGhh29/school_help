package com.dg.schoolhelp.controller;

import com.alibaba.fastjson2.JSONObject;
import com.dg.schoolhelp.entity.User;
import com.dg.schoolhelp.service.IUserService;
import com.dg.schoolhelp.utils.RedisUtils;
import com.dg.schoolhelp.utils.RestBean;
import com.dg.schoolhelp.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 *  前端控制器
 *
 * @author DG
 * @since 2024-09-23
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private IUserService userService;

    //小程序开发者id
    @Value("${wxapp.AppID}")
    private String AppID;

    //小程序的密钥（重置密钥需要在yaml中修改）
    @Value("${wxapp.AppSecret}")
    private String AppSecret;

    //向微信服务器获取openid模块
    private String haveOpenid(String code){
        String userOpenid = null;

        //1、向微信服务器 使用登录凭证 code 获取 session_key 和 openid
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + AppID +
                "&secret=" + AppSecret +
                "&js_code=" + code +
                "&grant_type=authorization_code";

        // 创建 RestTemplate 对象
        RestTemplate restTemplate = new RestTemplate();
        //获取数据
        ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(res.getBody());
        if (jsonObject != null) {
            userOpenid = jsonObject.getString("openid");
        }
        return userOpenid;
    }

    //注册模块
    @PostMapping("/register")
    public RestBean register(User user, String code) {
        try {
            String userOpenid = haveOpenid(code);
            user.setUserOpenid(userOpenid);

            userService.SignUp(user);
            return RestBean.success("注册成功");
        } catch (Exception e) {
            return RestBean.failure(500);
        }
    }




    @GetMapping("/login")
    public RestBean login(String code,@RequestHeader("access_token") String accessToken,@RequestHeader("refresh_token") String refreshToken) {
        try {
            Map<String, String> tokenMap = tokenUtils.tokenMap();
//            String userOpenid = haveOpenid(code);
            User user = userService.Login("1234567890");
            if (user == null) {
                return RestBean.failure(401, "用户不存在");
            }
            tokenMap.put("userName", user.getUserName());
            tokenMap.put("userAvatar", user.getUserAvatar());

            redisUtils.set(tokenMap.get("access_token"), String.valueOf(user.getId()), 60 * 60 * 24);
            redisUtils.set(tokenMap.get("refresh_token"), tokenMap.get("access_token"), 60 * 60 * 24 * 7);

            return RestBean.success(tokenMap);
        } catch (Exception e) {
            return RestBean.failure(500);
        }

    }
}
