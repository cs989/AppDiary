package com.hispital.appdiary.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.hispital.appdiary.R;
import com.hispital.appdiary.fragment.FocusFragment;
import com.hispital.appdiary.fragment.InfoFragment;
import com.hispital.appdiary.fragment.SelfFragment;
import com.hispital.appdiary.view.ToastMaker;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
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
        simpleAdapter = new SimpleAdapter(this, 
        		imageItem, R.layout.griditem_addpic, 
                new String[] { "itemImage"}, new int[] { R.id.info_add_gv_images}); 
        
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
         
         /*
          * ����GridView����¼�
          * ����:�ú���������󷽷� ����Ҫ�ֶ�����import android.view.View;
          */
        info_add_gv_images.setOnItemClickListener(new OnItemClickListener() {
   			@Override
 			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
 			{
   				if( imageItem.size() == 10) { //��һ��ΪĬ��ͼƬ
   				}
   				else if(position == 0) { //���ͼƬλ��Ϊ+ 0��Ӧ0��ͼƬ
   					//Toast.makeText(MainActivity.this, "���ͼƬ", Toast.LENGTH_SHORT).show();
   					AddImageDialog();
   				}
   				else {
   					DeleteDialog(position);
   					//Toast.makeText(MainActivity.this, "�����" + (position + 1) + " ��ͼƬ", 
   					//		Toast.LENGTH_SHORT).show();
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
    		if(imageItem.size()==1) {
    			ToastMaker.showShortToast("");
    			Toast.makeText(UpdateInfoActivity.this, "û��ͼƬ��Ҫ�ϴ�", Toast.LENGTH_SHORT).show();
    			return;
    		}
    		
			//��Ϣ��ʾ
			Toast.makeText(UpdateInfoActivity.this, "�����ɹ�", Toast.LENGTH_SHORT).show();
			break;
		// 留言提交
		case R.id.msg_post_bt_context:

			break;

		default:
			break;
		}
	}
	
	  protected void AddImageDialog() {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(UpdateInfoActivity.this);
	    	builder.setTitle("���ͼƬ");
	    	builder.setIcon(R.drawable.ic_launcher);
	    	builder.setCancelable(false); //����Ӧback��ť
	    	builder.setItems(new String[] {"�������ѡ��","�ֻ�������","ȡ��ѡ��ͼƬ"}, 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch(which) {
						case 0: //�������
							dialog.dismiss();
							Intent intent = new Intent(Intent.ACTION_PICK,       
			                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  
			                startActivityForResult(intent, IMAGE_OPEN);  
			                //ͨ��onResume()ˢ������
							break;
						case 1: //�ֻ����
							dialog.dismiss();
							File outputImage = new File(Environment.getExternalStorageDirectory(), "suishoupai_image.jpg");
							pathTakePhoto = outputImage.toString();
							try {
								if(outputImage.exists()) {
									outputImage.delete();
								}
								outputImage.createNewFile();
							} catch(Exception e) {
								e.printStackTrace();
							}
							imageUri = Uri.fromFile(outputImage);
							Intent intentPhoto = new Intent("android.media.action.IMAGE_CAPTURE"); //����
							intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
							startActivityForResult(intentPhoto, TAKE_PHOTO);
							break;
						case 2: //ȡ�����
							dialog.dismiss();
							break;
						default:
							break;
						}
					}
				});
	    	//��ʾ�Ի���
	    	builder.create().show();
	    }
	  
	  protected void DeleteDialog(final int position) {
	    	AlertDialog.Builder builder = new Builder(UpdateInfoActivity.this);
	    	builder.setMessage("ȷ���Ƴ������ͼƬ��");
	    	builder.setTitle("��ʾ");
	    	builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
	    		@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    			dialog.dismiss();
	    			imageItem.remove(position);
	    	        simpleAdapter.notifyDataSetChanged();
	    		}
	    	});
	    	builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
	    		@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    			dialog.dismiss();
	    			}
	    		});
	    	builder.create().show();
	    }

}
