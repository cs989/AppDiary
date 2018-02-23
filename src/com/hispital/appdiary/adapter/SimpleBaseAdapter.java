package com.hispital.appdiary.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * SimpleBaseAdapter extends BaseAdapter
 * 
 * 实现getCount,getItem,getItemId方法
 * 
 * @author blue
 * 
 * @param <T>
 */
public abstract class SimpleBaseAdapter<T> extends BaseAdapter {
	protected Context c = null;

	protected LayoutInflater layoutInflater = null;

	protected List<T> datas = null;

	public SimpleBaseAdapter(Context c, List<T> datas) {
		this.c = c;
		this.datas = datas;
		layoutInflater = LayoutInflater.from(c);
	}

	/**
	 * 刷新适配器数据源
	 * 
	 * @author andrew
	 * @param datas
	 */
	public void refreshDatas(List<T> datas) {
		this.datas = datas;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return datas != null ? datas.size() : 0;
	}

	@Override
	public T getItem(int position) {
		return datas != null ? datas.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	//ListView 局部更新
	public void notifyDataSetChanged(ListView listView, int position) {
		/** 第一个可见的位置 **/
		int firstVisiblePosition = listView.getFirstVisiblePosition();
		/** 最后一个可见的位置 **/
		int lastVisiblePosition = listView.getLastVisiblePosition();

		/** 在看见范围内才更新，不可见的滑动后自动会调用getView方法更新 **/
		if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
			/** 获取指定位置view对象 **/
			View view = listView.getChildAt(position - firstVisiblePosition);
			getView(position, view, listView);
		}

	}

	/**
	 * 这个方法getView()，是用来逐一绘制每一条item
	 * 
	 * setTag()会把View与ViewHolder绑定，形成一一对应的关系，拖动listview的时候会重新绘制每一条item
	 * 但是那些已经取得绑定的View，会调用getTag()方法，就不会重新绘制，而是拿到内存中已经取得的资源，提高了效率
	 * 
	 * @param position
	 *            position就是位置从0开始
	 * @param convertView
	 *            convertView是Spinner,ListView中每一项要显示的view
	 * @param parent
	 *            parent就是父窗体了，也就是Spinner,ListView,GridView了
	 * @return 通常return 的view也就是convertView
	 */

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
