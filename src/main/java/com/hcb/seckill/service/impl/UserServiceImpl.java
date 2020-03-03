package com.hcb.seckill.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.hcb.seckill.dao.UserDOMapper;
import com.hcb.seckill.dao.UserPasswordDOMapper;
import com.hcb.seckill.dataobject.UserDO;
import com.hcb.seckill.dataobject.UserPasswordDO;
import com.hcb.seckill.error.BussinessException;
import com.hcb.seckill.error.EmBusinessError;
import com.hcb.seckill.service.UserService;
import com.hcb.seckill.service.model.UserModel;
import com.hcb.seckill.validator.ValidationResult;
import com.hcb.seckill.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;
    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel validateLogin(String telphone, String enCodeByMD5) throws BussinessException {
        //通过用户的手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if(userDO == null) {
            throw new BussinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO,userPasswordDO);
        //拿到用户信息内加密的密码是否和传输的是否相匹配
        if(!StringUtils.equals(enCodeByMD5,userModel.getEncrptPassword())){
            throw new BussinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;

    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);

        if(userPasswordDO!=null){
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }

    @Override
    public UserModel getUserByUserId(Integer id) {
         UserDO userDO = userDOMapper.selectByPrimaryKey(id);
         if(userDO == null){
             return null;
         }
         UserModel userModel = new UserModel();
         BeanUtils.copyProperties(userDO,userModel);

         UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(id);
         if(userPasswordDO!=null){
             userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
         }
         return userModel;
    }


    @Transactional
    @Override
    public void register(UserModel userModel) throws BussinessException {
        if(userModel == null){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        ValidationResult result = validator.validate(userModel);
        System.out.println(result.getErrMsg()+"result");
        if (result.isHasErrors()) {
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        UserDO userDO = convertFromModel(userModel);
        userDOMapper.insertSelective(userDO);
        userModel.setId(userDO.getId());
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
        System.out.println("ok");
    }
    private UserPasswordDO convertPasswordFromModel(UserModel userModel){
        if(userModel == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }
    private UserDO convertFromModel(UserModel userModel){
        if(userModel == null) {
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }
}
