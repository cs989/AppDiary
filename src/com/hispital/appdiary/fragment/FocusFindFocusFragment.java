package com.hispital.appdiary.fragment;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.hispital.appdiary.R;
import com.hispital.appdiary.adapter.PatientItemAdapter;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.entity.PatientItem;
import com.hispital.appdiary.util.AppPreferences;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.DisplayUtil;
import com.hispital.appdiary.util.JListKit;
import com.hispital.appdiary.util.AppPreferences.PreferenceKey;
import com.hispital.appdiary.view.ProgressWheel;
import com.hispital.appdiary.view.SlideListView;
import com.hispital.appdiary.view.ToastMaker;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnScroll;
import com.lidroid.xutils.view.annotation.event.OnScrollStateChanged;

import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.AbsListView.OnScrollListener;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

public class FocusFindFocusFragment extends BaseFragment {

	@ViewInject(R.id.ptr)
	PtrFrameLayout ptr;
	@ViewInject(R.id.pw)
	ProgressWheel pw;
	@ViewInject(R.id.focus_diary_lv)
	SlideListView focus_diary_lv;

	// 数据源
	private List<PatientItem> datas = JListKit.newArrayList();
	// 适配器
	private PatientItemAdapter adapter;

	// 加载布局
	private LinearLayout loading_llyt;

	// 是否为最后一行
	private boolean isLastRow = false;
	// 是否还有更多数据
	private boolean isMore = true;
	// 是否正在加载数据
	private boolean isLoading = false;

	private boolean isUpdate = false;

	private int pageIndex = 0;
	private int pageSize = 10;
	private String uid = AppPreferences.instance().getString(PreferenceKey.USER_ID);

	@Override
	protected int getLayoutId() {

		return R.layout.fragment_focus_find_all_main;

	}

	@Override
	protected void initParams() {
		// 设置显示文字信息
		pw.setText("loading");
		// 开始旋转加载
		pw.spin();

		// 底部布局
		loading_llyt = (LinearLayout) getLayoutInflater(null).inflate(R.layout.listview_loading_view, null);

		// 初始化列表
		adapter = new PatientItemAdapter(context, datas, focus_diary_lv);
		// 增加底部加载布局
		focus_diary_lv.addFooterView(loading_llyt);
		// 绑定适配器
		focus_diary_lv.setAdapter(adapter);

		initPtr();

		pw.stopSpinning();
		pw.setVisibility(View.GONE);
		focus_diary_lv.setVisibility(View.VISIBLE);
		adapter.refreshDatas(datas);
		// 加载数据
		loadListData();
	}

	// 初始化下拉刷新
	private void initPtr() {
		// header
		StoreHouseHeader header = new StoreHouseHeader(context);
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		header.setPadding(0, DisplayUtil.dip2px(context, 15), 0, DisplayUtil.dip2px(context, 10));
		header.initWithString("ICU Diary");
		header.setTextColor(getResources().getColor(android.R.color.black));

		ptr.setHeaderView(header);
		ptr.addPtrUIHandler(header);
		ptr.setPtrHandler(new PtrHandler() {

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				isUpdate = true;
				pageIndex = 0;
				isMore = true;
				isLoading = true;
				loadListData();
			}

			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, focus_diary_lv, header);
			}
		});
	}

	// 加载列表数据
	private void loadListData() {
		isLoading = true;
		pageIndex++;
		RequestParams params = new RequestParams();
		params.addBodyParameter("pageIndex", pageIndex + "");
		params.addBodyParameter("pageSize", pageSize + "");
		params.addBodyParameter("uid", uid);
		params.addBodyParameter("isFocus", true + "");
		LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "getPatientList",
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						if (isUpdate) {
							ptr.refreshComplete();
						} else {
							pw.stopSpinning();
							pw.setVisibility(View.GONE);
						}
						ToastMaker.showShortToast("请求失败，请检查网络后重试");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String list = JSONObject.parseObject(arg0.result).getString("list");
						List<PatientItem> tmp = JSONObject.parseArray(list, PatientItem.class);

						pw.stopSpinning();
						pw.setVisibility(View.GONE);
						focus_diary_lv.setVisibility(View.VISIBLE);

						if (JListKit.isNotEmpty(tmp)) {
							if (pageIndex == 1) {
								// 移除底部加载布局
								if (tmp.size() < pageSize) {
									focus_diary_lv.removeFooterView(loading_llyt);
								}
							}
							if (isUpdate) {
								isUpdate = false;
								ptr.refreshComplete();
								datas.clear();
							}

							datas.addAll(tmp);
							adapter.refreshDatas(datas);
						} else {
							isMore = false;
							focus_diary_lv.removeFooterView(loading_llyt);
							ToastMaker.showShortToast("已没有更多数据");
						}
						isLoading = false;
					}
				});
	}

	@OnScroll(R.id.info_diary_lv)
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
			isLastRow = true;
		}
	}

	@OnScrollStateChanged(R.id.info_diary_lv)
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (isLastRow && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (!isLoading && isMore) {
				loadListData();
			}
			isLastRow = false;
		}
	}

}