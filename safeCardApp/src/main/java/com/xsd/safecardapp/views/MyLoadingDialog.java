package com.xsd.safecardapp.views;

import com.hysd.usiapp.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用： 自定义Loading界面
 */
public class MyLoadingDialog extends Dialog {

	private static MyLoadingDialog myLoadingDialog = null;

	private static ImageView mImageView;
	private static TextView mTextView;

	public static MyLoadingDialog getMyLoadingDialog(Context context, int theme) {

		myLoadingDialog = new MyLoadingDialog(context, theme);
		myLoadingDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		myLoadingDialog.setContentView(R.layout.dialog_loading);
		mImageView = (ImageView) myLoadingDialog
				.findViewById(R.id.iv_loading_dialog);
		mTextView = (TextView) myLoadingDialog
				.findViewById(R.id.tv_loading_dialog);
		myLoadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		RotateAnimation aa = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		aa.setDuration(1500);
		aa.setStartOffset(-1);
		aa.setRepeatCount(Animation.INFINITE);
		aa.setRepeatMode(Animation.RESTART);
		mImageView.setAnimation(aa);

		return myLoadingDialog;
	}

	public MyLoadingDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// this.setCancelable(false);
	}

	public MyLoadingDialog(Context context, int theme) {
		super(context, theme);
		// this.setCancelable(false);
	}

	public MyLoadingDialog(Context context) {
		super(context);
		// this.setCancelable(false);
	}

	public void setMessage(String msg) {
		mTextView.setText(msg);
	}

	public void setImage(int img) {
		mImageView.setImageResource(img);
	}

	@Override
	public void show() {

		Window mWindow = getWindow();
		WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.dimAmount = 0.5f;
		mWindow.setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		super.show();

	}

}
