package com.hispital.appdiary.adapter;

import java.util.List;

import com.hispital.appdiary.R;
import com.hispital.appdiary.activity.UpdatePatientActivity;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.cache.AsyncImageLoader;
import com.hispital.appdiary.entity.PatientItem;
import com.hispital.appdiary.util.AppPreferences;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.DisplayUtil;
import com.hispital.appdiary.util.JStringKit;
import com.hispital.appdiary.util.AppPreferences.PreferenceKey;
import com.hispital.appdiary.view.DialogMaker;
import com.hispital.appdiary.view.SlideListView;
import com.hispital.appdiary.view.ToastMaker;
import com.hispital.appdiary.view.DialogMaker.DialogCallBack;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SearchPatientItemAdapter extends SimpleBaseAdapter<PatientItem> {

	private ListView listView;

	public SearchPatientItemAdapter(Context c, List<PatientItem> datas, ListView listView) {
		super(c, datas);
		this.listView = listView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EntityHolder entityHolder = null;

		if (convertView == null) {
			entityHolder = new EntityHolder();

			convertView = layoutInflater.inflate(R.layout.search_patientitem, null);

			ViewUtils.inject(entityHolder, convertView);

			convertView.setTag(entityHolder);
		} else {
			entityHolder = (EntityHolder) convertView.getTag();
		}

		entityHolder.search_tv_patientitem.setText(datas.get(position).pno + ":" + datas.get(position).name);

		return convertView;
	}

	private class EntityHolder {

		@ViewInject(R.id.search_tv_patientitem)
		TextView search_tv_patientitem;

	}

}
