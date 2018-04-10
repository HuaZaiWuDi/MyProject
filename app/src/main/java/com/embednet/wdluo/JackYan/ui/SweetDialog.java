package com.embednet.wdluo.JackYan.ui;

import android.content.Context;
import android.os.Handler;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 项目名称：MyProject
 * 类描述：
 * 创建人：jack
 * 创建时间：2018/3/30
 */
public class SweetDialog extends SweetAlertDialog {

    private final Handler handler = new Handler();


    public SweetDialog(Context context) {
        this(context, NORMAL_TYPE);
    }

    public SweetDialog(Context context, int alertType) {
        super(context, alertType);
    }


    public SweetDialog setDaration(int timeOut) {
        handler.postDelayed(timeOutRunnable, timeOut);
        return this;
    }

    public SweetDialog setDialogDismissCallBack(DialogDismissCallBack dialogDismissCallBack) {
        this.dialogDismissCallBack = dialogDismissCallBack;
        return this;
    }

    public interface DialogDismissCallBack {
        void dismiss();
    }

    public DialogDismissCallBack dialogDismissCallBack;

    private Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if (isShowing()) {
                dismissWithAnimation();
                if (dialogDismissCallBack != null)
                    dialogDismissCallBack.dismiss();
            }
        }
    };


    @Override
    public void dismiss() {
        super.dismiss();
        if (dialogDismissCallBack != null)
            dialogDismissCallBack.dismiss();

    }


    @Override
    public void dismissWithAnimation() {
        super.dismissWithAnimation();
        if (dialogDismissCallBack != null)
            dialogDismissCallBack.dismiss();
    }
}
