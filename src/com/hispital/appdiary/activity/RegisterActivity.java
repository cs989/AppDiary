package com.hispital.appdiary.activity;

import com.hispital.appdiary.R;
import com.hispital.appdiary.fragment.SelfFragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;

public class RegisterActivity extends BaseActivity {

	// fragment事务
	private FragmentTransaction ft;

	// 静态方法启动activity
	public static void startActivity(Context context) {
		Intent intent = new Intent(context, RegisterActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.register_main;
	}

	@Override
	protected void initParams() {
		// TODO Auto-generated method stub
		ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.register_flyt_content,
				SelfFragment.instantiate(RegisterActivity.this, SelfFragment.class.getName(), null),
				"registerfragment");
		ft.commit();
	}

}
