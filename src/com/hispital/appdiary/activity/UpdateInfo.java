package com.hispital.appdiary.activity;

import android.content.Context;
import android.content.Intent;

public class UpdateInfo extends BaseActivity {

	
	
	// 静态方法启动activity
	public static void startActivity(Context context) {
		Intent intent = new Intent(context, UpdateInfo.class);
		context.startActivity(intent);
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void initParams() {
		// TODO Auto-generated method stub

	}

}
