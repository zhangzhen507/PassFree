package com.zzhen.app.passfree.services;

import android.content.Context;
import android.util.Log;

import com.zzhen.app.passfree.dao.DaoManager;
import com.zzhen.app.passfree.dao.UserDao;
import com.zzhen.app.passfree.entity.User;
import com.zzhen.app.passfree.util.Constant;
import com.zzhen.app.passfree.util.EncryptDecrypt;

import java.util.List;

/**
 * Created by zhangzhen on 2017/6/26.
 */

public class UserServices {

    private static final String TAG = "UserServices";

    private UserDao userDao;
    private Context context;

    private volatile static UserServices instance;

    public static UserServices getInstance(Context context){
        if(instance == null){
            synchronized (UserServices.class){
                instance = new UserServices(context);
            }
        }
        return instance;
    }

    private UserServices(Context context) {
        this.userDao = DaoManager.getInstance(context).getmDaoSession().getUserDao();
        this.context = context;
    }


    private boolean isUserRegistered(String username){
        String encryptUsername = EncryptDecrypt.encode(username);
        User user = userDao.queryBuilder().where(UserDao.Properties.Name.eq(encryptUsername)).unique();
        if(null == user){
            return false;
        }
        if(user.getName().equals(encryptUsername)){
            return true;
        }
        return false;
    }

    private String getPasswordByUsername(String username){
        String encryptUsername = EncryptDecrypt.encode(username);
        List<User> userList = userDao.queryBuilder().where(UserDao.Properties.Name.eq(encryptUsername)).list();

        if(userList == null || userList.size() > 1 || userList.size() <= 0){
            return null;
        }

        String password = userList.get(0).getPassword();

        return password;
    }

    public Long getUserIDByUserName(String username){
        Long id = 0L;

        if(null == username || username.equals("")){
            Log.e(TAG, "username is null");
            return 0L;
        }
        String encryptUsername = EncryptDecrypt.encode(username);
        List<User> userList = userDao.queryBuilder().where(UserDao.Properties.Name.eq(encryptUsername)).list();

        if(userList.size() > 1 || userList.size() <= 0){
            Log.e(TAG, "username is not unique");
            return 0L;
        }

        id = userList.get(0).getId();

        return id;
    }

    public int login(String username, String password){

        if(null == username || username.equals("") || null == password || password.equals("")){
            return Constant.PARAM_NULL;
        }
        if(!isUserRegistered(username)){
            return Constant.LOGIN_FAILED_NOT_REGISTER;
        }

        String encryptPassword = getPasswordByUsername(username);
        String decryptPassword = EncryptDecrypt.decode(encryptPassword);

        Log.v(TAG, "password = " + password);
        Log.v(TAG, "decryptPassword = " + decryptPassword);
        if(!password.equals(decryptPassword)){
            Log.e(TAG, "password is not equal with decrypted password");
            return Constant.WRONG_PASSWORD;
        }else {
            Log.v(TAG, "Login successfull");
            return Constant.LOGIN_SUCCESS;
        }
    }

    public int register(String username, String password){
        if(null == username || username.equals("") || null == password || password.equals("")){
            Log.e(TAG, "username is null or password is null");
            return Constant.PARAM_NULL;
        }

        if(isUserRegistered(username)){
            Log.v(TAG, username + "has been registered");
            return Constant.HAS_BEEN_REGISTERED;
        }

        String encryptPassword = EncryptDecrypt.encode(password);
        User user = new User();
        user.setName(EncryptDecrypt.encode(username));
        user.setPassword(encryptPassword);
        long id = userDao.insert(user);
        if(id > 0){
            return Constant.REGISTER_SUCCESS;
        }else {
            return Constant.INSERT_DATA_FAILED;
        }
    }

    public User getUserById(Long id){

        List<User> userList = this.userDao.queryBuilder().where(UserDao.Properties.Id.eq(id)).list();
        if(null == userList || userList.size() > 1 || userList.size() <= 0){
            Log.e(TAG, "username is not unique");
            return null;
        }

        User user = userList.get(0);
        return user;
    }
}
