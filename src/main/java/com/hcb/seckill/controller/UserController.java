package com.hcb.seckill.controller;

import com.alibaba.druid.util.StringUtils;
import com.hcb.seckill.controller.viewObject.UserView;
import com.hcb.seckill.error.BussinessException;
import com.hcb.seckill.error.EmBusinessError;
import com.hcb.seckill.response.CommonReturnType;
import com.hcb.seckill.service.UserService;
import com.hcb.seckill.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@RestController
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUserInfo(@RequestParam(name="id")Integer id) throws BussinessException {
        UserView userView = new UserView();
        UserModel userModel = userService.getUserByUserId(id);
        if(userModel == null){
            throw new BussinessException(EmBusinessError.USER_NOT_EXIST);
        }
        return CommonReturnType.create(convertFromUserModel(userModel,userView));
    }

    private UserView convertFromUserModel(UserModel userModel, UserView userView) {
        BeanUtils.copyProperties(userModel,userView);
        return userView;
    }

    //用户获取otp短信接口
    @RequestMapping(value="/getotp",method = {RequestMethod.POST},consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone) {
        //按照一定规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //将OTP验证同对应用户的手机号关联，使用HTTP session的方式绑定(redis非常适用）
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute(telphone, otpCode);
        //将OTP验证码通过短信通道发送给用户，省略
        System.out.println("telphone = " + telphone + "&optCode=" + otpCode);
        return CommonReturnType.create(null);
    }

    // 用户注册
    @RequestMapping(value="/register",method = {RequestMethod.POST},consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType userRegister(@RequestParam(name = "telphone") String telphone,
                                         @RequestParam(name = "otpCode") String otpCode,
                                         @RequestParam(name = "name") String name,
                                         @RequestParam(name = "gender") Integer gender,
                                         @RequestParam(name = "age") Integer age,
                                         @RequestParam(name = "password") String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        HttpSession httpSession = httpServletRequest.getSession();
        String sessionOtp = (String) httpSession.getAttribute(telphone);
        if(!StringUtils.equals(sessionOtp,otpCode)){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码错误");
        }

        UserModel userModel = new UserModel();
        userModel.setTelphone(telphone);
        userModel.setName(name);
        userModel.setGender(gender.byteValue());
        userModel.setAge(age);
        userModel.setEncrptPassword(enCodeByMD5(password));
        userModel.setRegisterMode("byphone");
        userService.register(userModel);
        return CommonReturnType.create(null);

    }
    private String enCodeByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        // 加密字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }

    // 用户登录
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name="telphone")String telphone,
                                  @RequestParam(name="password")String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //参数校验
        if(StringUtils.isEmpty(telphone)|| StringUtils.isEmpty(password)){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //用户登陆服务，用来校验用户登陆是否合法
        UserModel userModel = userService.validateLogin(telphone,enCodeByMD5(password));

        //将登陆凭证加入到用户登陆成功的session中
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        System.out.println(this.httpServletRequest.getSession().getAttribute("IS_LOGIN"));

        return CommonReturnType.create(null);
    }
}
