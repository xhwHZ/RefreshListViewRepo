package com.xhw.pull.refresh;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhw.pull.refresh.view.RefreshListView;
import com.xhw.pull.refresh.view.RefreshListView.OnLoadingMoreListener;
import com.xhw.pull.refresh.view.RefreshListView.OnRefreshingListener;

public class MainActivity extends Activity
{

	// 数据集合
	private List<String> dataList;

	// 自定义的下拉刷新ListView
	private RefreshListView refreshListView;

	private RefreshListViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// 隐藏窗口的标题栏(必须在setContentView前面)
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		initData();
		initListener();
	}

	private void initListener()
	{
		//下拉刷新
		refreshListView.setOnRefreshingListener(new OnRefreshingListener() {
			
			//当下拉刷新控件进入刷新中的状态，此方法被调用
			@Override
			public void onRefreshing()
			{
				//模拟下拉刷新请求服务器数据
				pullRequestService();
			}

		});
		
		//加载更多
		refreshListView.setOnLoadingMoreListener(new OnLoadingMoreListener() {
			
			/**
			 * 当下拉刷新控件进入加载更多的状态，此方法被调用
			 */
			@Override
			public void onLoadingMore()
			{
				//模拟加载更多请求服务器数据
				loadingMoreRequestService();
			}
		});
	}

	/**
	 * 模拟加载更多请求服务器获取数据的方法
	 */
	private void loadingMoreRequestService()
	{
		new Thread(){
			public void run() {
				SystemClock.sleep(2000);//模拟连接网络下载数据的时间
				dataList.add(new String("加载更多的数据"));//数据加到ListView的底部
				//请求完毕
				runOnUiThread(new Runnable() {
					
					@Override
					public void run()
					{
						//更新Adapter
						adapter.notifyDataSetChanged();
						//重置底部View状态
						refreshListView.completeLoadingMore();
					}
				});
				
			}
			
		}.start();
	}
	
	
	/**
	 * 模拟下拉刷新请求服务器获取数据的方法
	 */
	private void pullRequestService()
	{
		new Thread(){
			public void run() {
				SystemClock.sleep(3000);//模拟连接网络下载数据的时间
				dataList.add(0,new String("下拉刷新的数据"));//数据加到ListView的顶部
				//请求完毕
				runOnUiThread(new Runnable() {
					
					@Override
					public void run()
					{
						//更新Adapter
						adapter.notifyDataSetChanged();
						//重置顶部View状态
						refreshListView.completeRefresh();
					}
				});
				
			}
			
		}.start();
	}
	
	private void initView()
	{
		refreshListView = (RefreshListView) this
				.findViewById(R.id.refreshListView);
	}

	private void initData()
	{
		dataList = new ArrayList<String>();
		for (int i = 0; i < 15; i++)
		{
			String str = "ListView原来的数据:" + i;
			dataList.add(str);
		}
		//#start 注释的操作已经内嵌入RefreshListView
		//注释的操作已经内嵌入RefreshListView
		// 这步是在onCreate方法中执行的，所以在下面拿高度是拿不到的，在它测量完成后，再去获取他的高度
		//View headerView = View.inflate(this, R.layout.refresh_listview_header,
				//null);
		// 方法1
		// //添加headerView的布局监听，一旦布局操作执行完了，测量操作铁定早执行完了，可以在这里拿到高度
		// headerView.getViewTreeObserver().addOnGlobalLayoutListener(new
		// OnGlobalLayoutListener() {
		//
		// @Override
		// public void onGlobalLayout()
		// {
		// //不及时remove会多次调用
		// headerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		// //设置HeaderView的padding-top为负的HeaderView的高度，达到隐藏它的目的
		// int headerViewHeight=headerView.getHeight();
		// headerView.setPadding(0, -headerViewHeight, 0, 0);
		// //给ListView添加一个头部，这个方法必须在setAdapter前调用()
		// refreshListView.addHeaderView(headerView);
		// }
		// });

		// 方法2：
		//headerView.measure(0, 0);// 强迫系统去测量该view，两个参数没意义，测量完成后可以拿到getMeasuredHeight
		//int headerViewHeight = headerView.getMeasuredHeight();
		// 设置HeaderView的padding-top为负的HeaderView的高度，达到隐藏它的目的
		//headerView.setPadding(0, -headerViewHeight, 0, 0);
		// 给ListView添加一个头部，这个方法必须在setAdapter前调用()
		//refreshListView.addHeaderView(headerView);
		//#end
		adapter = new RefreshListViewAdapter();
		refreshListView.setAdapter(adapter);
	}

	// 适配器
	class RefreshListViewAdapter extends BaseAdapter
	{
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			TextView tv = new TextView(MainActivity.this);
			tv.setPadding(20, 20, 20, 20);
			tv.setTextSize(18);
			tv.setText(dataList.get(position));
			return tv;
		}

		@Override
		public int getCount()
		{
			return dataList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}
	}

}
