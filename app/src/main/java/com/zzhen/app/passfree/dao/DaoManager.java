package com.zzhen.app.passfree.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zzhen.app.passfree.R;

import org.greenrobot.greendao.database.Database;

/**
 * Created by zhangzhen on 2017/6/26.
 */

public class DaoManager {
    private DaoMaster mDaoMaster;
    private DaoMaster.DevOpenHelper mHelper;
    private DaoSession mDaoSession;
    private SQLiteDatabase db;
    //private Database db;
    private String mDbName;
    private Context mContext;

    private volatile static DaoManager manager;

    private DaoManager(Context context){
        this.mContext = context;
        this.mDbName = context.getString(R.string.db_name);
        setDatabase();
    }

    public static DaoManager getInstance(Context context){
        DaoManager instance = null;
        if(manager == null){
            synchronized (DaoManager.class){

                if (instance == null){
                    instance = new DaoManager(context);
                    manager = instance;
                }
            }
        }
        return manager;
    }

    private void setDatabase(){
        mHelper = new DaoMaster.DevOpenHelper(mContext, mDbName, null);
        db = mHelper.getWritableDatabase();
       // db = mHelper.getEncryptedReadableDb("dbpassword");
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getmDaoSession() {
        return mDaoSession;
    }
}
