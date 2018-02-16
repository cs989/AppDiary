package com.hispital.appdiary.fragment;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.hispital.appdiary.R;
import com.hispital.appdiary.adapter.InfoItemAdapter;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.entity.InfoItem;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.DisplayUtil;
import com.hispital.appdiary.util.JListKit;
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
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

public class FindAllFragment extends BaseFragment {

	@ViewInject(R.id.ptr)
	PtrFrameLayout ptr;
	@ViewInject(R.id.pw)
	ProgressWheel pw;
	@ViewInject(R.id.info_diary_lv)
	SlideListView info_diary_lv;

	// 数据源
	private List<InfoItem> datas = JListKit.newArrayList();
	// 适配器
	private InfoItemAdapter adapter;

	// 加载布局
	private LinearLayout loading_llyt;

	// 是否为最后一行
	private boolean isLastRow = false;
	// 是否还有更多数据
	private boolean isMore = true;
	// 是否正在加载数据
	private boolean isLoading = false;

	private int pageIndex = 0;
	private int pageSize = 10;

	@Override
	protected int getLayoutId() {

		return R.layout.fragment_find_all_main;

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
		adapter = new InfoItemAdapter(context, datas, info_diary_lv);
		// 增加底部加载布局
		info_diary_lv.addFooterView(loading_llyt);
		// 绑定适配器
		info_diary_lv.setAdapter(adapter);

		// initPtr();

		for (int i = 0; i < 20; i++) {
			InfoItem item = new InfoItem();
			item.id = i;
			item.title = "这是第" + i + "个标题";
			datas.add(item);
		}

		pw.stopSpinning();
		pw.setVisibility(View.GONE);
		info_diary_lv.setVisibility(View.VISIBLE);
		adapter.refreshDatas(datas);
		// 加载数据
		// loadListData();
	}

	// 初始化下拉刷新
	private void initPtr() {
		// header
		StoreHouseHeader header = new StoreHouseHeader(context);
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		header.setPadding(0, DisplayUtil.dip2px(context, 15), 0, DisplayUtil.dip2px(context, 10));
		header.initWithString("Diary Diary");
		header.setTextColor(getResources().getColor(android.R.color.black));

		ptr.setHeaderView(header);
		ptr.addPtrUIHandler(header);
		ptr.setPtrHandler(new PtrHandler() {

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				frame.postDelayed(new Runnable() {
					@Override
					public void run() {
						ptr.refreshComplete();
					}
				}, 1800);
			}

			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
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
		LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "login", params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						pw.stopSpinning();
						pw.setVisibility(View.GONE);
						ToastMaker.showShortToast("请求失败，请检查网络后重试");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String list = JSONObject.parseObject(arg0.result).getString("list");
						List<InfoItem> tmp = JSONObject.parseArray(list, InfoItem.class);

						pw.stopSpinning();
						pw.setVisibility(View.GONE);
						info_diary_lv.setVisibility(View.VISIBLE);

						if (JListKit.isNotEmpty(tmp)) {
							if (pageIndex == 1) {
								// 移除底部加载布局
								if (tmp.size() < pageSize) {
									info_diary_lv.removeFooterView(loading_llyt);
								}
							}
							datas.addAll(tmp);
							adapter.refreshDatas(datas);
						} else {
							isMore = false;
							info_diary_lv.removeFooterView(loading_llyt);
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
