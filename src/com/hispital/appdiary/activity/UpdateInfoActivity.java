package com.hispital.appdiary.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.hispital.appdiary.R;
import com.hispital.appdiary.view.DialogMaker;
import com.hispital.appdiary.view.DialogMaker.DialogCallBack;
import com.hispital.appdiary.view.ToastMaker;
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
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

public class UpdateInfoActivity extends BaseActivity {

	// 返回按钮
	@ViewInject(R.id.info_add_iv_back)
	ImageView info_add_iv_back;

	// 保存按钮
	@ViewInject(R.id.info_add_iv_save)
	ImageView info_add_iv_save;

	// 标题
	@ViewInject(R.id.info_add_et_title)
	EditText info_add_et_title;

	// 上传图片
	@ViewInject(R.id.info_add_gv_images)
	GridView info_add_gv_images;

	// 留言内容
	@ViewInject(R.id.msg_add_et_context)
	EditText msg_add_et_context;

	// 留言提交按钮
	@ViewInject(R.id.msg_post_bt_context)
	Button msg_post_bt_context;

	// 留言列表
	@ViewInject(R.id.msg_item_lv)
	ListView msg_item_lv;

	private final int IMAGE_OPEN = 1;
	private final int GET_DATA = 2;
	private final int TAKE_PHOTO = 3;
	private String pathImage;
	private String pathTakePhoto;
	private ArrayList<HashMap<String, Object>> imageItem;

	private Uri imageUri;
	private Bitmap bmp;

	private SimpleAdapter simpleAdapter;

	// 静态方法启动activity
	public static void startActivity(Context context) {
		Intent intent = new Intent(context, UpdateInfoActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 防止下面输入框无法获取焦点
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 禁止横屏幕
	}

	@Override
	protected int getLayoutId() {
		return R.layout.info_update_main;
	}

	@Override
	protected void initParams() {
		// TODO Auto-generated method stub
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic);
		imageItem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("itemImage", bmp);
		map.put("pathImage", "add_pic");
		imageItem.add(map);
		simpleAdapter = new SimpleAdapter(this, imageItem, R.layout.griditem_addpic, new String[] { "itemImage" },
				new int[] { R.id.info_add_gv_images });

		simpleAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				// TODO Auto-generated method stub
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView i = (ImageView) view;
					i.setImageBitmap((Bitmap) data);
					return true;
				}
				return false;
			}
		});
		info_add_gv_images.setAdapter(simpleAdapter);

		// gridview添加点击事件
		info_add_gv_images.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (imageItem.size() == 10) {
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
			if (imageItem.size() == 1) {
				ToastMaker.showShortToast("");
				return;
			}

			ToastMaker.showShortToast("");
			break;
		// 留言提交
		case R.id.msg_post_bt_context:

			break;

		default:
			break;
		}
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
	
    //获取图片路径 响应startActivityForResult    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    
        super.onActivityResult(requestCode, resultCode, data);          
        //打开图片    
        if(resultCode==RESULT_OK && requestCode==IMAGE_OPEN) {          
            Uri uri = data.getData();    
            if (!TextUtils.isEmpty(uri.getAuthority())) {    
                //查询选择图片    
                Cursor cursor = getContentResolver().query(    
                        uri,    
                        new String[] { MediaStore.Images.Media.DATA },    
                        null,     
                        null,     
                        null);    
                //返回 没找到选择图片    
                if (null == cursor) {    
                    return;    
                }    
                //光标移动至开头 获取图片路径    
                cursor.moveToFirst();    
                pathImage = cursor.getString(cursor    
                        .getColumnIndex(MediaStore.Images.Media.DATA));    
            }  
        }  //end if 打开图片  
        if(resultCode==RESULT_OK && requestCode==GET_DATA) { 
            pathImage = data.getStringExtra("pathProcess");
        }
        //����
        if(resultCode==RESULT_OK && requestCode==TAKE_PHOTO) {  
        	 pathImage = pathTakePhoto;
//        	Intent intent = new Intent("com.android.camera.action.CROP"); //����  
//            intent.setDataAndType(imageUri, "image/*"); 
//            intent.putExtra("scale", true);  
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);  
//            //�㲥ˢ�����   
//            Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);  
//            intentBc.setData(imageUri);       
//            this.sendBroadcast(intentBc);      
//            //������������
//			Intent intentPut = new Intent(this, ProcessActivity.class); //���->����
//			intentPut.putExtra("path", pathTakePhoto);
//			//startActivity(intent);
//			startActivityForResult(intentPut, GET_DATA);
        }
    }  
	
	//刷新图片  
    @Override  
    protected void onResume() {  
        super.onResume();  
        if(!TextUtils.isEmpty(pathImage)){  
            Bitmap addbmp=BitmapFactory.decodeFile(pathImage);  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            map.put("itemImage", addbmp);  
            imageItem.add(map);  
            simpleAdapter = new SimpleAdapter(this,   
                    imageItem, R.layout.griditem_addpic,   
                    new String[] { "itemImage"}, new int[] { R.id.imageView1});   
            simpleAdapter.setViewBinder(new ViewBinder() {    
                @Override    
                public boolean setViewValue(View view, Object data,    
                        String textRepresentation) {    
                    // TODO Auto-generated method stub    
                    if(view instanceof ImageView && data instanceof Bitmap){    
                        ImageView i = (ImageView)view;    
                        i.setImageBitmap((Bitmap) data);    
                        return true;    
                    }    
                    return false;    
                }  
            });   
            info_add_gv_images.setAdapter(simpleAdapter);  
            simpleAdapter.notifyDataSetChanged();  
            //刷新后释放防止手机休眠后自动添加  
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
				imageItem.remove(position);
				simpleAdapter.notifyDataSetChanged();
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
