package com.hispital.appdiary.fragment;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.hispital.appdiary.R;
import com.hispital.appdiary.activity.LoadingActivity;
import com.hispital.appdiary.activity.LoginActivity;
import com.hispital.appdiary.activity.MainActivity;
import com.hispital.appdiary.adapter.UserItemAdapter;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.entity.UserItem;
import com.hispital.appdiary.util.AppPreferences;
import com.hispital.appdiary.util.AppPreferences.PreferenceKey;
import com.hispital.appdiary.util.ConstantsUtil;
import com.hispital.appdiary.util.JListKit;
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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * 我的
 * 
 * @author blue
 */
public class SelfFragment extends BaseFragment {

	@ViewInject(R.id.self_iv_back)
	ImageView self_iv_back;

	@ViewInject(R.id.self_title)
	TextView self_title;

	@ViewInject(R.id.self_iv_save)
	ImageView self_iv_save;

	@ViewInject(R.id.self_image_lv)
	ListView self_image_lv;

	@ViewInject(R.id.self_et_name)
	EditText self_et_name;

	@ViewInject(R.id.self_et_lname)
	EditText self_et_lname;

	@ViewInject(R.id.self_et_tel)
	EditText self_et_tel;

	// 男
	@ViewInject(R.id.male_rb)
	RadioButton male_rb;

	// 女
	@ViewInject(R.id.famale_rb)
	RadioButton famale_rb;

	@ViewInject(R.id.self_et_password)
	EditText self_et_password;

	@ViewInject(R.id.self_et_confirmpsd)
	EditText self_et_confirmpsd;

	@ViewInject(R.id.self_bt_exit)
	Button self_bt_exit;

	@ViewInject(R.id.self_ly_lname)
	LinearLayout self_ly_lname;

	@ViewInject(R.id.self_view_lname)
	View self_view_lname;

	private final int IMAGE_OPEN = 1;
	private final int GET_DATA = 2;
	private final int TAKE_PHOTO = 3;
	private String pathImage;

	private List<UserItem> datasImage = JListKit.newArrayList();
	UserItem datas = new UserItem();
	private Uri imageUri;

	// 适配器
	private UserItemAdapter adapter;

	// 静态方法启动创建activity 注册
	public static void startActivity(Context context) {
		Intent intent = new Intent(context, SelfFragment.class);
		context.startActivity(intent);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.self_main;
	}

	@Override
	protected void initParams() {

		adapter = new UserItemAdapter(context, datasImage, self_image_lv);
		self_image_lv.setAdapter(adapter);

		self_image_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				AddImageDialog();
			}
		});

		if (AppPreferences.instance().getString(PreferenceKey.USER_ID).equals("")) {
			self_iv_back.setVisibility(View.VISIBLE);
			self_title.setText("注册");
			self_bt_exit.setVisibility(View.GONE);
			self_view_lname.setVisibility(View.VISIBLE);
			self_ly_lname.setVisibility(View.VISIBLE);
		} else {
			self_iv_back.setVisibility(View.GONE);
			self_title.setText("我的");
			self_bt_exit.setVisibility(View.VISIBLE);
			self_view_lname.setVisibility(View.GONE);
			self_ly_lname.setVisibility(View.GONE);
			loadUserDataFromNet();
		}

	}

	public void loadUserData(UserItem item) {
		self_et_name.setText(item.name);
		self_et_lname.setText(item.lname);
		if (item.sex.equals("男"))
			male_rb.setChecked(true);
		else
			famale_rb.setChecked(true);
		self_et_password.setText(item.password);
		self_et_confirmpsd.setText(item.password);
		self_et_tel.setText(item.tel);
	}

	public void loadUserDataFromNet() {

		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", AppPreferences.instance().getString(PreferenceKey.USER_ID));
		LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "getUserByUid",
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						// String list =
						// JSONObject.parseObject(arg0.result).getString("list");
						UserItem tmp = JSONObject.parseObject(arg0.result, UserItem.class);
						if (tmp != null) {
							datas = tmp;
							datasImage.clear();
							datasImage.add(tmp);
							adapter.refreshDatas(datasImage);
							loadUserData(datas);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						ToastMaker.showShortToast("数据返回失败");
					}

				});
	}

	@OnClick({ R.id.self_iv_back, R.id.self_iv_save, R.id.self_iv_img, R.id.self_bt_exit })
	public void viewOnClick(View view) {
		switch (view.getId()) {
		// 后退
		case R.id.self_iv_back:
			Activity activity = (Activity) context;
			activity.finish();
			break;
		// 保存
		case R.id.self_iv_save:
			DialogMaker.showCommonAlertDialog(context, "", "请选择", new String[] { "保存", "取消" }, new DialogCallBack() {
				@Override
				public void onButtonClicked(Dialog dialog, int position, Object tag) {
					// TODO Auto-generated method stub
					switch (position) {
					case 0:
						updateUser();
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
		case R.id.self_iv_img:
			AddImageDialog();
			break;
		case R.id.self_bt_exit:
			AppPreferences.instance().remove(PreferenceKey.USER_ID);
			AppPreferences.instance().remove(PreferenceKey.PRO_ID);
			LoginActivity.startActivity(context);
			((Activity) context).finish();
			break;
		default:
			break;
		}
	}

	protected boolean updateUser() {
		String uid = AppPreferences.instance().getString(PreferenceKey.USER_ID);
		RequestParams params = new RequestParams();
		boolean isImage = false;
		String netPath = "";
		String httpmethod = "updateUserByUid";
		String path = datas.uurl;
		if (datas.bmp != null) {
			isImage = true;
			httpmethod = "updateUserByUidImage";
			params.addBodyParameter("file0", new File(datas.uurl));
		} else {
			netPath = datas.uurl;
		}

		if (male_rb.isChecked())
			datas.sex = "男";
		else
			datas.sex = "女";
		if (JStringKit.isNotEmpty(uid)) {
			params.addBodyParameter("uid", uid + "");
		} else {
			if (isImage)
				httpmethod = "createUserImage";
			else
				httpmethod = "createUser";
		}
		params.addBodyParameter("name", self_et_name.getText().toString());
		params.addBodyParameter("sex", datas.sex);
		params.addBodyParameter("tel", self_et_tel.getText().toString());
		params.addBodyParameter("lname", self_et_lname.getText().toString());
		params.addBodyParameter("password", self_et_password.getText().toString());

		params.addBodyParameter("uurl", netPath);

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
						ToastMaker.showShortToast("保存成功");
					}
				});

		return false;
	}

	protected void AddImageDialog() {

		DialogMaker.showCommonAlertDialog(context, "", "请选择", new String[] { "相册", "拍摄", "取消" }, new DialogCallBack() {

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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 打开图片
		if (resultCode == ((Activity) context).RESULT_OK && requestCode == IMAGE_OPEN) {
			Uri uri = data.getData();
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				// 查询选择图片
				Cursor cursor = context.getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA },
						null, null, null);
				// 返回 没找到选择图片
				if (null == cursor) {
					return;
				}
				// 光标移动至开头 获取图片路径
				cursor.moveToFirst();
				pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			}
		} // end if 打开图片
		if (resultCode == ((Activity) context).RESULT_OK && requestCode == GET_DATA) {
			pathImage = imageUri.toString();
		}
		// ����
		if (resultCode == ((Activity) context).RESULT_OK && requestCode == TAKE_PHOTO) {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(imageUri, "image/*");
			intent.putExtra("scale", true);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intentBc.setData(imageUri);
			context.sendBroadcast(intentBc);
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
	public void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(pathImage)) {
			Bitmap addbmp = BitmapFactory.decodeFile(pathImage);
			datas.bmp = addbmp;
			datas.uurl = pathImage;
			// datasImage.clear();
			// UserItem item = new UserItem();
			// item.bmp = addbmp;
			// item.uurl=pathImage;
			// datasImage.add(item);
			adapter.refreshDatas(datasImage);
		}
	}
}
