package com.hispital.appdiary.adapter;

import java.util.List;

import com.hispital.appdiary.R;
import com.hispital.appdiary.cache.AsyncImageLoader;
import com.hispital.appdiary.entity.ImageItem;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.DisplayUtil;
import com.lidroid.xutils.ViewUtils;
import android.support.v4.app.Fragment;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageItemAdapter extends SimpleBaseAdapter<ImageItem> {

	private GridView gridview;

	public ImageItemAdapter(Context c, List<ImageItem> dates, GridView gridview) {
		super(c, dates);
		this.gridview = gridview;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EntityHolder entityHolder = null;
		if (convertView == null) {
			entityHolder = new EntityHolder();

			convertView = layoutInflater.inflate(R.layout.griditem_addpic, null);

			ViewUtils.inject(entityHolder, convertView);

			convertView.setTag(entityHolder);
		} else {
			entityHolder = (EntityHolder) convertView.getTag();
		}

		// 从服务器加载图片
		if (datas.get(position).bmp == null) {
			entityHolder.imageView1.setTag(ConstantsUtil.IMAGE_URL + datas.get(position).iurl);
			// 开启异步加载图片
			AsyncImageLoader.getInstance(c).loadBitmaps(gridview, entityHolder.imageView1,
					ConstantsUtil.IMAGE_URL + datas.get(position).iurl, DisplayUtil.dip2px(c, 105),
					DisplayUtil.dip2px(c, 70));
		} else {
			// 从本地加载图片
			entityHolder.imageView1.setTag(datas.get(position).iurl);
			entityHolder.imageView1.setImageBitmap(datas.get(position).bmp);
		}

		return convertView;

	}

	private class EntityHolder {
		@ViewInject(R.id.imageView1)
		ImageView imageView1;
	}

}
