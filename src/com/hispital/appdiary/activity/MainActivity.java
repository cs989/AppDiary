package com.hispital.appdiary.activity;

import com.hispital.appdiary.R;
import com.hispital.appdiary.fragment.FocusFragment;
import com.hispital.appdiary.fragment.InfoFragment;
import com.hispital.appdiary.fragment.SelfFragment;
import com.hispital.appdiary.util.AppPreferences;
import com.hispital.appdiary.view.ToastMaker;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	// 日记
	@ViewInject(R.id.diary_llyt_info)
	LinearLayout diary_llyt_info;
	@ViewInject(R.id.diary_iv_info)
	ImageView diary_iv_info;
	@ViewInject(R.id.diary_tv_info)
	TextView diary_tv_info;
	// 关注
	@ViewInject(R.id.diary_llyt_focus)
	LinearLayout diary_llyt_focus;
	@ViewInject(R.id.diary_iv_focus)
	ImageView diary_iv_focus;
	@ViewInject(R.id.diary_tv_focus)
	TextView diary_tv_focus;
	// 我的
	@ViewInject(R.id.diary_llyt_self)
	LinearLayout diary_llyt_self;
	@ViewInject(R.id.diary_iv_self)
	ImageView diary_iv_self;
	@ViewInject(R.id.diary_tv_self)
	TextView diary_tv_self;

	// 点击退出时记录时间
	private long firstTime = 0;
	// 选中的索引
	private int chooseIndex = -1;

	// fragment事务
	private FragmentTransaction ft;

	// 是否activity发生了回收
	private boolean isRecycle = false;

	private FragmentOnTouchListener fragmentOnTouchListener;

	// 静态方法启动activity
	public static void startActivity(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 防止下面输入框无法获取焦点
			viewOnClick(diary_llyt_info);
		}
	}

	// 控件点击事件
	@OnClick({ R.id.diary_llyt_info, R.id.diary_llyt_focus, R.id.diary_llyt_self })
	public void viewOnClick(View view) {
		ft = getSupportFragmentManager().beginTransaction();

		switch (view.getId()) {
		// 日记
		case R.id.diary_llyt_info:

			if (chooseIndex != 0) {
				chooseIndex = 0;
				tabBgChange(chooseIndex);
				ft.replace(R.id.diary_flyt_content,
						InfoFragment.instantiate(MainActivity.this, InfoFragment.class.getName(), null),
						"newsfragment");
			}

			break;
		// 关注
		case R.id.diary_llyt_focus:

			if (chooseIndex != 1) {
				chooseIndex = 1;
				tabBgChange(chooseIndex);
				ft.replace(R.id.diary_flyt_content,
						FocusFragment.instantiate(MainActivity.this, FocusFragment.class.getName(), null),
						"findfragment");
			}

			break;
		// 我的
		case R.id.diary_llyt_self:

			if (chooseIndex != 2) {
				chooseIndex = 2;
				tabBgChange(chooseIndex);
				ft.replace(R.id.diary_flyt_content,
						SelfFragment.instantiate(MainActivity.this, SelfFragment.class.getName(), null),
						"selffragment");
			}

			break;

		default:
			break;
		}
		ft.commit();
	}

	// 切换tab背景
	private void tabBgChange(int index) {
		switch (index) {
		case 0:

			diary_iv_info.setSelected(true);
			diary_tv_info.setTextColor(getResources().getColor(R.color.diary_cl_choose));

			diary_iv_focus.setSelected(false);
			diary_tv_focus.setTextColor(getResources().getColor(R.color.diary_cl_unchoose));
			diary_iv_self.setSelected(false);
			diary_tv_self.setTextColor(getResources().getColor(R.color.diary_cl_unchoose));

			break;
		case 1:

			diary_iv_focus.setSelected(true);
			diary_tv_focus.setTextColor(getResources().getColor(R.color.diary_cl_choose));

			diary_iv_info.setSelected(false);
			diary_tv_info.setTextColor(getResources().getColor(R.color.diary_cl_unchoose));
			diary_iv_self.setSelected(false);
			diary_tv_self.setTextColor(getResources().getColor(R.color.diary_cl_unchoose));

			break;
		case 2:

			diary_iv_self.setSelected(true);
			diary_tv_self.setTextColor(getResources().getColor(R.color.diary_cl_choose));

			diary_iv_focus.setSelected(false);
			diary_tv_focus.setTextColor(getResources().getColor(R.color.diary_cl_unchoose));
			diary_iv_info.setSelected(false);
			diary_tv_info.setTextColor(getResources().getColor(R.color.diary_cl_unchoose));

			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_main;
	}

	@Override
	protected void initParams() {
		// TODO Auto-generated method stub
		if (AppPreferences.instance().getString(AppPreferences.PreferenceKey.PRO_ID).equals("3")) {
			diary_llyt_focus.setVisibility(View.GONE);
		} else {
			diary_llyt_focus.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (fragmentOnTouchListener != null) {
			fragmentOnTouchListener.onTouch(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	public interface FragmentOnTouchListener {
		public boolean onTouch(MotionEvent ev);
	}

	public void registerFragmentOnTouchListener(FragmentOnTouchListener fragmentOnTouchListener) {
		this.fragmentOnTouchListener = fragmentOnTouchListener;
	}

	public void unregisterMyOnTouchListener(FragmentOnTouchListener myOnTouchListener) {
		this.fragmentOnTouchListener = null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isRecycle", true);
		outState.putInt("chooseIndex", chooseIndex);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		isRecycle = savedInstanceState.getBoolean("isRecycle");
		chooseIndex = savedInstanceState.getInt("chooseIndex");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isRecycle) {
			tabBgChange(chooseIndex);
		}
	}

	@Override
	public void onBackPressed() {
		long secondTime = System.currentTimeMillis();
		// 如果两次按键时间间隔大于1000毫秒，则不退出
		if (secondTime - firstTime > 1000) {
			ToastMaker.showShortToast("再按一次退出客户端");
			firstTime = secondTime;// 更新firstTime
		} else {
			finish();
		}
	}
}
