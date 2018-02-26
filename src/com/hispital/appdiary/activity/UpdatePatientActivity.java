package com.hispital.appdiary.activity;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.hispital.appdiary.R;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.cache.AsyncImageLoader;
import com.hispital.appdiary.entity.PatientItem;
import com.hispital.appdiary.util.AppPreferences;
import com.hispital.appdiary.util.AppPreferences.PreferenceKey;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.DisplayUtil;
import com.hispital.appdiary.util.JStringKit;
import com.hispital.appdiary.view.DialogMaker;
import com.hispital.appdiary.view.DialogMaker.DialogCallBack;
import com.hispital.appdiary.view.ToastMaker;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class UpdatePatientActivity extends BaseActivity {

	// 返回按钮
	@ViewInject(R.id.patient_iv_back)
	ImageView patient_iv_back;

	// 页面标题
	@ViewInject(R.id.patient_title)
	TextView patient_title;

	// 保存按钮
	@ViewInject(R.id.patient_iv_save)
	ImageView patient_iv_save;

	// 头像
	@ViewInject(R.id.patient_iv_img)
	ImageView patient_iv_img;

	// 入院编号
	@ViewInject(R.id.patient_et_no)
	EditText patient_et_no;

	// 姓名
	@ViewInject(R.id.patient_et_name)
	EditText patient_et_name;

	// 男
	@ViewInject(R.id.male_rb)
	RadioButton male_rb;

	// 女
	@ViewInject(R.id.famale_rb)
	RadioButton famale_rb;

	// 联系电话
	@ViewInject(R.id.patient_et_phone)
	EditText patient_et_phone;

	// 出生日期
	@ViewInject(R.id.patient_tv_birthday)
	TextView patient_tv_birthday;

	// 出生日期选择
	@ViewInject(R.id.patient_bt_birthday)
	Button patient_bt_birthday;

	// 病情简介
	@ViewInject(R.id.patient_et_context)
	EditText patient_et_context;

	private final int IMAGE_OPEN = 1;
	private final int GET_DATA = 2;
	private final int TAKE_PHOTO = 3;
	private String pathImage;
	private boolean isUpdate = false;
	private String uid = AppPreferences.instance().getString(PreferenceKey.USER_ID);
	private String pro_id = AppPreferences.instance().getString(PreferenceKey.PRO_ID);

	PatientItem datas = new PatientItem();

	private Uri imageUri;
	private String pid;

	int mYear = 1980, mMonth = 0, mDay = 1;
	final int DATE_DIALOG = 1;

	// 静态方法启动创建activity
	public static void startActivity(Context context) {
		Intent intent = new Intent(context, UpdatePatientActivity.class);
		context.startActivity(intent);
	}

	// 静态方法启动修改activity
	public static void startActivity(Context context, String pid, boolean isUpdate) {
		Intent intent = new Intent(context, UpdatePatientActivity.class);
		intent.putExtra("pid", pid);
		intent.putExtra("isUpdate", isUpdate);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		pid = getIntent().getStringExtra("pid");
		isUpdate = getIntent().getBooleanExtra("isUpdate", false);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 防止下面输入框无法获取焦点
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 禁止横屏幕
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.patient_main;
	}

	@Override
	protected void initParams() {
		// TODO Auto-generated method stub

		if (JStringKit.isEmpty(pid)) {
			patient_bt_birthday.setVisibility(View.VISIBLE);
			patient_iv_save.setVisibility(View.VISIBLE);
			patient_title.setText("新建");
			etIsWrite(true);
		} else {
			if (isUpdate) {
				patient_bt_birthday.setVisibility(View.VISIBLE);
				patient_iv_save.setVisibility(View.VISIBLE);
				patient_title.setText("编辑");
				etIsWrite(true);
				loadPatientDataFromNet();
			} else {
				patient_bt_birthday.setVisibility(View.GONE);
				patient_iv_save.setVisibility(View.GONE);
				patient_title.setText("查看");
				etIsWrite(false);
				loadPatientDataFromNet();
			}
		}
	}

	public void etIsWrite(boolean isWrite) {
		patient_et_name.setFocusable(isWrite);
		patient_et_phone.setFocusable(isWrite);
		patient_et_no.setFocusable(isWrite);
		patient_et_context.setFocusable(isWrite);
		patient_et_name.setFocusableInTouchMode(isWrite);
		patient_et_phone.setFocusableInTouchMode(isWrite);
		patient_et_no.setFocusableInTouchMode(isWrite);
		patient_et_context.setFocusableInTouchMode(isWrite);
	}

	public void loadPatientData(PatientItem item) {
		patient_et_no.setText(item.pno);
		patient_et_name.setText(item.name);
		if (item.sex.equals("男"))
			male_rb.setChecked(true);
		else
			famale_rb.setChecked(true);
		patient_et_phone.setText(item.tel);
		patient_tv_birthday.setText(item.birthday.substring(0, 11));
		patient_et_context.setText(item.pcondition);

		if (!(item.purl == null || item.purl.equals(""))) {

			patient_iv_img.setTag(ConstantsUtil.IMAGE_URL + item.purl);
			AsyncImageLoader.getInstance(this).loadBitmaps(null, patient_iv_img, ConstantsUtil.IMAGE_URL + item.purl,
					DisplayUtil.dip2px(this, 105), DisplayUtil.dip2px(this, 70));
		} else {
			// 异步加载单张图片如果为空会报错，所以从资源文件中加载
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.empty_photo);
			patient_iv_img.setImageBitmap(bmp);
		}

	}

	public void loadPatientDataFromNet() {

		RequestParams params = new RequestParams();
		// params.addBodyParameter("uid", uid);
		params.addBodyParameter("pid", pid);
		LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "getPatientByPid",
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						// String list =
						// JSONObject.parseObject(arg0.result).getString("list");
						PatientItem tmp = JSONObject.parseObject(arg0.result, PatientItem.class);
						if (tmp != null) {
							datas = tmp;
							loadPatientData(datas);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						ToastMaker.showShortToast("数据返回失败");
					}

				});
	}

	@OnClick({ R.id.patient_iv_back, R.id.patient_iv_save, R.id.patient_bt_birthday, R.id.patient_iv_img })
	public void viewOnClick(View view) {
		switch (view.getId()) {
		// 后退
		case R.id.patient_iv_back:
			finish();
			break;
		// 保存
		case R.id.patient_iv_save:
			DialogMaker.showCommonAlertDialog(this, "", "请选择", new String[] { "保存", "取消" }, new DialogCallBack() {
				@Override
				public void onButtonClicked(Dialog dialog, int position, Object tag) {
					// TODO Auto-generated method stub
					switch (position) {
					case 0:
						updatePatient();
						ToastMaker.showShortToast("正在上传");
						break;
					case 1:
						dialog.dismiss();
						break;
					default:
						break;
					}
				}

				@Override
				public void onCancelDialog(Dialog dialog, Object tag) {
					return;
				}

			}, true, true, new Object());

			break;
		// 留言提交
		case R.id.patient_bt_birthday:
			showDialog(DATE_DIALOG);
			break;
		case R.id.patient_iv_img:
			AddImageDialog();
			break;
		default:
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG:
			return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
		}
		return null;
	}

	protected boolean updatePatient() {
		RequestParams params = new RequestParams();
		boolean isImage = false;
		String netPath = "";
		String httpmethod = "updatePatientByPid";
		String path = datas.purl;
		if (datas.bmp != null) {
			isImage = true;
			httpmethod = "updategPatientByPidImage";
			params.addBodyParameter("file0", new File(datas.purl));
		} else {
			netPath = datas.purl;
		}

		if (male_rb.isChecked())
			datas.sex = "男";
		else
			datas.sex = "女";
		if (JStringKit.isNotEmpty(pid)) {
			params.addBodyParameter("pid", pid + "");
		} else {
			if (isImage)
				httpmethod = "creategPatientImage";
			else
				httpmethod = "creategPatient";
		}
		params.addBodyParameter("pno", patient_et_no.getText().toString());
		params.addBodyParameter("name", patient_et_name.getText().toString());
		params.addBodyParameter("sex", datas.sex);
		params.addBodyParameter("tel", patient_et_phone.getText().toString());
		params.addBodyParameter("birthday", patient_tv_birthday.getText().toString());
		params.addBodyParameter("pcondition", patient_et_context.getText().toString());

		params.addBodyParameter("purl", netPath);

		LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + httpmethod, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 回送消息、
						ToastMaker.showShortToast("上传失败");
					}

					@Override
					public void onLoading(long total, long current, boolean isUploading) {
						super.onLoading(total, current, isUploading);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// 回送消息
						ToastMaker.showShortToast("上传成功");
						finish();
					}
				});

		return false;
	}

	protected void AddImageDialog() {

		DialogMaker.showCommonAlertDialog(this, "", "请选择", new String[] { "相册", "拍摄", "取消" }, new DialogCallBack() {

			@Override
			public void onButtonClicked(Dialog dialog, int position, Object tag) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					Intent intent = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, IMAGE_OPEN);
					break;
				case 1:
					File outputImage = new File(Environment.getExternalStorageDirectory(), "test.jpg");
					try {
						if (outputImage.exists()) {
							outputImage.delete();
						}
						outputImage.createNewFile();
					} catch (Exception e) {
						e.printStackTrace();
					}
					imageUri = Uri.fromFile(outputImage);
					System.out.println(imageUri);
					Intent intentPhoto = new Intent("android.media.action.IMAGE_CAPTURE");
					intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(intentPhoto, TAKE_PHOTO);
					break;
				case 2:
					dialog.dismiss();
					break;
				default:
					break;
				}
			}

			@Override
			public void onCancelDialog(Dialog dialog, Object tag) {
				return;
			}

		}, true, true, new Object());
	}

	// 获取图片路径 响应startActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 打开图片
		if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN) {
			Uri uri = data.getData();
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				// 查询选择图片
				Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null,
						null, null);
				// 返回 没找到选择图片
				if (null == cursor) {
					return;
				}
				// 光标移动至开头 获取图片路径
				cursor.moveToFirst();
				pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			}
		} // end if 打开图片
		if (resultCode == RESULT_OK && requestCode == GET_DATA) {
			pathImage = imageUri.toString();
		}
		// ����
		if (resultCode == RESULT_OK && requestCode == TAKE_PHOTO) {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(imageUri, "image/*");
			intent.putExtra("scale", true);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intentBc.setData(imageUri);
			this.sendBroadcast(intentBc);
			// //������������
			// Intent intentPut = new Intent(this, ProcessActivity.class);
			// //���->����
			// intentPut.putExtra("path", pathTakePhoto);
			// //startActivity(intent);
			startActivityForResult(intent, GET_DATA);
		}
	}

	// 刷新图片
	@Override
	protected void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(pathImage)) {
			Bitmap addbmp = BitmapFactory.decodeFile(pathImage);
			patient_iv_img.setImageBitmap(addbmp);
			datas.bmp = addbmp;
			datas.purl = pathImage;
			pathImage = null;
		}
	}

	/**
	 * 设置日期 利用StringBuffer追加
	 */
	public void display() {
		patient_tv_birthday
				.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
	}

	private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			display();
		}
	};
}
