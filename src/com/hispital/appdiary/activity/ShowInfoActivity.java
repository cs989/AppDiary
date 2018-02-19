package com.hispital.appdiary.activity;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.hispital.appdiary.R;
import com.hispital.appdiary.adapter.ImageItemAdapter;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.entity.ImageItem;
import com.hispital.appdiary.entity.InfoItem;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.JListKit;
import com.hispital.appdiary.view.DialogMaker;
import com.hispital.appdiary.view.ToastMaker;
import com.hispital.appdiary.view.DialogMaker.DialogCallBack;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ShowInfoActivity extends BaseActivity {

	// 返回按钮
	@ViewInject(R.id.info_show_iv_back)
	ImageView info_show_iv_back;

	// 标题
	@ViewInject(R.id.info_show_tv_title)
	TextView info_show_tv_title;

	// 内容
	@ViewInject(R.id.info_show_tv_context)
	TextView info_show_tv_context;

	// 上传图片
	@ViewInject(R.id.info_show_gv_images)
	GridView info_show_gv_images;

	// 留言内容
	@ViewInject(R.id.msg_show_et_context)
	EditText msg_show_et_context;

	// 留言提交按钮
	@ViewInject(R.id.msg_post_bt_context)
	Button msg_post_bt_context;

	// 留言列表
	@ViewInject(R.id.msg_item_lv)
	ListView msg_item_lv;

	private String rid;

	private String imageJson;

	// image数据源
	List<ImageItem> datas = JListKit.newArrayList();

	// 适配器
	private ImageItemAdapter adapter;

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.info_show_main;
	}

	// 静态方法启动activity
	public static void startActivity(Context context, String rid) {
		Intent intent = new Intent(context, ShowInfoActivity.class);
		intent.putExtra("rid", rid);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		rid = getIntent().getStringExtra("rid");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 防止下面输入框无法获取焦点
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 禁止横屏幕
	}

	@Override
	protected void initParams() {
		// TODO Auto-generated method stub
		adapter = new ImageItemAdapter(this, datas, info_show_gv_images);
		info_show_gv_images.setAdapter(adapter);

		// gridview添加点击事件
		info_show_gv_images.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				NewsPictureActivity.startActivity(v.getContext(), position, imageJson);
			}
		});
		loadInfoData();
	}

	private void loadInfoData() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("rid", rid);
		LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "getImageByRid",
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						List<ImageItem> tmp = JSONObject.parseArray(arg0.result, ImageItem.class);
						if (JListKit.isNotEmpty(tmp)) {
							imageJson = arg0.result;
							datas.addAll(tmp);
						}
						adapter.refreshDatas(datas);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						ToastMaker.showShortToast("数据返回失败");
					}

				});

		LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "getRecordByRid",
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// String list =
						// JSONObject.parseObject(arg0.result).getString("list");
						InfoItem tmp = JSONObject.parseObject(arg0.result, InfoItem.class);
						if (tmp != null) {
							info_show_tv_title.setText(tmp.title);
							info_show_tv_context.setText(tmp.content);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						ToastMaker.showShortToast("数据返回失败");
					}
				});

	}

	@OnClick({ R.id.info_show_iv_back, R.id.msg_post_bt_context })
	public void viewOnClick(View view) {
		switch (view.getId()) {
		// 后退
		case R.id.info_show_iv_back:
			finish();
			break;
		case R.id.msg_post_bt_context:
			finish();
			break;
		default:
			break;
		}
	}

}
