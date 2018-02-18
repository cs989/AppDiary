package com.hispital.appdiary.entity;

/**
 * 新闻条目
 * 
 * @author blue
 */
public class InfoItem {
	// id
	public int rid;
	// 病人id
	public int pid;
	// 病人头像
	public String purl;
	// 用户
	public int uid;
	// 标题
	public String title;
	// 内容
	public String content;
	// 创建时间
	public String ftime;
	// 消息集合
	public String msg_list;
	// 图片集合
	public String image_list;
	
	public int msg_count;
}
