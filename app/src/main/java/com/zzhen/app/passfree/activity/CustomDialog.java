package com.zzhen.app.passfree.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;


/**
 * Created by zhangzhen on 2017/6/27.
 */

public class CustomDialog {
    private static CustomDialog instance;
    private Context context;

    public static CustomDialog getInstance(Context context){
        if(instance == null){
            synchronized (CustomDialog.class){
                instance = new CustomDialog(context);
            }
        }
        return instance;
    }

    public CustomDialog(Context context) {

        this.context = context;
    }

    public void showCodeDialog(final String codepwd){
        AlertDialog codeDlg = null;
        AlertDialog.Builder builder = null;

        builder = new AlertDialog.Builder(context);
        codeDlg = builder
                .setTitle("Code Password:")
                .setMessage(codepwd)
                .setPositiveButton("Copy", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Copy Code", Toast.LENGTH_SHORT).show();
                        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("code", codepwd);
                        cm.setPrimaryClip(clipData);
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Close Code Dialog", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        if(!((Activity)context).isFinishing()) {
            codeDlg.show();
        }
    }
}
