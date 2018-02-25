package com.hispital.appdiary.adapter;

import java.util.List;

import com.hispital.appdiary.R;
import com.hispital.appdiary.activity.UpdatePatientActivity;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.cache.AsyncImageLoader;
import com.hispital.appdiary.entity.PatientItem;
import com.hispital.appdiary.util.AppPreferences;
import com.hispital.appdiary.util.AppPreferences.PreferenceKey;
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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PatientItemAdapter extends SimpleBaseAdapter<PatientItem> {
	private SlideListView listView;
	final private String uid = AppPreferences.instance().getString(PreferenceKey.USER_ID);

	public PatientItemAdapter(Context c, List<PatientItem> datas, SlideListView listView) {
		super(c, datas);
		this.listView = listView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EntityHolder entityHolder = null;

		if (convertView == null) {
			entityHolder = new EntityHolder();

			convertView = layoutInflater.inflate(R.layout.patient_item, null);

			ViewUtils.inject(entityHolder, convertView);

			convertView.setTag(entityHolder);
		} else {
			entityHolder = (EntityHolder) convertView.getTag();
		}

		final int ption = position;
		final int pid = datas.get(position).pid;
		entityHolder.main_tv_delete.setTag(datas.get(position).pid);
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
							params.addBodyParameter("pid", pid + "");
							LocalApplication.getInstance().httpUtils.send(HttpMethod.POST,
									ConstantsUtil.SERVER_URL + "deletePatientByPid", params,
									new RequestCallBack<String>() {

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
		entityHolder.main_tv_edit.setTag(datas.get(position).pid);
		entityHolder.main_tv_edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!JStringKit.isEmpty(v.getTag().toString())) {
					UpdatePatientActivity.startActivity(c, v.getTag().toString(), true);
				}
				notifyDataSetChanged();
				listView.turnToNormal();
			}
		});

		// entityHolder.patient_focus_iv_img.setImageBitmap(bm);

		entityHolder.patient_item_tv_content.setText(datas.get(position).pcondition);
		entityHolder.item_tv_time
				.setText(datas.get(position).ptime.substring(0, 11) + " by " + datas.get(position).name);
		// 给imageview设置一个tag，保证异步加载图片时不会乱序
		entityHolder.patient_item_iv_img.setTag(ConstantsUtil.IMAGE_URL + datas.get(position).purl);
		// 开启异步加载图片
		AsyncImageLoader.getInstance(c).loadBitmaps(listView, entityHolder.patient_item_iv_img,
				ConstantsUtil.IMAGE_URL + datas.get(position).purl, DisplayUtil.dip2px(c, 105),
				DisplayUtil.dip2px(c, 70));

		// 调制到现实页面
		entityHolder.patient_item_iv_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UpdatePatientActivity.startActivity(v.getContext(), datas.get(ption).pid + "", false);
			}
		});
		Bitmap bmp = null;
		boolean temp = false;
		if (datas.get(position).userid == 0) {
			bmp = BitmapFactory.decodeResource(listView.getResources(), R.drawable.focus_unchoose);
			temp = true;
		} else {
			bmp = BitmapFactory.decodeResource(listView.getResources(), R.drawable.focus_choose);
			temp = false;
		}
		final boolean iscreate = temp;
		entityHolder.patient_focus_iv_img.setImageBitmap(bmp);
		entityHolder.patient_focus_iv_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RequestParams params = new RequestParams();
				params.addBodyParameter("uid", uid);
				params.addBodyParameter("pid", pid + "");
				params.addBodyParameter("isCreate", iscreate + "");
				LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "updateFocus",
						params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// 局部更形关注图片图片
						if (iscreate) {
							datas.get(ption).userid = Integer.valueOf(uid).intValue();
						} else {
							datas.get(ption).userid = 0;
						}
						notifyDataSetChanged(listView, ption);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						ToastMaker.showShortToast("数据返回失败");
					}

				});
			}
		});

		return convertView;
	}

	private class EntityHolder {
		@ViewInject(R.id.patient_item_iv_img)
		ImageView patient_item_iv_img;
		@ViewInject(R.id.patient_item_tv_content)
		TextView patient_item_tv_content;
		@ViewInject(R.id.item_tv_time)
		TextView item_tv_time;
		@ViewInject(R.id.patient_focus_iv_img)
		ImageView patient_focus_iv_img;
		@ViewInject(R.id.main_tv_edit)
		TextView main_tv_edit;
		@ViewInject(R.id.main_tv_delete)
		TextView main_tv_delete;
	}

}
