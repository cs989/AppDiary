package com.hispital.appdiary.activity;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.hispital.appdiary.R;
import com.hispital.appdiary.adapter.ImageItemAdapter;
import com.hispital.appdiary.application.LocalApplication;
import com.hispital.appdiary.entity.ImageItem;
import com.hispital.appdiary.entity.InfoItem;
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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UpdateInfoActivity extends BaseActivity {

	// 返回按钮
	@ViewInject(R.id.info_add_iv_back)
	ImageView info_add_iv_back;
	
	//页面标题
	@ViewInject(R.id.info_title)
	TextView info_title;
	

	// 保存按钮
	@ViewInject(R.id.info_add_iv_save)
	ImageView info_add_iv_save;

	// 标题
	@ViewInject(R.id.info_add_et_title)
	EditText info_add_et_title;

	// 内容
	@ViewInject(R.id.info_add_et_context)
	EditText info_add_et_context;

	// 上传图片
	@ViewInject(R.id.info_add_gv_images)
	GridView info_add_gv_images;

	// 留言列表
	@ViewInject(R.id.msg_item_lv)
	ListView msg_item_lv;

	private final int IMAGE_OPEN = 1;
	private final int GET_DATA = 2;
	private final int TAKE_PHOTO = 3;
	private String pathImage;
	private String pathTakePhoto;
	// private ArrayList<HashMap<String, Object>> imageItem;

	List<ImageItem> datas = JListKit.newArrayList();

	private Uri imageUri;
	private Bitmap bmp;

	// private SimpleAdapter simpleAdapter;

	// 适配器
	private ImageItemAdapter adapter;

	private String rid;

	// 静态方法启动activity
	public static void startActivity(Context context) {
		Intent intent = new Intent(context, UpdateInfoActivity.class);
		context.startActivity(intent);
	}

	// 静态方法启动activity
	public static void startActivity(Context context, String rid) {
		Intent intent = new Intent(context, UpdateInfoActivity.class);
		intent.putExtra("rid", rid);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		rid = getIntent().getStringExtra("rid");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 防止下面输入框无法获取焦点
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 禁止横屏幕
	}

	@Override
	protected int getLayoutId() {
		return R.layout.info_update_main;
	}

	@Override
	protected void initParams() {
		// 默认图片加载数据
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic);
		ImageItem item = new ImageItem();
		item.iurl = "add_pic";
		item.bmp = bmp;
		datas.add(item);

		adapter = new ImageItemAdapter(this, datas, info_add_gv_images);

		// imageItem = new ArrayList<HashMap<String, Object>>();
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("itemImage", bmp);
		// map.put("pathImage", "add_pic");
		// imageItem.add(map);
		// simpleAdapter = new SimpleAdapter(this, imageItem,
		// R.layout.griditem_addpic, new String[] { "itemImage" },
		// new int[] { R.id.info_add_gv_images });
		//
		// simpleAdapter.setViewBinder(new ViewBinder() {
		// @Override
		// public boolean setViewValue(View view, Object data, String
		// textRepresentation) {
		// // TODO Auto-generated method stub
		// if (view instanceof ImageView && data instanceof Bitmap) {
		// ImageView i = (ImageView) view;
		// i.setImageBitmap((Bitmap) data);
		// return true;
		// }
		// return false;
		// }
		// });
		info_add_gv_images.setAdapter(adapter);
		// info_add_gv_images.setAdapter(simpleAdapter);

		// gridview添加点击事件
		info_add_gv_images.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (datas.size() == 8) {
					ToastMaker.showShortToast("最多添加8张图片");
				} else if (position == 0) {
					// Toast.makeText(MainActivity.this, "���ͼƬ",
					// Toast.LENGTH_SHORT).show();
					AddImageDialog();
				} else {
					DeleteDialog(position);
					// Toast.makeText(MainActivity.this, "�����" + (position +
					// 1) + " ��ͼƬ",
					// Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (JStringKit.isEmpty(rid)) {
			info_title.setText("新建记录");
			adapter.refreshDatas(datas);
		} else {
			info_title.setText("编辑记录");
			loadInfoData();
		}
	}

	public void loadInfoData() {

		RequestParams params = new RequestParams();
		params.addBodyParameter("rid", rid);
		LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "getImageByRid",
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						// String list =
						// JSONObject.parseObject(arg0.result).getString("list");
						List<ImageItem> tmp = JSONObject.parseArray(arg0.result, ImageItem.class);
						if (JListKit.isNotEmpty(tmp)) {
							datas.addAll(tmp);
						}
						adapter.refreshDatas(datas);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						ToastMaker.showShortToast("数据返回失败");
					}

				});

		LocalApplication.getInstance().httpUtils.send(HttpMethod.POST, ConstantsUtil.SERVER_URL + "getRecordByRid",
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// String list =
						// JSONObject.parseObject(arg0.result).getString("list");
						InfoItem tmp = JSONObject.parseObject(arg0.result, InfoItem.class);
						if (tmp != null) {
							info_add_et_title.setText(tmp.title);
							info_add_et_context.setText(tmp.content);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						ToastMaker.showShortToast("数据返回失败");
					}
				});

	}

	@OnClick({ R.id.info_add_iv_back, R.id.info_add_iv_save, R.id.msg_post_bt_context })
	public void viewOnClick(View view) {
		switch (view.getId()) {
		// 后退
		case R.id.info_add_iv_back:
			finish();
			break;
		// 保存
		case R.id.info_add_iv_save:
			DialogMaker.showCommonAlertDialog(this, "", "请选择", new String[] { "保存", "取消" }, new DialogCallBack() {
				@Override
				public void onButtonClicked(Dialog dialog, int position, Object tag) {
					// TODO Auto-generated method stub
					switch (position) {
					case 0:
						updateImage();
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
		case R.id.msg_post_bt_context:

			break;

		default:
			break;
		}
	}

	protected boolean updateImage() {
		if (datas.size() == 0) {
			return false;
		}

		RequestParams params = new RequestParams();
		// Iterator<HashMap<String, Object>> iterator = imageItem.iterator();
		// int i = 0;
		// while (iterator.hasNext()) {
		// String path = String.valueOf(iterator.next().get("pathImage"));
		// if (!path.equals("add_pic")) {
		// params.addBodyParameter("file" + i++, new File(path));
		// }
		// }

		String netPath = "";
		int i = 0;
		String httpmethod = "updateRecord";
		for (ImageItem item : datas) {
			String path = item.iurl;
			if (!path.equals("add_pic")) {
				if (item.bmp == null && !item.iurl.contains("http"))
					netPath = netPath + item.iurl + ",";
				else {
					httpmethod = "updateRecordImage";
					params.addBodyParameter("file" + i++, new File(item.iurl));
				}
			}
		}

		if (JStringKit.isNotEmpty(rid))
			params.addBodyParameter("rid", rid + "");
		params.addBodyParameter("title", info_add_et_title.getText().toString());
		params.addBodyParameter("content", info_add_et_context.getText().toString());
		params.addBodyParameter("netPath", netPath);

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
					pathTakePhoto = outputImage.toString();
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

					// 调用系统相机拍照

					// File file = new
					// File(Environment.getExternalStorageDirectory().getPath()+"/appdiary/image/");
					// //是否是文件夹，不是就创建文件夹
					// if (!file.exists()) file.mkdirs();
					// SimpleDateFormat timeStampFormat =new
					// SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
					// //指定保存路径
					// pathTakePhoto =
					// Environment.getExternalStorageDirectory().getPath()+"/appdiary/image/"
					// +
					// timeStampFormat.format(new Date()) + ".jpg";
					// System.out.println(pathTakePhoto);
					// Intent intentFromCapture = new
					// Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// startActivityForResult(intentFromCapture, TAKE_PHOTO);
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
			ImageItem item = new ImageItem();
			item.bmp = addbmp;
			item.iurl = pathImage;
			datas.add(item);
			// HashMap<String, Object> map = new HashMap<String, Object>();
			// map.put("itemImage", addbmp);
			// map.put("pathImage", pathImage);
			// imageItem.add(map);
			adapter.refreshDatas(datas);
			// simpleAdapter = new SimpleAdapter(this, imageItem,
			// R.layout.griditem_addpic, new String[] { "itemImage" },
			// new int[] { R.id.imageView1 });
			// simpleAdapter.setViewBinder(new ViewBinder() {
			// @Override
			// public boolean setViewValue(View view, Object data, String
			// textRepresentation) {
			// // TODO Auto-generated method stub
			// if (view instanceof ImageView && data instanceof Bitmap) {
			// ImageView i = (ImageView) view;
			// i.setImageBitmap((Bitmap) data);
			// return true;
			// }
			// return false;
			// }
			// });
			// info_add_gv_images.setAdapter(simpleAdapter);
			// simpleAdapter.notifyDataSetChanged();
			// 刷新后释放防止手机休眠后自动添加
			pathImage = null;
		}
	}

	protected void DeleteDialog(final int position) {
		AlertDialog.Builder builder = new Builder(UpdateInfoActivity.this);
		builder.setMessage("确认要删除吗？");
		builder.setTitle("提示ʾ");
		builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				datas.remove(datas.get(position));
				adapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

}
