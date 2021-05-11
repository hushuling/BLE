package com.xiekang.bluetooths;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiekang.bluetooths.utlis.Cotexts;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.DeviceIdUtil;
import com.xiekang.bluetooths.utlis.MD5Util;
import com.xiekang.bluetooths.utlis.SharePrefrenceUtil;
import com.xiekang.bluetooths2.R;


/**
 * 注册激活界面
 */
public class RegisterActivity extends Dialog implements View.OnClickListener{
  private String register;
  private TextView textView1,textView2,textView3,textView4;
  private EditText edittext1,edittext2,edittext3,edittext4;
  private Activity activity;

  public RegisterActivity(@NonNull Activity context) {
    super(context);
    this.activity=context;
    LayoutInflater inflater = LayoutInflater.from(context);
    View viewDialog = inflater.inflate(R.layout.activity_register, null);
    setCanceledOnTouchOutside(false);
    setCancelable(false);
    setContentView(viewDialog);
    initView();
  }

  public void initView() {
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    findViewById(R.id.btn_sure).setOnClickListener(this);
    textView1= findViewById(R.id.textview1);
    textView2=findViewById(R.id.textview2);
    textView3=findViewById(R.id.textview3);
    textView4=findViewById(R.id.textview4);

    edittext1=findViewById(R.id.edittext1);
    edittext2= findViewById(R.id.edittext2);
    edittext3=findViewById(R.id.edittext3);
    edittext4=findViewById(R.id.edittext4);

    ed();
    init();
  }

  private void ed() {
    /**
     * 输入框接收完输入自动跳转
     */
    edittext1.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Do nothing
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() == 4) {
          edittext2.requestFocus();
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });
    edittext2.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (TextUtils.isEmpty(charSequence)) {
          edittext2.requestFocus();
        }
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() == 4) {
          edittext3.requestFocus();
        } else {
          edittext2.requestFocus();
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });
    edittext3.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (TextUtils.isEmpty(charSequence)) {
          edittext3.requestFocus();
        }
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() == 4) {
          edittext4.requestFocus();
        } else {
          edittext3.requestFocus();
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });
    edittext4.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (TextUtils.isEmpty(charSequence)) {
          edittext4.requestFocus();
        }
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });
  }

  private void doRegister() {
    String number = edittext1.getText().toString() + edittext2.getText().toString() + edittext3.getText().toString() + edittext4.getText().toString().trim();
    String sign = MD5Util.Md5(register + Cotexts.getRegiaterKey());
    if (sign.equals(number)) {
      SharePrefrenceUtil.saveParam(Cotexts.getSignValue(), number);
      dismiss();
    } else {
      dismiss();
    Toast.makeText(ContextProvider.get().getContext(),"注册码输入有误",Toast.LENGTH_LONG).show();
      activity.finish();
    }
  }

  protected void init() {

   String key= (String) SharePrefrenceUtil.getData(Cotexts.getSignKey(),"");
   if (!TextUtils.isEmpty(key)){
     register=key;
   }else {
     register = DeviceIdUtil.getDeviceId(ContextProvider.get().getContext());
   }
    if (register.length()>16){
      register=register.substring(0,16);
      SharePrefrenceUtil.saveParam(Cotexts.getSignKey(), register);
    }
    if (!TextUtils.isEmpty(register) && register.length() == 16) {
      textView1.setText(register.substring(0, 4));
      textView2.setText(register.substring(4, 8));
      textView3.setText(register.substring(8, 12));
      textView4.setText(register.substring(12, 16));
    } else {
      dismiss();
      activity.finish();
      Toast.makeText(ContextProvider.get().getContext(),"获取设备标识码异常",Toast.LENGTH_LONG).show();
    }
  }
  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btn_sure) {
      doRegister();
    }

  }
}
