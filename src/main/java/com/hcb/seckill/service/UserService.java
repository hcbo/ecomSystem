package com.hcb.seckill.service;

import com.hcb.seckill.error.BussinessException;
import com.hcb.seckill.service.model.UserModel;

public interface UserService {
    public UserModel getUserByUserId(Integer id);
    public void register(UserModel userModel) throws BussinessException;

    public UserModel validateLogin(String telphong, String enCodeByMD5) throws BussinessException;
}
