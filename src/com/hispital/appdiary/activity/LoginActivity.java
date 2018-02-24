package com.hispital.appdiary.activity;

import com.alibaba.fastjson.JSONObject;
import com.hispital.appdiary.R;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.entity.InfoItem;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.view.ToastMaker;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginActivity extends BaseActivity {

	// 登录
	@ViewInject(R.id.main_btn_login)
	TextView main_btn_login;

	@ViewInject(R.id.layout_progress)
	View layout_progress;

	@ViewInject(R.id.input_layout)
	View input_layout;

	@ViewInject(R.id.input_layout_name)
	LinearLayout input_layout_name;

	@ViewInject(R.id.input_layout_psw)
	LinearLayout input_layout_psw;

	@ViewInject(R.id.input_et_username)
	EditText input_et_username;

	@ViewInject(R.id.input_et_psw)
	EditText input_et_psw;

	private float mWidth, mHeight;

	@Override
	protected int getLayoutId() {

		return R.layout.login_activity_main;
	}

	@Override
	protected void initParams() {
		// TODO Auto-generated method stub

	}

	@OnClick({ R.id.main_btn_login })
	public void viewOnClick(View view) {
		switch (view.getId()) {
		// 登录
		case R.id.main_btn_login:
			if (input_et_username.getText().equals("") && input_et_psw.getText().equals("")) {
				ToastMaker.showShortToast("用户名或密码不能为空");
			} else {
				// 计算出控件的高与宽
				mWidth = main_btn_login.getMeasuredWidth();
				mHeight = main_btn_login.getMeasuredHeight();
				// 隐藏输入框
				input_layout_name.setVisibility(View.INVISIBLE);
				input_layout_psw.setVisibility(View.INVISIBLE);
				inputAnimator(input_layout, mWidth, mHeight);
				RequestParams params = new RequestParams();
				params.addBodyParameter("username", input_et_username.getText() + "");
				params.addBodyParameter("password", input_et_psw.getText() + "");
				LocalApplication.getInstance().httpUtils.send(HttpMethod.POST,
						ConstantsUtil.SERVER_URL + "getRecordByRid", params, new RequestCallBack<String>() {

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								// String list =
								// JSONObject.parseObject(arg0.result).getString("list");
								InfoItem tmp = JSONObject.parseObject(arg0.result, InfoItem.class);
								if (tmp != null) {
									// info_add_et_title.setText(tmp.title);
									// info_add_et_context.setText(tmp.content);
								}
							}

							@Override
							public void onFailure(HttpException error, String msg) {
								// TODO Auto-generated method stub
								ToastMaker.showShortToast("数据返回失败");
							}
						});
			}
			break;
		default:
			break;
		}
	}

	private void inputAnimator(final View view, float w, float h) {

		AnimatorSet set = new AnimatorSet();

		ValueAnimator animator = ValueAnimator.ofFloat(0, w);
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (Float) animation.getAnimatedValue();
				ViewGroup.MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
				params.leftMargin = (int) value;
				params.rightMargin = (int) value;
				view.setLayoutParams(params);
			}
		});

		ObjectAnimator animator2 = ObjectAnimator.ofFloat(input_layout, "scaleX", 1f, 0.5f);
		set.setDuration(1000);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.playTogether(animator, animator2);
		set.start();
		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				/**
				 * 动画结束后，先显示加载的动画，然后再隐藏输入框
				 */
				layout_progress.setVisibility(View.VISIBLE);
				progressAnimator(layout_progress);
				input_layout.setVisibility(View.INVISIBLE);

			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});

	}

	/**
	 * 出现进度动画
	 * 
	 * @param view
	 */
	private void progressAnimator(final View view) {
		PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f);
		PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1f);
		ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view, animator, animator2);
		animator3.setDuration(1000);
		animator3.setInterpolator(new JellyInterpolator());
		animator3.start();

	}

	class JellyInterpolator extends LinearInterpolator {
		private float factor;

		public JellyInterpolator() {
			this.factor = 0.15f;
		}

		@Override
		public float getInterpolation(float input) {
			return (float) (Math.pow(2, -10 * input) * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
		}
	}
}
