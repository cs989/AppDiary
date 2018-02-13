package com.hispital.appdiary.fragment;

import java.util.List;

import com.hispital.appdiary.R;
import com.hispital.appdiary.activity.UpdateInfoActivity;
import com.hispital.appdiary.util.JListKit;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 资讯
 * 
 * @author blue
 */
public class InfoFragment extends BaseFragment {

	// 选择开关
	@ViewInject(R.id.fragment_find_llyt_switch)
	LinearLayout fragment_find_llyt_switch;
	// 已关注
	@ViewInject(R.id.fragment_find_tv_focused)
	TextView fragment_find_tv_focused;
	// 查找全部
	@ViewInject(R.id.fragment_find_tv_all)
	TextView fragment_find_tv_all;

	// 新增记录
	@ViewInject(R.id.fragment_find_info_btn_add)
	Button fragment_find_info_btn_add;

	// viewpager
	@ViewInject(R.id.info_vp)
	ViewPager info_vp;
	
	private UpdateInfoActivity updateInfoActivity;

	private List<Fragment> fragmentList = JListKit.newArrayList();

	// // fragment事务
	// private FragmentTransaction ft;
	// 已关注
	private FindFocusedFragment findFocusedFragment;
	// 查找全部
	private FindAllFragment findAllFragment;

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_info_main;
	}

	@Override
	protected void initParams() {
		findFocusedFragment = new FindFocusedFragment();
		findAllFragment = new FindAllFragment();
		fragmentList.add(findFocusedFragment);
		fragmentList.add(findAllFragment);

		info_vp.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {

			@Override
			public int getCount() {
				return fragmentList.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return fragmentList.get(arg0);
			}
		});

		info_vp.setCurrentItem(0);
		info_vp.setOnPageChangeListener(new DefineOnPageChangeListener());
	}

	// 控件点击事件
	@SuppressWarnings("deprecation")
	@OnClick({ R.id.fragment_find_tv_focused, R.id.fragment_find_tv_all, R.id.fragment_find_info_btn_add })
	public void viewOnClick(View view) {
		switch (view.getId()) {
		case R.id.fragment_find_tv_focused:

			info_vp.setCurrentItem(0);

			break;
		case R.id.fragment_find_tv_all:

			info_vp.setCurrentItem(1);

			break;
		case R.id.fragment_find_info_btn_add:

			addDiary();

			break;
		default:
			break;
		}
	}

	// 新建记录
	private void addDiary() {
		UpdateInfoActivity.startActivity(context);
//		String path = "C:/Users/Public/Pictures/Sample Pictures/1.jpg";
//		RequestParams params = new RequestParams();
//		params.addBodyParameter("file", new File(path));
//		final Handler handler = new Handler();
//
//		LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "login", params, new RequestCallBack<String>()
//		{
//
//			@Override
//			public void onFailure(HttpException arg0, String arg1)
//			{
//				// 回送消息
//				handler.sendEmptyMessage(-1);
//			}
//
//			@Override
//			public void onLoading(long total, long current, boolean isUploading)
//			{
//				super.onLoading(total, current, isUploading);
//			}
//
//			@Override
//			public void onSuccess(ResponseInfo<String> arg0)
//			{
//				// 回送消息
//				handler.sendEmptyMessage(1);
//			}
//		});
	}

	// viewpager视图切换监听器
	public class DefineOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			// 要闻
			case 0:
				fragment_find_llyt_switch.setBackgroundResource(R.drawable.fragment_find_ic_left);
				fragment_find_tv_focused.setTextColor(getResources().getColor(R.color.find_cl_choose));
				fragment_find_tv_all.setTextColor(getResources().getColor(R.color.find_cl_unchoose));
				break;
			// 新车
			case 1:

				fragment_find_llyt_switch.setBackgroundResource(R.drawable.fragment_find_ic_right);
				fragment_find_tv_all.setTextColor(getResources().getColor(R.color.find_cl_choose));
				fragment_find_tv_focused.setTextColor(getResources().getColor(R.color.find_cl_unchoose));

				break;
			}
		}
	}
}
