package com.hispital.appdiary.activity;

import com.alibaba.fastjson.JSONObject;
import com.hispital.appdiary.R;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.entity.UserItem;
import com.hispital.appdiary.util.AppPreferences;
import com.hispital.appdiary.util.AppPreferences.PreferenceKey;
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
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
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

	@ViewInject(R.id.login_iv_exit)
	ImageView login_iv_exit;

	@ViewInject(R.id.login_tv_register)
	TextView login_tv_register;

	private float mWidth, mHeight;
	private ObjectAnimator animator3;

	public static void startActivity(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected int getLayoutId() {

		return R.layout.login_activity_main;
	}

	@Override
	protected void initParams() {
		// TODO Auto-generated method stub

	}

	@OnClick({ R.id.main_btn_login, R.id.login_tv_register, R.id.login_iv_exit })
	public void viewOnClick(View view) {
		switch (view.getId()) {
		// 登录
		case R.id.main_btn_login:
			if (input_et_username.getText().toString().equals("") || input_et_psw.getText().toString().equals("")) {
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
				params.addBodyParameter("username", input_et_username.getText().toString());
				params.addBodyParameter("password", input_et_psw.getText().toString());
				LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "loginCheck",
						params, new RequestCallBack<String>() {

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								UserItem tmp = null;
								try {
									tmp = JSONObject.parseObject(arg0.result, UserItem.class);
								} catch (Exception ex) {

								}
								if (tmp != null) {
									AppPreferences.instance().remove(PreferenceKey.USER_ID);
									AppPreferences.instance().remove(PreferenceKey.PRO_ID);
									AppPreferences.instance().putString(PreferenceKey.USER_ID, tmp.uid + "");
									AppPreferences.instance().putString(PreferenceKey.PRO_ID, tmp.pid + "");
									MainActivity.startActivity(LoginActivity.this);
									finish();
								} else {
									//休眠2s不然显示有问题
									Handler handler = new Handler();
									handler.postDelayed(new Runnable() {
										@Override
										public void run() {
											recovery();
											ToastMaker.showShortToast("用户名或密码错误");
										}
									}, 2000);

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
		case R.id.login_tv_register:
			RegisterActivity.startActivity(this);
			break;
		case R.id.login_iv_exit:
			this.finish();
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
		animator3 = ObjectAnimator.ofPropertyValuesHolder(view, animator, animator2);
		animator3.setDuration(1000);
		animator3.setInterpolator(new JellyInterpolator());
		animator3.start();
	}

	private void recovery() {
		layout_progress.setVisibility(View.GONE);
		input_layout.setVisibility(View.VISIBLE);
		input_layout_name.setVisibility(View.VISIBLE);
		input_layout_psw.setVisibility(View.VISIBLE);
		ViewGroup.MarginLayoutParams params = (MarginLayoutParams) input_layout.getLayoutParams();
		params.leftMargin = 0;
		params.rightMargin = 0;
		input_layout.setLayoutParams(params);

		ObjectAnimator animator2 = ObjectAnimator.ofFloat(input_layout, "scaleX", 0.5f, 1f);
		animator2.setDuration(500);
		animator2.setInterpolator(new AccelerateDecelerateInterpolator());
		animator2.start();
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
