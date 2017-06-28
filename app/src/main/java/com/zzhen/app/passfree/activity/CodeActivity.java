package com.zzhen.app.passfree.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.zzhen.app.passfree.R;
import com.zzhen.app.passfree.entity.Code;
import com.zzhen.app.passfree.services.CodeServices;

import com.zzhen.app.passfree.util.EncryptDecrypt;

import java.util.List;

public class CodeActivity extends AppCompatActivity {

    private final String TAG = "CodeActivity";


    private final int ADD_CODE = 1;


    private Long userId;

    private ListView listView;
    private CommonTypeAdapter<Code> codeAdapter;

    private AddCodeDialog.Builder addDlgbuilder;
    private UpdateCodeDialog.Builder updateDlgBuilder;

    private CodeServices codeServices;

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userId = bundle.getLong(getString(R.string.USER_ID));


        codeServices = CodeServices.getInstance(CodeActivity.this);

        listView = (ListView)findViewById(R.id.lv_codelist);
        addDlgbuilder = new AddCodeDialog.Builder(CodeActivity.this);
        addDlgbuilder.setUserid(userId);
        addDlgbuilder.setTitle("Add A New Code");

        updateDlgBuilder = new UpdateCodeDialog.Builder(CodeActivity.this);
        updateDlgBuilder.setUserid(userId);
        updateDlgBuilder.setTitle("Update Code");

        codeAdapter = new CommonTypeAdapter<Code>(R.layout.codeitem) {
            @Override
            public void bindView(ViewHolder holder, Code obj) {
                holder.setText(R.id.tv_code_item, EncryptDecrypt.decode(obj.getName()));
            }
        };
        listView.setAdapter(codeAdapter);

        List<Code> codeList = codeServices.getCodeList();
        updateCodeList(codeList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Code code = codeAdapter.getItem(position);

                PopupMenu popupMenu = new PopupMenu(CodeActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popupmenu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.v(TAG, "popup menu is clicked");

                        switch (item.getItemId()){
                            case R.id.item_pwd:
                                showPassword(code);
                                break;
                            case R.id.item_update:
                                updateCode(code);
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Code code = codeAdapter.getItem(position);

                showDeleteCodeDialog(codeServices, code);

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        menu.add(1, ADD_CODE, 1, "Add code");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        switch (id){
            case ADD_CODE:
                Toast.makeText(CodeActivity.this, "Add Code", Toast.LENGTH_SHORT).show();
                addCode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addCode(){

        addDlgbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Add a new code OK button clicked");
                dialog.dismiss();

                List<Code> codeList = codeServices.getCodeList();
                updateCodeList(codeList);
            }
        });
        addDlgbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Add a new code Cancel button clicked");
                dialog.dismiss();
            }
        });
        addDlgbuilder.create().show();
        return;
    }

    private void updateCodeList(List<Code> codeList){
        for(Code code : codeList){
            if(!codeAdapter.isDataExist(code)){
                codeAdapter.add(code);
            }
        }
    }

    private void showPassword(Code code){
        String codePwd = code.getPassword();
        String codePwdDecrypt = EncryptDecrypt.decode(codePwd);
        CustomDialog.getInstance(CodeActivity.this).showCodeDialog(codePwdDecrypt);
    }

    private void updateCode(Code code){
        updateDlgBuilder.setCode(code);

        updateDlgBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Update code OK button clicked");
                dialog.dismiss();
                List<Code> codeList = codeServices.getCodeList();
                updateCodeList(codeList);
            }
        });

        updateDlgBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Update code Cancel button clicked");
                dialog.dismiss();
            }
        });
        updateDlgBuilder.create().show();
        return;
    }

    private void showDeleteCodeDialog(final CodeServices codeServices, final Code code){
        AlertDialog codeDelDlg = null;
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(CodeActivity.this);
        codeDelDlg = builder
                .setTitle("Delete Code")
                .setMessage("Are you sure to delete code "+code.getName()+" ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        codeServices.deleteCode(code);
                        Toast.makeText(CodeActivity.this, "Delete code "+code.getName(), Toast.LENGTH_SHORT).show();
                        codeAdapter.remove(code);
                        List<Code> codeList = codeServices.getCodeList();
                        updateCodeList(codeList);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CodeActivity.this, "Not delete code", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        if(!isFinishing()) {
            codeDelDlg.show();
        }
    }
}
