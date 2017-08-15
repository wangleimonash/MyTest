package com.dtr.zxing.activity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dtr.zxing.camera.CameraManager;
import com.dtr.zxing.decode.DecodeThread;
import com.dtr.zxing.utils.BeepManager;
import com.dtr.zxing.utils.CaptureActivityHandler;
import com.dtr.zxing.utils.InactivityTimer;
import com.google.zxing.Result;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.activity.InputDeviceActivity;
import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
/**
 * 
 *二维码扫描添加学生卡
 */
public final class CaptureActivity extends Activity implements
		SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;

	private SurfaceView scanPreview = null;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;

	private Rect mCropRect = null;

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	private boolean isHasSurface = false;

	private MyLoadingDialog dialog;
	private String imeiNumber;
	private Message message;
	private String resultCode = "";
	private SharedPreferences sp;
	private String imei;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (msg.what) {

			case Consts.MESSAGE_SUCCESS:
				if ("0".equals(resultCode)) {
					requestModeData(imei,"00:01".trim().replace(":", ""),"23:59".trim().replace(":", ""),10+"");
//					Toast.makeText(CaptureActivity.this, "添加成功",
//							Toast.LENGTH_SHORT).show();
//					Intent intent = new Intent(CaptureActivity.this,MainTabActivity.class);
//					startActivity(intent);
//					setResult(9527);
//					finish();
				}
				if ("4".equals(resultCode)) {
					Toast.makeText(CaptureActivity.this, "平安卡已被其他账号添加过了",
							Toast.LENGTH_SHORT).show();
					setResult(4);
					finish();
				}
				if ("12".equals(resultCode)) {
					Toast.makeText(CaptureActivity.this, "添加失败，请重新添加",
							Toast.LENGTH_SHORT).show();
					setResult(12);
					finish();
				}
				break;
			case Consts.MESSAGE_FAIL:
				Toast.makeText(CaptureActivity.this, "二维码有误",
						Toast.LENGTH_SHORT).show();
				    finish();
				break;
			case SUCCESS:
				Toast.makeText(CaptureActivity.this, "添加成功",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(CaptureActivity.this,MainTabActivity.class);
				startActivity(intent);
				setResult(9527);
				finish();
				break;
			case EMPTY:
				Toast.makeText(CaptureActivity.this, "添加成功",
						Toast.LENGTH_SHORT).show();
				Intent intent1 = new Intent(CaptureActivity.this,MainTabActivity.class);
				startActivity(intent1);
				setResult(9527);
				finish();
				break;
			case FAILT:
				Toast.makeText(CaptureActivity.this, "添加成功",
						Toast.LENGTH_SHORT).show();
				Intent intent2 = new Intent(CaptureActivity.this,MainTabActivity.class);
				startActivity(intent2);
				setResult(9527);
				finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_capture);

		scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);

		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);

		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.9f);
		animation.setDuration(4500);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.RESTART);
		scanLine.startAnimation(animation);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());

		handler = null;

		if (isHasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(scanPreview.getHolder());
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			scanPreview.getHolder().addCallback(this);
		}

		inactivityTimer.onResume();
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		beepManager.close();
		cameraManager.closeDriver();
		if (!isHasSurface) {
			scanPreview.getHolder().removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!isHasSurface) {
			isHasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isHasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * 
	 * @param bundle
	 *            The extras
	 */
	public void handleDecode(Result rawResult, Bundle bundle) {

		inactivityTimer.onActivity();
		beepManager.playBeepSoundAndVibrate();

		System.out.println("dsafdfdfg" + rawResult.getText() + "hehe");
		imei = rawResult.getText().toString().trim();
		requestAddDeviceData(rawResult.getText().toString().trim(),
				sp.getString("username", ""));

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, cameraManager,
						DecodeThread.ALL_MODE);
			}

			initCrop();
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		// camera error
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage("相机打开出错，请稍后重试");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}

		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	public Rect getCropRect() {
		return mCropRect;
	}

	/**
	 * 初始化截取的矩形区域
	 */
	private void initCrop() {
		int cameraWidth = cameraManager.getCameraResolution().y;
		int cameraHeight = cameraManager.getCameraResolution().x;

		/** 获取布局中扫描框的位置信息 */
		int[] location = new int[2];
		scanCropView.getLocationInWindow(location);

		int cropLeft = location[0];
		int cropTop = location[1] - getStatusBarHeight();

		int cropWidth = scanCropView.getWidth();
		int cropHeight = scanCropView.getHeight();

		/** 获取布局容器的宽高 */
		int containerWidth = scanContainer.getWidth();
		int containerHeight = scanContainer.getHeight();

		/** 计算最终截取的矩形的左上角顶点x坐标 */
		int x = cropLeft * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的左上角顶点y坐标 */
		int y = cropTop * cameraHeight / containerHeight;

		/** 计算最终截取的矩形的宽度 */
		int width = cropWidth * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的高度 */
		int height = cropHeight * cameraHeight / containerHeight;

		/** 生成最终的截取的矩形 */
		mCropRect = new Rect(x, y, width + x, height + y);
	}

	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void input(View v) {
		Intent intent = new Intent(this, InputDeviceActivity.class);
		startActivityForResult(intent, 110);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == 9527) {
			
				Intent intent = new Intent(this, MainTabActivity.class);
				startActivity(intent);
				setResult(9527);
				finish();
				return;
		
		} else {
			finish();
			return;
		}
	}

	private void requestAddDeviceData(final String imeiNumber,
			final String username) {

		if (NetUtils.isNetworkAvailable(this)) {

			showLoadingDialog();

			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "ADD"));
					params.add(new BasicNameValuePair("ID", imeiNumber));
					params.add(new BasicNameValuePair("SIM", ""));
					params.add(new BasicNameValuePair("PC", username));
					params.add(new BasicNameValuePair("AU", AUUtils
							.getAU("ADD")));

					try {
						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, Consts.URL_PATH, params);
						// 对参数编码
						String param = URLEncodedUtils.format(params, "UTF-8");
						System.out.println("xxxxxxurl" + Consts.URL_PATH + "?"
								+ param);
						JSONObject object = new JSONObject(jsonString);
						resultCode = object.getString("code");
						System.out.println("aaaaaawwwwwwdsdsads" + jsonString);
						System.out.println("aaaaaawwwwwwdsaddwww" + imeiNumber);
						System.out.println("aaaaaawwwwwwwww" + resultCode);
						if (!TextUtils.isEmpty(resultCode)) {
							message = Message.obtain();
							message.what = Consts.MESSAGE_SUCCESS;
							mHandler.sendMessage(message);
						} else {
							message = Message.obtain();
							message.what = Consts.MESSAGE_FAIL;
							mHandler.sendMessage(message);
						}

					} catch (Exception e) {
						e.printStackTrace();
						message = Message.obtain();
						message.what = Consts.MESSAGE_FAIL;
						mHandler.sendMessage(message);
					}
				}
			});

		} else {
			message = Message.obtain();
			message.what = Consts.MESSAGE_FAIL;
			mHandler.sendMessage(message);
		}

	}

	private void showLoadingDialog() {
		dialog = MyLoadingDialog.getMyLoadingDialog(this, R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();
	}
	/********************************后加上去的**************************/
	private String jsonString;
	private Message msg;
	private String mCode;
	public void requestModeData(final String imei,final String startTime,final String endTime,final String lt) {

		showLoadingDialog();

		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "SL"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("ST", startTime));
					params.add(new BasicNameValuePair("ET", endTime));
					params.add(new BasicNameValuePair("LT", lt));
					params.add(new BasicNameValuePair("RP", lt));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("SL")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("xiaosilaozi"+jsonString);

						JSONObject ojb = new JSONObject(jsonString);
						mCode = ojb.getString("code");

						System.out.println("fsdfdfhre" + mCode);

						if (!TextUtils.isEmpty(mCode)) {
							msg = Message.obtain();
							msg.what = SUCCESS;
							handler.sendMessage(msg);
						} else {
							msg = Message.obtain();
							msg.what = EMPTY;
							handler.sendMessage(msg);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} else {
			msg = Message.obtain();
			msg.what = FAILT;
			handler.sendMessage(msg);
		}
	}
	private static final int SUCCESS = 100;
	private static final int FAILT = -100;
	private static final int EMPTY = -200;
}