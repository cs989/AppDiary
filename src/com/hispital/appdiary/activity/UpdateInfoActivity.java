package com.hispital.appdiary.activity;

import com.hispital.appdiary.R;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

public class UpdateInfoActivity extends BaseActivity {
	
	//返回按钮
	@ViewInject(R.id.info_add_iv_back)
	ImageView info_add_iv_back;
	
	//保存按钮
	@ViewInject(R.id.info_add_iv_save)
	ImageView info_add_iv_save;
	
	//标题
	@ViewInject(R.id.info_add_et_title)
	EditText info_add_et_title;
	
	//上传图片
	@ViewInject(R.id.info_add_gv_images)
	GridView info_add_gv_images;
	
	//留言内容
	@ViewInject(R.id.msg_add_et_context)
	EditText msg_add_et_context;
	
	//留言提交按钮
	@ViewInject(R.id.msg_post_bt_context)
	Button msg_post_bt_context;
	
	//留言列表
	@ViewInject(R.id.msg_item_lv)
	ListView msg_item_lv;

	// 静态方法启动activity
	public static void startActivity(Context context) {
		Intent intent = new Intent(context, UpdateInfoActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.info_update_main;
	}

	@Override
	protected void initParams() {
		// TODO Auto-generated method stub

	}

}
