package com.zzhen.app.passfree.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzhen.app.passfree.R;

import com.zzhen.app.passfree.services.CodeServices;
import com.zzhen.app.passfree.util.AutoGenCode;
import com.zzhen.app.passfree.util.Constant;


/**
 * Created by zhangzhen on 2017/6/13.
 */

public class AddCodeDialog extends Dialog{

    private final String TAG = "AddCodeDialog";

    private Context mContext;
    private static AddCodeDialog instance;

    public AddCodeDialog(Context context, int theme) {
        super(context, theme);
    }


    public AddCodeDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public static AddCodeDialog getInstance(Context context){
        if(instance == null){
            synchronized (AddCodeDialog.class){
                instance = new AddCodeDialog(context);
            }
        }
        return instance;
    }

    public static class Builder{
        private String TAG = "AddCodeDialog.Builder";
        private Context context;
        private String title;

        private String positiveButtonText;
        private String negativeButtonText;

        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        private CodeServices codeServices;

        private Long userid;


        public Builder(Context context){
            this.context = context;
            this.codeServices = CodeServices.getInstance(context);
        }

        public void setUserid(Long userid) {
            this.userid = userid;
        }

        public Builder setTitle(int title){
            this.title = (String)context.getText(title);
            return this;
        }

        public Builder setTitle(String title){
            this.title = title;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public int addNewCode(View layout){
            Log.v(TAG, "add a new code: ");

            EditText codeName = (EditText)layout.findViewById(R.id.dlg_editText_codeName);
            EditText codePwd = (EditText)layout.findViewById(R.id.dlg_editText_codePwd);

            if(null == codeName || codeName.getText().toString().equals("") || codePwd == null || codePwd.getText().toString().equals("")){
                Toast.makeText(context, "Code name and password can not be null", Toast.LENGTH_SHORT).show();
                return Constant.PARAM_NULL;
            }

            String codeNameStr = codeName.getText().toString();
            String codePwdStr = codePwd.getText().toString();

            return this.codeServices.addCode(codeNameStr, codePwdStr, userid);
        }

        public AddCodeDialog create(){

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final AddCodeDialog dialog = new AddCodeDialog(context,R.style.Dialog);

            final View layout = inflater.inflate(R.layout.add_code_dlg, null);

            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            // set dialog title
            ((TextView)layout.findViewById(R.id.dlg_txt_title)).setText(title);

            ((Button)layout.findViewById(R.id.dlg_btn_autoCreate)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String autoPwd = AutoGenCode.getInstance().genCode(10);

                    EditText etPwd = (EditText)layout.findViewById(R.id.dlg_editText_codePwd);
                    etPwd.setText(autoPwd);
                }
            });

            // set the confirm button
            if(positiveButtonText != null){
                ((Button)layout.findViewById(R.id.dlg_btn_positive)).setText(positiveButtonText);
                if(positiveButtonClickListener != null){
                    ((Button)layout.findViewById(R.id.dlg_btn_positive)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v(TAG, "Positive Button Clicked");

                            //add a new device
                            int ret = addNewCode(layout);

                            switch (ret){
                                case Constant.PARAM_NULL:
                                    Log.e(TAG, "param is null");
                                    Toast.makeText(context, "please input code info", Toast.LENGTH_SHORT).show();

                                    break;
                                case Constant.HAVE_NO_USER:
                                    Log.e(TAG, "user is not bind to this code");
                                    Toast.makeText(context, "user is not bind to this code", Toast.LENGTH_SHORT).show();
                                    break;
                                case Constant.INSERT_DATA_FAILED:
                                    Log.e(TAG, "insert code data failed");
                                    Toast.makeText(context, "insert code data failed", Toast.LENGTH_SHORT).show();
                                    break;
                                case Constant.ADD_CODE_SUCCESS:
                                    Log.v(TAG, "add code success");
                                    Toast.makeText(context, "Add code success", Toast.LENGTH_SHORT).show();
                                    break;
                            }

                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            }else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.dlg_btn_positive).setVisibility(View.GONE);
            }
            //set the cancel button
            if(negativeButtonText != null){
                ((Button)layout.findViewById(R.id.dlg_btn_cancel)).setText(negativeButtonText);
                if(negativeButtonClickListener != null){
                    ((Button)layout.findViewById(R.id.dlg_btn_cancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v(TAG, "Negative Button Clicked");

                            negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            }else{
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.dlg_btn_cancel).setVisibility(View.GONE);
            }

            dialog.setContentView(layout);
            return dialog;
        }
    }
}
