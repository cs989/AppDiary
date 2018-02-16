package com.hispital.appdiary.view;

import com.hispital.appdiary.util.DisplayUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SlideListView extends ListView {
	private String TAG = getClass().getSimpleName();

	private int mScreenWidth;
	private int mDownX;
	private int mDownY;
	private int mMenuWidth;

	private boolean isMenuShow;
	private boolean isMoving;

	private int mOperatePosition = -1;
	private ViewGroup mPointChild;
	private LinearLayout.LayoutParams mLayoutParams;

	public SlideListView(Context context) {
		super(context);
		getScreenWidth(context);
	}

	public SlideListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getScreenWidth(context);
	}

	public SlideListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		getScreenWidth(context);
	}

	private void getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			performActionDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			performActionMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			performActionUp();
			break;
		}
		return super.onTouchEvent(ev);
	}

	private void performActionDown(MotionEvent ev) {
		mDownX = (int) ev.getX();
		mDownY = (int) ev.getY();
		// 如果点击的不是同一个item，则关掉正在显示的菜单
		int position = pointToPosition(mDownX, mDownY);
		if (isMenuShow && position != mOperatePosition) {
			turnToNormal();
		}
		mOperatePosition = position;
		mPointChild = (ViewGroup) getChildAt(position - getFirstVisiblePosition());
		if (mPointChild != null) {
			mMenuWidth = mPointChild.getChildAt(1).getLayoutParams().width;
			mLayoutParams = (LinearLayout.LayoutParams) mPointChild.getChildAt(0).getLayoutParams();
			mLayoutParams.width = mScreenWidth- 10;
			setChildLayoutParams();
		}
	}

	private boolean performActionMove(MotionEvent ev) {
		int nowX = (int) ev.getX();
		int nowY = (int) ev.getY();
		// if (isMoving) {
		// if (Math.abs(nowY - mDownY) > 0) {
		// Log.e(TAG, "kkkkkkk");
		// onInterceptTouchEvent(ev);
		// }
		// }
		if (Math.abs(nowX - mDownX) > 0) {
			// 左滑 显示菜单
			if (nowX < mDownX) {
				if (isMenuShow) {
					mLayoutParams.leftMargin = -mMenuWidth;
				} else {
					// 计算显示的宽度
					int scroll = (nowX - mDownX);
					if (-scroll >= mMenuWidth) {
						scroll = -mMenuWidth;
					}
					mLayoutParams.leftMargin = scroll;
				}
			}
			// 右滑 如果菜单显示状态，则关闭菜单
			if (isMenuShow && nowX > mDownX) {
				int scroll = nowX - mDownX;
				if (scroll >= mMenuWidth) {
					scroll = mMenuWidth;
				}
				mLayoutParams.leftMargin = scroll - mMenuWidth;
			}
			setChildLayoutParams();
			isMoving = true;
			return true;
		}

		return super.onTouchEvent(ev);
	}

	private void performActionUp() {
		// 超过一半时，显示菜单，否则隐藏
		if (-mLayoutParams.leftMargin >= mMenuWidth / 2) {
			mLayoutParams.leftMargin = -mMenuWidth;
			setChildLayoutParams();
			isMenuShow = true;
		} else {
			turnToNormal();
		}
		isMoving = false;
	}

	private void setChildLayoutParams() {
		if (mPointChild != null) {
			mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
		}
	}

	/**
	 * 正常显示
	 */
	public void turnToNormal() {
		mLayoutParams.leftMargin = 0;
		mOperatePosition = -1;
		setChildLayoutParams();
		isMenuShow = false;
	}
}
