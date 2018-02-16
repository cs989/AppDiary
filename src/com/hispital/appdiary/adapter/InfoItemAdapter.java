package com.hispital.appdiary.adapter;

import java.util.List;

import com.hispital.appdiary.R;
import com.hispital.appdiary.cache.AsyncImageLoader;
import com.hispital.appdiary.entity.InfoItem;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.DisplayUtil;
import com.hispital.appdiary.view.SlideListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
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

		entityHolder.main_tv_delete.setTag(datas.get(position).id);
		entityHolder.main_tv_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("陈胜测试项目"+v.getTag());
				// datas.remove(datas.get(position));
				datas.remove(v.getTag());
				notifyDataSetChanged();
				listView.turnToNormal();
			}
		});
		entityHolder.main_tv_edit.setTag(datas.get(position).id);
		entityHolder.main_tv_edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				InfoItem temp = getItem(position);
				//datas.remove(datas.get(position).id);
				// datas.add(0, temp);
				notifyDataSetChanged();
				listView.turnToNormal();
			}
		});

		entityHolder.item_tv_title.setText(datas.get(position).title);
		// 给imageview设置一个tag，保证异步加载图片时不会乱序h
		entityHolder.item_iv_img.setTag(ConstantsUtil.IMAGE_URL + datas.get(position).cover_image);
		// 开启异步加载图片
		AsyncImageLoader.getInstance(c).loadBitmaps(listView, entityHolder.item_iv_img,
				ConstantsUtil.IMAGE_URL + datas.get(position).cover_image, DisplayUtil.dip2px(c, 105),
				DisplayUtil.dip2px(c, 70));

		return convertView;
	}

	private class EntityHolder {
		@ViewInject(R.id.item_iv_img)
		ImageView item_iv_img;
		@ViewInject(R.id.item_tv_title)
		TextView item_tv_title;
		@ViewInject(R.id.main_tv_edit)
		TextView main_tv_edit;
		@ViewInject(R.id.main_tv_delete)
		TextView main_tv_delete;
	}

}
