package com.hispital.appdiary.adapter;

import java.util.List;

import com.hispital.appdiary.R;
import com.hispital.appdiary.activity.ShowInfoActivity;
import com.hispital.appdiary.activity.UpdateInfoActivity;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.cache.AsyncImageLoader;
import com.hispital.appdiary.entity.InfoItem;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.DisplayUtil;
import com.hispital.appdiary.util.JStringKit;
import com.hispital.appdiary.view.DialogMaker;
import com.hispital.appdiary.view.DialogMaker.DialogCallBack;
import com.hispital.appdiary.view.SlideListView;
import com.hispital.appdiary.view.ToastMaker;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 新闻条目适配器
 * 
 * @author blue
 * 
 */
public class InfoItemAdapter extends SimpleBaseAdapter<InfoItem> {
	private SlideListView listView;

	public InfoItemAdapter(Context c, List<InfoItem> datas, SlideListView listView) {
		super(c, datas);
		this.listView = listView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EntityHolder entityHolder = null;

		if (convertView == null) {
			entityHolder = new EntityHolder();

			convertView = layoutInflater.inflate(R.layout.info_item, null);

			ViewUtils.inject(entityHolder, convertView);

			convertView.setTag(entityHolder);
		} else {
			entityHolder = (EntityHolder) convertView.getTag();
		}

		final int ption = position;
		final int rid = datas.get(position).rid;
		entityHolder.main_tv_delete.setTag(datas.get(position).rid);
		entityHolder.main_tv_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DialogMaker.showCommonAlertDialog(c, "", "请选择", new String[] { "删除", "取消" }, new DialogCallBack() {
					@Override
					public void onButtonClicked(Dialog dialog, int position, Object tag) {
						// TODO Auto-generated method stub
						switch (position) {
						case 0:
							RequestParams params = new RequestParams();
							params.addBodyParameter("rid", rid + "");
							LocalApplication.getInstance().httpUtils.send(HttpMethod.POST,
									ConstantsUtil.SERVER_URL + "deleteRecord", params, new RequestCallBack<String>() {

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									datas.remove(datas.get(ption));
									notifyDataSetChanged();
									listView.turnToNormal();
								}

								@Override
								public void onFailure(HttpException error, String msg) {
									// TODO Auto-generated method stub
									ToastMaker.showShortToast("数据返回失败");
								}

							});
							break;
						case 1:
							dialog.dismiss();
							break;
						default:
							break;
						}
					}

					@Override
					public void onCancelDialog(Dialog dialog, Object tag) {
						return;
					}

				}, true, true, new Object());

			}
		});
		entityHolder.main_tv_edit.setTag(datas.get(position).rid);
		entityHolder.main_tv_edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!JStringKit.isEmpty(v.getTag().toString())) {
					UpdateInfoActivity.startActivity(c, v.getTag().toString());
				}
				notifyDataSetChanged();
				listView.turnToNormal();
			}
		});

		entityHolder.item_tv_title.setText(datas.get(position).title);
		entityHolder.item_tv_time
				.setText(datas.get(position).ftime.substring(0, 11) + " by " + datas.get(position).name);
		entityHolder.item_tv_count.setText(datas.get(position).msg_count + "");
		// 给imageview设置一个tag，保证异步加载图片时不会乱序
		entityHolder.item_iv_img.setTag(ConstantsUtil.IMAGE_URL + datas.get(position).purl);
		// 开启异步加载图片
		AsyncImageLoader.getInstance(c).loadBitmaps(listView, entityHolder.item_iv_img,
				ConstantsUtil.IMAGE_URL + datas.get(position).purl, DisplayUtil.dip2px(c, 105),
				DisplayUtil.dip2px(c, 70));

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				ShowInfoActivity.startActivity(v.getContext(), datas.get(position).rid + "");
			}
		});

		return convertView;
	}

	private class EntityHolder {
		@ViewInject(R.id.item_iv_img)
		ImageView item_iv_img;
		@ViewInject(R.id.item_tv_title)
		TextView item_tv_title;
		@ViewInject(R.id.item_tv_time)
		TextView item_tv_time;
		@ViewInject(R.id.item_tv_count)
		TextView item_tv_count;
		@ViewInject(R.id.main_tv_edit)
		TextView main_tv_edit;
		@ViewInject(R.id.main_tv_delete)
		TextView main_tv_delete;
	}

}
