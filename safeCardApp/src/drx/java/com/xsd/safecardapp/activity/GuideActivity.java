package com.xsd.safecardapp.activity;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.utils.Consts;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用：导航功能界面
 */
public class GuideActivity extends Activity {

	private WebView webView;
	private ProgressBar progressBar;

	private Double lat;
	private Double lng;
	private Double currentLat;
	private Double currentLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				progressBar.setProgress(progress);
				if (progress == 100) {
					progressBar.setVisibility(View.INVISIBLE);
				}
			}
		});
		webView.setWebViewClient(new WebViewClient() {

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) { // Handle the error

			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		Bundle bundle = getIntent().getBundleExtra(Consts.INTENT_DATA);
		if (bundle != null) {
			lat = bundle.getDouble("lat");
			lng = bundle.getDouble("lng");
			currentLat = bundle.getDouble("currentLat");
			currentLng = bundle.getDouble("currentLng");

			webView.loadUrl("http://m.amap.com/?from=" + currentLat + "," + currentLng
					+ "(from)&to=" + lat + "," + lng
					+ "(to)&type=0&opt=1");
		}

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		webView.destroy();
		webView = null;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
