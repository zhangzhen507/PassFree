package com.zzhen.app.passfree.services;

import android.content.Context;
import android.util.Log;

import com.zzhen.app.passfree.dao.CodeDao;
import com.zzhen.app.passfree.dao.DaoManager;
import com.zzhen.app.passfree.entity.Code;
import com.zzhen.app.passfree.entity.User;
import com.zzhen.app.passfree.util.Constant;
import com.zzhen.app.passfree.util.EncryptDecrypt;

import java.util.List;

/**
 * Created by zhangzhen on 2017/6/27.
 */

public class CodeServices {
    private static final String TAG = "CodeServices";

    private CodeDao codeDao;
    private UserServices userServices;
    private Context context;

    private volatile static CodeServices instance;

    public static CodeServices getInstance(Context context){
        if(instance == null){
            synchronized (CodeServices.class){
                instance = new CodeServices(context);
            }
        }
        return instance;
    }

    private CodeServices(Context context) {
        this.context = context;
        this.codeDao = DaoManager.getInstance(context).getmDaoSession().getCodeDao();
        this.userServices = UserServices.getInstance(context);
    }

    public int addCode(String codeName, String codePassword, Long userId){

        if(null == codeName || codeName.equals("") || null == codePassword || codePassword.equals("")){
            Log.e(TAG, "codeName is null or codePassword is null");
            return Constant.PARAM_NULL;
        }

        User user = userServices.getUserById(userId);
        if(null == user){
            Log.e(TAG, "can not get user by userid");
            return Constant.HAVE_NO_USER;
        }
        String decryptCode = EncryptDecrypt.encode(codePassword);
        Code code = new Code();
        code.setUserId(userId);
        code.setName(EncryptDecrypt.encode(codeName));
        code.setPassword(decryptCode);
        code.setUser(user);
        long id = this.codeDao.insert(code);
        if(id <= 0){
            Log.e(TAG, "add code failed");
            return Constant.INSERT_DATA_FAILED;
        }
        Log.v(TAG, "add code success");
        return Constant.ADD_CODE_SUCCESS;
    }

    public int updateCode(Long id, String codeName, String codePassword, Long userId){
        if(null == codeName || codeName.equals("") || null == codePassword || codePassword.equals("")){
            Log.e(TAG, "codeName is null or codePassword is null");
            return Constant.PARAM_NULL;
        }

        User user = userServices.getUserById(userId);
        if(null == user){
            Log.e(TAG, "can not get user by userid");
            return Constant.HAVE_NO_USER;
        }

        Code code = getCodeById(id);

        if(null == code){
            return Constant.NOT_EXIST;
        }

        code.setName(EncryptDecrypt.encode(codeName));
        String decryptCode = EncryptDecrypt.encode(codePassword);
        code.setPassword(decryptCode);
        code.setUserId(userId);
        code.setUser(userServices.getUserById(userId));

        this.codeDao.update(code);

        return Constant.UPDATE_CODE_SUCCESS;
    }

    public List<Code> getCodeList(){
        List<Code> codeList = this.codeDao.queryBuilder().list();
        return codeList;
    }

    public Code getCodeById(Long id){

        List<Code> codeList = this.codeDao.queryBuilder().where(CodeDao.Properties.Id.eq(id)).list();
        if(null == codeList || codeList.size() <= 0 || codeList.size() > 1){
            return null;
        }

        return codeList.get(0);
    }

    public void deleteCode(Code code){
        this.codeDao.delete(code);
    }

    public void deleteCode(Long id){
        this.codeDao.deleteByKey(id);
    }
}
