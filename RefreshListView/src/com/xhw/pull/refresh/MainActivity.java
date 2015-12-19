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

	// ���ݼ���
	private List<String> dataList;

	// �Զ��������ˢ��ListView
	private RefreshListView refreshListView;

	private RefreshListViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// ���ش��ڵı�����(������setContentViewǰ��)
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		initData();
		initListener();
	}

	private void initListener()
	{
		//����ˢ��
		refreshListView.setOnRefreshingListener(new OnRefreshingListener() {
			
			//������ˢ�¿ؼ�����ˢ���е�״̬���˷���������
			@Override
			public void onRefreshing()
			{
				//ģ������ˢ���������������
				pullRequestService();
			}

		});
		
		//���ظ���
		refreshListView.setOnLoadingMoreListener(new OnLoadingMoreListener() {
			
			/**
			 * ������ˢ�¿ؼ�������ظ����״̬���˷���������
			 */
			@Override
			public void onLoadingMore()
			{
				//ģ����ظ����������������
				loadingMoreRequestService();
			}
		});
	}

	/**
	 * ģ����ظ��������������ȡ���ݵķ���
	 */
	private void loadingMoreRequestService()
	{
		new Thread(){
			public void run() {
				SystemClock.sleep(2000);//ģ�����������������ݵ�ʱ��
				dataList.add(new String("���ظ��������"));//���ݼӵ�ListView�ĵײ�
				//�������
				runOnUiThread(new Runnable() {
					
					@Override
					public void run()
					{
						//����Adapter
						adapter.notifyDataSetChanged();
						//���õײ�View״̬
						refreshListView.completeLoadingMore();
					}
				});
				
			}
			
		}.start();
	}
	
	
	/**
	 * ģ������ˢ�������������ȡ���ݵķ���
	 */
	private void pullRequestService()
	{
		new Thread(){
			public void run() {
				SystemClock.sleep(3000);//ģ�����������������ݵ�ʱ��
				dataList.add(0,new String("����ˢ�µ�����"));//���ݼӵ�ListView�Ķ���
				//�������
				runOnUiThread(new Runnable() {
					
					@Override
					public void run()
					{
						//����Adapter
						adapter.notifyDataSetChanged();
						//���ö���View״̬
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
			String str = "ListViewԭ��������:" + i;
			dataList.add(str);
		}
		//#start ע�͵Ĳ����Ѿ���Ƕ��RefreshListView
		//ע�͵Ĳ����Ѿ���Ƕ��RefreshListView
		// �ⲽ����onCreate������ִ�еģ������������ø߶����ò����ģ�����������ɺ���ȥ��ȡ���ĸ߶�
		//View headerView = View.inflate(this, R.layout.refresh_listview_header,
				//null);
		// ����1
		// //���headerView�Ĳ��ּ�����һ�����ֲ���ִ�����ˣ���������������ִ�����ˣ������������õ��߶�
		// headerView.getViewTreeObserver().addOnGlobalLayoutListener(new
		// OnGlobalLayoutListener() {
		//
		// @Override
		// public void onGlobalLayout()
		// {
		// //����ʱremove���ε���
		// headerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		// //����HeaderView��padding-topΪ����HeaderView�ĸ߶ȣ��ﵽ��������Ŀ��
		// int headerViewHeight=headerView.getHeight();
		// headerView.setPadding(0, -headerViewHeight, 0, 0);
		// //��ListView���һ��ͷ�����������������setAdapterǰ����()
		// refreshListView.addHeaderView(headerView);
		// }
		// });

		// ����2��
		//headerView.measure(0, 0);// ǿ��ϵͳȥ������view����������û���壬������ɺ�����õ�getMeasuredHeight
		//int headerViewHeight = headerView.getMeasuredHeight();
		// ����HeaderView��padding-topΪ����HeaderView�ĸ߶ȣ��ﵽ��������Ŀ��
		//headerView.setPadding(0, -headerViewHeight, 0, 0);
		// ��ListView���һ��ͷ�����������������setAdapterǰ����()
		//refreshListView.addHeaderView(headerView);
		//#end
		adapter = new RefreshListViewAdapter();
		refreshListView.setAdapter(adapter);
	}

	// ������
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
