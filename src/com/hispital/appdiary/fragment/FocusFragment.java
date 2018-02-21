package com.hispital.appdiary.fragment;

import java.util.List;

import com.hispital.appdiary.R;
import com.hispital.appdiary.activity.UpdateInfoActivity;
import com.hispital.appdiary.activity.UpdatePatientActivity;
import com.hispital.appdiary.util.JListKit;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 找车
 * 
 * @author blue
 */
public class FocusFragment extends BaseFragment {
	// 选择开关
	@ViewInject(R.id.fragment_focus_find_llyt_switch)
	LinearLayout fragment_focus_find_llyt_switch;
	// 已关注
	@ViewInject(R.id.fragment_focus_find_tv_focused)
	TextView fragment_focus_find_tv_focused;
	// 查找全部
	@ViewInject(R.id.fragment_focus_find_tv_all)
	TextView fragment_focus_find_tv_all;

	// 新增记录
	@ViewInject(R.id.fragment_find_focus_btn_add)
	Button fragment_find_focus_btn_add;

	// fragment事务
	private FragmentTransaction ft;

	// // viewpager
	// @ViewInject(R.id.info_vp)
	// ViewPager info_vp;

	private UpdateInfoActivity updateInfoActivity;

	private List<Fragment> fragmentList = JListKit.newArrayList();

	// // fragment事务
	// private FragmentTransaction ft;
	// 已关注
	private FocusFindAllFragment focusFindAllFragment;
	// 查找全部
	private FocusFindFocusFragment focusFindFocusFragment;

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_focus_main;
	}

	@Override
	protected void initParams() {
		viewOnClick(fragment_focus_find_tv_focused);
	}

	@OnClick({ R.id.fragment_focus_find_tv_focused, R.id.fragment_focus_find_tv_all, R.id.fragment_find_focus_btn_add })
	public void viewOnClick(View view) {
		ft = getChildFragmentManager().beginTransaction();

		switch (view.getId()) {
		// 已关注
		case R.id.fragment_focus_find_tv_focused:

			fragment_focus_find_llyt_switch.setBackgroundResource(R.drawable.fragment_find_ic_left);
			fragment_focus_find_tv_focused.setTextColor(getResources().getColor(R.color.find_cl_choose));
			fragment_focus_find_tv_all.setTextColor(getResources().getColor(R.color.find_cl_unchoose));
			if (focusFindFocusFragment == null) {
				focusFindFocusFragment = new FocusFindFocusFragment();
			}
			ft.replace(R.id.fragment_focus_flyt_content, focusFindFocusFragment);

			break;
		// 全部
		case R.id.fragment_focus_find_tv_all:

			fragment_focus_find_llyt_switch.setBackgroundResource(R.drawable.fragment_find_ic_right);
			fragment_focus_find_tv_all.setTextColor(getResources().getColor(R.color.find_cl_choose));
			fragment_focus_find_tv_focused.setTextColor(getResources().getColor(R.color.find_cl_unchoose));

			if (focusFindAllFragment == null) {
				focusFindAllFragment = new FocusFindAllFragment();
			}
			ft.replace(R.id.fragment_focus_flyt_content, focusFindAllFragment);

			break;
		case R.id.fragment_find_focus_btn_add:
			addPatient();
			break;
		default:
			break;
		}
		ft.commit();
	}

	// 新建记录
	private void addPatient() {
		UpdatePatientActivity.startActivity(context);
	}
}