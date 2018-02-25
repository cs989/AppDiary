package com.hispital.appdiary.adapter;

import java.util.List;

import com.hispital.appdiary.R;
import com.hispital.appdiary.cache.AsyncImageLoader;
import com.hispital.appdiary.entity.UserItem;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.DisplayUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

public class UserItemAdapter extends SimpleBaseAdapter<UserItem> {

	private ListView listView;

	public UserItemAdapter(Context c, List<UserItem> datas, ListView listView) {
		super(c, datas);
		this.listView = listView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EntityHolder entityHolder = null;

		if (convertView == null) {
			entityHolder = new EntityHolder();

			convertView = layoutInflater.inflate(R.layout.user_image_item, null);

			ViewUtils.inject(entityHolder, convertView);

			convertView.setTag(entityHolder);
		} else {
			entityHolder = (EntityHolder) convertView.getTag();
		}

		if (datas.get(position).uurl != null && !datas.get(position).uurl.equals("")
				&& datas.get(position).bmp == null) {
			entityHolder.self_iv_img.setTag(ConstantsUtil.IMAGE_URL + datas.get(position).uurl);
			// 开启异步加载图片
			AsyncImageLoader.getInstance(c).loadBitmaps(listView, entityHolder.self_iv_img,
					ConstantsUtil.IMAGE_URL + datas.get(position).uurl, DisplayUtil.dip2px(c, 105),
					DisplayUtil.dip2px(c, 70));
		} else if (datas.get(position).bmp == null) {
			Bitmap bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.empty_photo);
			entityHolder.self_iv_img.setImageBitmap(bmp);
		} else {
			entityHolder.self_iv_img.setImageBitmap(datas.get(position).bmp);
		}
		return convertView;

	}

	private class EntityHolder {
		@ViewInject(R.id.self_iv_img)
		ImageView self_iv_img;
	}

}
