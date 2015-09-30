package com.wr.webview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.samsung.zr.webviewtest.R;

public class MainActivity extends Activity {

	private WebView mWebView;

    // WebViewClient主要帮助WebView处理各种通知、请求事件
	private WebViewClient mWebViewClient = new WebViewClient() {
		// 处理页面导航
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			mWebView.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}
	};
	
    //	WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
	private WebChromeClient mChromeClient = new WebChromeClient() {

		private View myView = null;
		private CustomViewCallback myCallback = null;

		// 配置权限 （在WebChromeClinet中实现）
		@Override
		public void onGeolocationPermissionsShowPrompt(String origin,
				GeolocationPermissions.Callback callback) {
			callback.invoke(origin, true, false);
			super.onGeolocationPermissionsShowPrompt(origin, callback);
		}

		// 扩充数据库的容量（在WebChromeClinet中实现）
		@Override
		public void onExceededDatabaseQuota(String url,
				String databaseIdentifier, long currentQuota,
				long estimatedSize, long totalUsedQuota,
				WebStorage.QuotaUpdater quotaUpdater) {

			quotaUpdater.updateQuota(estimatedSize * 2);
		}

		// 扩充缓存的容量
		@Override
		public void onReachedMaxAppCacheSize(long spaceNeeded,
				long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {

			quotaUpdater.updateQuota(spaceNeeded * 2);
		}

		// Android 使WebView支持HTML5 Video（全屏）播放的方法
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			if (myCallback != null) {
				myCallback.onCustomViewHidden();
				myCallback = null;
				return;
			}
			ViewGroup parent = (ViewGroup) mWebView.getParent();
			parent.removeView(mWebView);
			parent.addView(view);
			myView = view;
			myCallback = callback;
			mChromeClient = this;
		}

		@Override
		public void onHideCustomView() {
			if (myView != null) {
				if (myCallback != null) {
					myCallback.onCustomViewHidden();
					myCallback = null;
				}

				ViewGroup parent = (ViewGroup) myView.getParent();
				parent.removeView(myView);
				parent.addView(mWebView);
				myView = null;
			}
		}
	};

	private void initSettings() {

		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置标题栏样式
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏

		setContentView(R.layout.activity_main);
		mWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = mWebView.getSettings();

		webSettings.setJavaScriptEnabled(true); // 开启Javascript脚本
		webSettings.setDomStorageEnabled(true); // 启用localStorage 和// essionStorage
		webSettings.setAppCacheEnabled(true); // 开启应用程序缓存
		String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(appCacheDir);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setAppCacheMaxSize(1024 * 1024 * 1);// 设置缓冲大小，我设的是1M
		webSettings.setAllowFileAccess(true);
		webSettings.setRenderPriority(RenderPriority.HIGH); // 设置渲染优先级
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

		mWebView.setWebChromeClient(mChromeClient);
		mWebView.setWebViewClient(mWebViewClient);
	}

	// 浏览网页历史记录 goBack()和goForward()
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.initSettings();

		mWebView.loadUrl("http://192.168.1.230/html/player_vod_A000000.html");
		// mWebView.loadUrl("http://192.168.1.230/html/player_m3u8_A000000.html");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (null != mWebView) {
			mWebView.onPause();
		}
		super.onPause();
	}

}