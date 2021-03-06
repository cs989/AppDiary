package com.hispital.appdiary.util;

import java.io.File;

import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.view.DialogMaker;
import com.hispital.appdiary.view.DialogMaker.DialogCallBack;
import com.hispital.appdiary.view.ToastMaker;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 版本更新
 */

public class UpdateManager {
	private String downLoadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";
	private int type = 0;// 更新方式，0：引导更新，1：安装更新，2：强制更新
	private String url = "";// apk下载地址
	private String updateMessage = "";// 更新内容
	private String fileName = null;// 文件名
	private boolean isDownload = false;// 是否下载
	// private NotificationManager mNotifyManager;
	private NotificationCompat.Builder mBuilder;
	private ProgressDialog progressDialog;

	public static UpdateManager updateManager;

	public static UpdateManager getInstance() {
		if (updateManager == null) {
			updateManager = new UpdateManager();
		}
		return updateManager;
	}

	private UpdateManager() {

	}

	/**
	 * 弹出版本更新提示框
	 */
	public void showDialog(final Context context) {
		String title = "";
		String left = "";
		boolean cancelable = true;
		if (type == 1 | isDownload) {
			title = "必须安装新版本才能使用";
			left = "浏览器下载更新";
		} else {
			title = "必须安装新版本才能使用";
			left = "浏览器下载更新";
		}
		if (type == 2) {
			cancelable = false;
		}

		DialogMaker.showCommonAlertDialog(context, title, "请选择", new String[] { left, "取消" }, new DialogCallBack() {
			@Override
			public void onButtonClicked(Dialog dialog, int position, Object tag) {
				switch (position) {
				case 0:
					if (type == 1 | isDownload) {
						installApk(context, new File(downLoadPath, fileName));
					} else {
						if (url != null && !TextUtils.isEmpty(url)) {
							// if (type == 2) {
							// createProgress(context);
							// } else {
							// createNotification(context);
							// }
							downloadFile(context);
							System.exit(0);
						} else {
							Toast.makeText(context, "下载地址错误", Toast.LENGTH_SHORT).show();
						}
					}
					break;
				case 1:
					System.exit(0);
					break;
				default:
					break;
				}
			}

			@Override
			public void onCancelDialog(Dialog dialog, Object tag) {
				return;
			}

		}, cancelable, true, new Object());
	}

	/**
	 * 下载apk
	 *
	 */
	public void downloadFile(final Context context) {

		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		context.startActivity(intent);
		// RequestParams params = new RequestParams();
		// params.setSaveFilePath(downLoadPath + fileName);
		// LocalApplication.getInstance().httpUtils.send(HttpMethod.GET, url,
		// new RequestCallBack<String>() {
		//
		// @Override
		// public void onFailure(HttpException arg0, String arg1) {
		// // 回送消息、
		// ToastMaker.showShortToast("下载失败");
		// Activity activity = (Activity) context;
		// activity.finish();
		// }
		//
		// @Override
		// public void onLoading(long total, long current, boolean isUploading)
		// {
		// if (type == 0) {
		// notifyNotification(current, total);
		// } else if (type == 2) {
		// progressDialog.setProgress((int) (current * 100 / total));
		// }
		// if (total == current) {
		// if (type == 0) {
		// mBuilder.setContentText("下载完成");
		// // mNotifyManager.notify(10086,
		// // mBuilder.build());
		// } else if (type == 2) {
		// progressDialog.setMessage("下载完成");
		// }
		// if (type == 1) {
		// showDialog(context);
		// } else {
		// installApk(context, new File(downLoadPath, fileName));
		// }
		// }
		// }
		//
		// @Override
		// public void onSuccess(ResponseInfo<String> arg0) {
		// ToastMaker.showShortToast("下载成功");
		// Activity activity = (Activity) context;
		// activity.finish();
		// installApk(context, new File(downLoadPath, fileName));
		// // 回送消息
		// // ToastMaker.showShortToast("下载成功");
		// // finish();
		// }
		// });

		// x.http().request(HttpMethod.GET, params, new
		// Callback.ProgressCallback<File>() {
		//
		// @Override
		// public void onSuccess(File result) {
		//
		// }
		//
		// @Override
		// public void onError(Throwable ex, boolean isOnCallback) {
		// Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
		// }
		//
		// @Override
		// public void onCancelled(CancelledException cex) {
		//
		// }
		//
		// @Override
		// public void onFinished() {
		//
		// }
		//
		// @Override
		// public void onWaiting() {
		//
		// }
		//
		// @Override
		// public void onStarted() {
		//
		// }
		//
		// @Override
		// public void onLoading(long total, long current, boolean
		// isDownloading) {
		// // 实时更新通知栏进度条
		// if (type == 0) {
		// notifyNotification(current, total);
		// } else if (type == 2) {
		// progressDialog.setProgress((int) (current * 100 / total));
		// }
		// if (total == current) {
		// if (type == 0) {
		// mBuilder.setContentText("下载完成");
		// mNotifyManager.notify(10086, mBuilder.build());
		// } else if (type == 2) {
		// progressDialog.setMessage("下载完成");
		// }
		// if (type == 1) {
		// showDialog(context);
		// } else {
		// installApk(context, new File(downLoadPath, fileName));
		// }
		// }
		// }
		// });
	}

	/**
	 * 强制更新时显示在屏幕的进度条
	 *
	 */
	private void createProgress(Context context) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMax(100);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("正在下载...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
	}

	/**
	 * 创建通知栏进度条
	 *
	 */
	private void createNotification(Context context) {
		// mNotifyManager = (NotificationManager)
		// context.getSystemService(NOTIFICATION_SERVICE);
		// mBuilder = new NotificationCompat.Builder(context);
		//// mBuilder.setSmallIcon(BaseAndroid.getBaseConfig().getAppLogo());
		// mBuilder.setContentTitle("版本更新");
		// mBuilder.setContentText("正在下载...");
		// mBuilder.setProgress(0, 0, false);
		// Notification notification = mBuilder.build();
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		// mNotifyManager.notify(10086, notification);
	}

	/**
	 * 更新通知栏进度条
	 *
	 */
	private void notifyNotification(long percent, long length) {
		// mBuilder.setProgress((int) length, (int) percent, false);
		// mNotifyManager.notify(10086, mBuilder.build());
	}

	/**
	 * 安装apk
	 *
	 * @param context
	 *            上下文
	 * @param file
	 *            APK文件
	 */
	private void installApk(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * @return 当前应用的版本号
	 */
	public int getVersionCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			int version = info.versionCode;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 判断当前网络是否wifi
	 */
	public boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public UpdateManager setUrl(String url) {
		this.url = url;
		return this;
	}

	public UpdateManager setType(int type) {
		this.type = type;
		return this;
	}

	public UpdateManager setUpdateMessage(String updateMessage) {
		this.updateMessage = updateMessage;
		return this;
	}

	public UpdateManager setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public UpdateManager setIsDownload(boolean download) {
		isDownload = download;
		return this;
	}

}
