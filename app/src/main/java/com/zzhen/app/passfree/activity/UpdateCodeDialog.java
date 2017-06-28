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
import com.zzhen.app.passfree.entity.Code;
import com.zzhen.app.passfree.services.CodeServices;
import com.zzhen.app.passfree.util.AutoGenCode;
import com.zzhen.app.passfree.util.Constant;
import com.zzhen.app.passfree.util.EncryptDecrypt;

/**
 * Created by zhangzhen on 2017/6/27.
 */

public class UpdateCodeDialog extends Dialog {
    private final String TAG = "UpdateCodeDialog";

    private Context mContext;
    private static UpdateCodeDialog instance;

    public UpdateCodeDialog(Context context, int theme) {
        super(context, theme);
    }


    public UpdateCodeDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public static UpdateCodeDialog getInstance(Context context){
        if(instance == null){
            synchronized (UpdateCodeDialog.class){
                instance = new UpdateCodeDialog(context);
            }
        }
        return instance;
    }

    public static class Builder{
        private String TAG = "UpdateCodeDialog.Builder";
        private Context context;
        private String title;

        private String positiveButtonText;
        private String negativeButtonText;

        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        private CodeServices codeServices;

        private Long userid;

        private Code code;


        public Builder(Context context){
            this.context = context;

            this.codeServices = CodeServices.getInstance(context);
        }

        public void setCode(Code code) {
            this.code = code;
        }

        public void setUserid(Long userid) {
            this.userid = userid;
        }

        public UpdateCodeDialog.Builder setTitle(int title){
            this.title = (String)context.getText(title);
            return this;
        }

        public UpdateCodeDialog.Builder setTitle(String title){
            this.title = title;
            return this;
        }

        public UpdateCodeDialog.Builder setPositiveButton(int positiveButtonText,
                                                       OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public UpdateCodeDialog.Builder setPositiveButton(String positiveButtonText,
                                                       OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public UpdateCodeDialog.Builder setNegativeButton(int negativeButtonText,
                                                       OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public UpdateCodeDialog.Builder setNegativeButton(String negativeButtonText,
                                                       OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public int updateCode(View layout){
            Log.v(TAG, "add a new code: ");

            EditText codeName = (EditText)layout.findViewById(R.id.dlg_editText_codeName);
            EditText codePwd = (EditText)layout.findViewById(R.id.dlg_editText_codePwd);

            if(null == codeName || codeName.getText().toString().equals("") || codePwd == null || codePwd.getText().toString().equals("")){
                Toast.makeText(context, "Code name and password can not be null", Toast.LENGTH_SHORT).show();
                return Constant.PARAM_NULL;
            }

            String codeNameStr = codeName.getText().toString();
            String codePwdStr = codePwd.getText().toString();

            return this.codeServices.updateCode(this.code.getId(), codeNameStr, codePwdStr, userid);
        }

        public UpdateCodeDialog create(){

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final UpdateCodeDialog dialog = new UpdateCodeDialog(context,R.style.Dialog);

            final View layout = inflater.inflate(R.layout.add_code_dlg, null);

            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            // set dialog title
            ((TextView)layout.findViewById(R.id.dlg_txt_title)).setText(title);

            ((EditText)layout.findViewById(R.id.dlg_editText_codeName)).setText(code.getName());
            ((EditText)layout.findViewById(R.id.dlg_editText_codePwd)).setText(EncryptDecrypt.decode(code.getPassword()));

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

                            //update a new device
                            int ret = updateCode(layout);

                            switch (ret){
                                case Constant.PARAM_NULL:
                                    Log.e(TAG, "param is null");
                                    Toast.makeText(context, "please input code info", Toast.LENGTH_SHORT).show();

                                    break;
                                case Constant.HAVE_NO_USER:
                                    Log.e(TAG, "user is not bind to this code");
                                    Toast.makeText(context, "user is not bind to this code", Toast.LENGTH_SHORT).show();
                                    break;
                                case Constant.NOT_EXIST:
                                    Log.e(TAG, "Code data is not exist");
                                    Toast.makeText(context, "Code data is not exist", Toast.LENGTH_SHORT).show();
                                    break;
                                case Constant.ADD_CODE_SUCCESS:
                                    Log.v(TAG, "Update code success");
                                    Toast.makeText(context, "Update code success", Toast.LENGTH_SHORT).show();
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
