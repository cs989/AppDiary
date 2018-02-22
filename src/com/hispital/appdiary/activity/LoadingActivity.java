package com.hispital.appdiary.activity;

import java.io.File;

import com.hispital.appdiary.R;
import com.hispital.appdiary.util.AppPreferences;
import com.hispital.appdiary.util.AppPreferences.PreferenceKey;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.UpdateManager;
import com.hispital.appdiary.util.VersionUtil;
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
					String start_version = AppPreferences.instance().getString(PreferenceKey.START_VERSION);
					checkUpdate(LoadingActivity.this, 2, ConstantsUtil.IMAGE_URL + "apk/Diary.apk", "更新了\n修复", true);
					if (!start_version.equals(VersionUtil.getAppVersionName())) {
						// GuideActivity.startActivity(LoadingActivity.this);

						AppPreferences.instance().putString(PreferenceKey.START_VERSION,
								VersionUtil.getAppVersionName());
					} else {
						MainActivity.startActivity(LoadingActivity.this);
					}
					finish();
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

	public void checkUpdate(Context context, int versionCode, String url, String updateMessage, boolean isForced) {
		if (versionCode > UpdateManager.getInstance().getVersionCode(context)) {
			int type = 0;// 更新方式，0：引导更新，1：安装更新，2：强制更新
			if (UpdateManager.getInstance().isWifi(context)) {
				type = 1;
			}
			if (isForced) {
				type = 2;
			}

			// 检测是否已下载
			String downLoadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";
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
		}
	}
}
