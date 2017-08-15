package com.xsd.safecardapp.activity;

import java.util.List;

import com.xsd.safecardapp.App;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.utils.LockPatternUtils;
import com.xsd.safecardapp.views.LockPatternView;
import com.xsd.safecardapp.views.LockPatternView.Cell;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 重置手势解锁
 * @author OnlyWay
 *
 */
public class ResetGestureActivity extends Activity {

	public static final int CANCEL_OK = 80; // 取消成功返回码

	private LockPatternView mLockPatternView; // 手势视图

	private int mFailedPatternAttemptsSinceLastTimeout = 0; // 允许出错的次数
	private CountDownTimer mCountdownTimer = null; // 定时器
	private Handler mHandler = new Handler(); // 负责处理次数超了之后，禁止秒数倒数的处理器
	private TextView mHeadTextView; // 错误提示

	private Toast mToast;

	private TextView textView;

	/**
	 * 吐司提示 2015/1/19 15:27
	 * 
	 * @param message
	 */
	private void showToast(CharSequence message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(message);
		}

		mToast.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_gesture);

		// 手势视图
		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_unlock_lockview);

		textView = (TextView) this.findViewById(R.id.gesturepwd_unlock_forget);

		// 设置图案解锁的监听器
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);

		// 设置视图是否会使用触觉反馈，PS:每设置一个点，手机会震动一下
		mLockPatternView.setTactileFeedbackEnabled(true);

		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}

	/**
	 * 清除图案 2015/1/19 15:31
	 */
	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern(); // 清除图案
		}
	};

	/**
	 * 图案解锁的监听器 2015/1/19 15:31
	 */
	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		/**
		 * 检测到用户开始输入图案 2015/1/19 15:31
		 */
		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		/**
		 * 图案已经被清除 2015/1/19 15:32
		 */
		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		/**
		 * 检测到用户输入的图案 2015/1/19 15:33
		 * 
		 * @param pattern
		 */
		public void onPatternDetected(List<Cell> pattern) {
			if (pattern == null)
				return;

			// 检查用户此次输入的图案与之前设置的图案是否一致
			if (App.getInstance().getLockPatternUtils().checkPattern(pattern)) {

				// 设置显示的模式为正确模式
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Correct);

				// // 打开界面
				// Intent intent = getIntent();
				// Bundle b = new Bundle();
				// intent.putExtra("data", b);
				//
				// setResult(UnlockGestureActivity.RESULT_OK, intent); // 设置返回结果
				// finish(); // 结束当前Activity
				showToast("验证成功");
				Intent i = new Intent(ResetGestureActivity.this,
						CreateGesturePasswordActivity.class);
				setResult(CANCEL_OK);
				startActivity(i);
				finish();

			} else { // 不一致

				// 设置显示的模式为错误模式
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Wrong);

				// 检查输入长度
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++; // 允许出错的次数

					// 获取当前还剩余允许出错的次数
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					if (retry >= 0) {
						if (retry == 0)
							showToast("您已5次输错密码，请30秒后再试");

						mHeadTextView.setText("密码错误，还可以再输入" + retry + "次");
						mHeadTextView.setTextColor(Color.RED);
					}

				} else { // 长度不够
					showToast("输入长度不够，请重试");
				}

				// 当次数已经大于5次，限制一段时间后，重试 2015/1/19 15:46
				if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
					mHandler.postDelayed(attemptLockout, 200); // 延迟200毫秒后进行
				} else {

					// 清除图案
					mLockPatternView.postDelayed(mClearPatternRunnable, 1500);
				}
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
		}
	};

	/**
	 * 限制一段时间无法输入图案 2015/1/19 15:48
	 */
	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			mLockPatternView.clearPattern(); // 清除图案
			mLockPatternView.setEnabled(false); // 限制输入

			// 定时器：参数：总毫秒数，每1000毫秒跳动1次 2015/1/19 15:51
			mCountdownTimer = new CountDownTimer(
					LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

				/**
				 * 心跳 2015/1/19 15:50
				 * 
				 * @param millisUntilFinished
				 *            剩余的毫秒数
				 */
				@Override
				public void onTick(long millisUntilFinished) {
					int secondsRemaining = (int) (millisUntilFinished / 1000);
					if (secondsRemaining > 0) {
						mHeadTextView.setText(secondsRemaining + " 秒后重试");
					}
				}

				@Override
				public void onFinish() {

					// 时间到，重置
					mHeadTextView.setText("请绘制手势密码");
					mHeadTextView.setTextColor(Color.WHITE);
					mLockPatternView.setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
				}
			}.start();
		}
	};
}
