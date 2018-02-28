package com.hispital.appdiary.adapter;

import java.util.List;

import com.hispital.appdiary.R;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.entity.MessageItem;
import com.hispital.appdiary.util.AppPreferences;
import com.hispital.appdiary.util.AppPreferences.PreferenceKey;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.view.ToastMaker;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class MessageItemAdapter extends SimpleBaseAdapter<MessageItem> {

	private ListView listView;

	public MessageItemAdapter(Context c, List<MessageItem> datas, ListView listView) {
		super(c, datas);
		this.listView = listView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		EntityHolder entityHolder = null;

		if (convertView == null) {
			entityHolder = new EntityHolder();

			convertView = layoutInflater.inflate(R.layout.message_item, null);

			ViewUtils.inject(entityHolder, convertView);

			convertView.setTag(entityHolder);
		} else {
			entityHolder = (EntityHolder) convertView.getTag();
		}

		entityHolder.info_show_tv_title.setText(position + 1 + "F");
		entityHolder.info_msg_tv_content.setText(datas.get(position).content);
		entityHolder.item_msg_tv_time
				.setText(datas.get(position).time.substring(0, 11) + " by " + datas.get(position).name);

		final int tempposition = position;
		// entityHolder.item_msg_tv_delete.setVisibility(View.GONE);

		if ((datas.get(position).uid + "").equals(AppPreferences.instance().getString(PreferenceKey.USER_ID))) {
			entityHolder.item_msg_tv_delete.setVisibility(View.VISIBLE);
			entityHolder.item_msg_tv_delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					deleteMsg(tempposition);
				}
			});
		} else {
			entityHolder.item_msg_tv_delete.setVisibility(View.GONE);
		}
		return convertView;
	}

	private void deleteMsg(final int position) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("mid", datas.get(position).mid + "");
		LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "deleteMsgByMid",
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						datas.remove(position);
						notifyDataSetChanged();
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						ToastMaker.showShortToast("数据返回失败");
					}

				});
	}

	private class EntityHolder {
		@ViewInject(R.id.info_show_tv_title)
		TextView info_show_tv_title;
		@ViewInject(R.id.info_msg_tv_content)
		TextView info_msg_tv_content;
		@ViewInject(R.id.item_msg_tv_time)
		TextView item_msg_tv_time;
		@ViewInject(R.id.item_msg_tv_delete)
		TextView item_msg_tv_delete;
	}
}
