package com.hispital.appdiary.activity;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.hispital.appdiary.R;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.cache.AsyncImageLoader;
import com.hispital.appdiary.entity.ImageItem;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.JListKit;
import com.hispital.appdiary.view.photoview.PhotoView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 资讯图解详情
 * 
 * @author blue
 * 
 */
public class NewsPictureActivity extends BaseActivity {
	@ViewInject(R.id.info_picture_iv_back)
	ImageView info_picture_iv_back;
	@ViewInject(R.id.info_picture_tv_index)
	TextView info_picture_tv_index;
	@ViewInject(R.id.info_picture_vp)
	ViewPager info_picture_vp;

	// 获取的数据
	private List<ImageItem> dataList = JListKit.newArrayList();
	private int position;

	// 静态方法启动activity
	public static void startActivity(Context context, int position, String datas) {
		Intent intent = new Intent(context, NewsPictureActivity.class);
		intent.putExtra("datas", datas);
		intent.putExtra("position", position);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		dataList = JSONArray.parseArray(getIntent().getStringExtra("datas"), ImageItem.class);
		position = getIntent().getIntExtra("position", 1);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.news_picture_main;
	}

	@Override
	protected void initParams() {
		info_picture_tv_index.setText(position + 1 + "/" + dataList.size());
		// 绑定适配器
		info_picture_vp.setAdapter(new ViewPagerAdapter());
		info_picture_vp.setOnPageChangeListener(new ViewPagerChangeListener());
		info_picture_vp.setCurrentItem(position);
	}

	// 控件点击事件
	@OnClick(R.id.info_picture_iv_back)
	public void viewOnClick(View view) {
		finish();
	}

	// 查看大图viewpager适配器
	private class ViewPagerAdapter extends PagerAdapter {

		@SuppressLint("InflateParams")
		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View view = getLayoutInflater().inflate(R.layout.news_picture_item, null);
			PhotoView picture_iv_item = (PhotoView) view.findViewById(R.id.picture_iv_item);
			// 给imageview设置一个tag，保证异步加载图片时不会乱序
			picture_iv_item.setTag(ConstantsUtil.IMAGE_URL + dataList.get(position).iurl);
			// 开启异步加载图片,显示图片宽度为screenW
			AsyncImageLoader.getInstance(NewsPictureActivity.this).loadBitmaps(view, picture_iv_item,
					ConstantsUtil.IMAGE_URL + dataList.get(position).iurl, LocalApplication.getInstance().screenW, 0);
			container.addView(view);

			return view;
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);
		}

	}

	// viewpager切换监听器
	private class ViewPagerChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			info_picture_tv_index.setText((arg0 + 1) + "/" + dataList.size());
		}

	}
}
