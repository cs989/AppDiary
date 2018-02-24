package com.hispital.appdiary.activity;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.hispital.appdiary.R;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.entity.VersionInfo;
import com.hispital.appdiary.util.AppPreferences;
import com.hispital.appdiary.util.AppPreferences.PreferenceKey;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.UpdateManager;
import com.hispital.appdiary.util.VersionUtil;
import com.hispital.appdiary.view.ToastMaker;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * loading界面
 * 
 * @author blue
 */
public class LoadingActivity extends BaseActivity {
	@ViewInject(R.id.loading_iv_ad)
	ImageView loading_iv_ad;

	// 透明度补间动画
	private Animation layoutAnimation;

	@Override
	protected int getLayoutId() {
		return R.layout.loading_main;
	}

	String downLoadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";

	@Override
	protected void initParams() {
		// 透明度补间动画
		layoutAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_loading);
		layoutAnimation.setAnimationListener(new AlphaLayoutListener());
		loading_iv_ad.startAnimation(layoutAnimation);
	}

	// 透明度补间动画监听器
	class AlphaLayoutListener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			loading_iv_ad.postDelayed(new Runnable() {

				@Override
				public void run() {
					// 得到启动时的版本号,此时没有存储过数据所以为空字符串
					LocalApplication.getInstance().httpUtils.send(HttpMethod.GET,
							ConstantsUtil.SERVER_URL + "getVersionCode", new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// 回送消息、
							ToastMaker.showShortToast("请求网络失败");
						}

						@Override
						public void onLoading(long total, long current, boolean isUploading) {
							super.onLoading(total, current, isUploading);
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// 回送消息
							String usrid = AppPreferences.instance().getString(PreferenceKey.USER_ID);
							VersionInfo tmp = JSONObject.parseObject(arg0.result, VersionInfo.class);
							if (VersionUtil.getAppVersionName().equals(tmp.versionname)) {
								deleteFile(downLoadPath + tmp.appname + ".apk");
								if (usrid.equals("")) {
									LoginActivity.startActivity(LoadingActivity.this);
								} else {
									MainActivity.startActivity(LoadingActivity.this);
								}
								finish();
							} else {
								checkUpdate(LoadingActivity.this, 0,
										ConstantsUtil.IMAGE_URL + "apk/" + tmp.appname + ".apk", "更新了\n修复", true);
							}

							// String start_version =
							// AppPreferences.instance().getString(PreferenceKey.START_VERSION);

						}
					});

					// if
					// (!start_version.equals(VersionUtil.getAppVersionName()))
					// {
					// // GuideActivity.startActivity(LoadingActivity.this);
					// // checkUpdate(LoadingActivity.this, 2,
					// // ConstantsUtil.IMAGE_URL + "apk/Diary.apk", "更新了\n修复",
					// // true);
					// //
					// AppPreferences.instance().putString(PreferenceKey.START_VERSION,
					// // VersionUtil.getAppVersionName());
					// } else {
					// deleteFile(downLoadPath + tmp.appname + ".apk");
					// MainActivity.startActivity(LoadingActivity.this);
					// }

				}
			}, 2 * 1000);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationStart(Animation animation) {

		}
	}

	// 删除安装包
	public boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return false;
	}

	public void checkUpdate(Context context, int versionCode, String url, String updateMessage, boolean isForced) {
		// if (versionCode >
		// UpdateManager.getInstance().getVersionCode(context)) {
		int type = 0;// 更新方式，0：引导更新，1：安装更新，2：强制更新
		// if (UpdateManager.getInstance().isWifi(context)) {
		// type = 1;
		// }
		if (isForced) {
			type = 2;
		}

		// 检测是否已下载

		File dir = new File(downLoadPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
		if (fileName == null && TextUtils.isEmpty(fileName) && !fileName.contains(".apk")) {
			fileName = context.getPackageName() + ".apk";
		}
		File file = new File(downLoadPath + fileName);

		// 设置参数
		UpdateManager.getInstance().setType(type).setUrl(url).setUpdateMessage(updateMessage).setFileName(fileName)
				.setIsDownload(file.exists());
		if (type == 1 && !file.exists()) {
			UpdateManager.getInstance().downloadFile(context);
		} else {
			UpdateManager.getInstance().showDialog(context);
		}
		// }
	}
}
