package com.xhw.pull.refresh.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xhw.pull.refresh.R;

public class RefreshListView extends ListView
{

	/**
	 * 下拉刷新状态
	 */
	private final static int PULL_REFRESH = 0;
	/**
	 * 松开刷新状态
	 */
	private final static int RELEASE_REFRESH = 1;
	/**
	 * 正在刷新状态
	 */
	private final static int REFRESHING = 2;

	/**
	 * 当前刷新状态
	 */
	private int currentState = PULL_REFRESH;

	/**
	 * 顶部View
	 */
	private View headerView;

	private ImageView iv_arrow;

	private ProgressBar pb_rotate;

	private TextView tv_state;

	private TextView tv_time;

	/**
	 * 底部View
	 */
	private View footerView;

	// Animation
	private RotateAnimation down2UpAnimation;

	private RotateAnimation up2DownAnimation;

	/**
	 * 顶部View的高度
	 */
	private int headerViewHeight;

	/**
	 * 底部View的高度
	 */
	private int footerViewHeight;

	public RefreshListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public RefreshListView(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		initHeadView();
		initAnimation();
		initFooterView();
		initListener();
	}

	/**
	 * 当前是否处于加载更多状态的标记
	 */
	private boolean isLoadingMore = false;

	/**
	 * 初始化监听器
	 */
	private void initListener()
	{
		// 设置本控件的滚动监听器(设置此监听器为了处理底部View)
		this.setOnScrollListener(new OnScrollListener() {

			/**
			 * 滑动状态改变，此方法调用 参数：scrollState的三个取值 SCROLL_STATE_IDLE :闲置状态，就是手指松开
			 * SCROLL_STATE_TOUCH_SCROLL : 手指触摸滑动，就是按着滑动 SCROLL_STATE_FLING :
			 * 快速滑动后松开，惯性滑动
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				// 当手指松开，并且滑动到ListView的最后一个条目时，需要显示FooterView
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& (RefreshListView.this.getLastVisiblePosition() == RefreshListView.this
								.getCount() - 1))
				{
					if (!isLoadingMore)// 避免重复执行
					{
						isLoadingMore = true;
						// (RefreshListView.this.getLastVisiblePosition() ==
						// RefreshListView.this.getCount() - 1)
						// 上面那个逻辑判断的实质是:虽然初始化时将footerView隐藏了，但是我觉得并没有完全隐藏的，还有一丁点露出来，只是我们肉眼看不见
						// 当滑动到最后一个普通item再过一丁点位置时，getLastVisiblePosition拿到的位置就是FooterView的位置，getCount拿的是整个ListView的条目数，包括HeaderView和footerView
						// 这就能解释为什么最后一个普通条目刚出现时，不能进入这个逻辑判断了

						// 改变padding,让footerView显示出来
						footerView.setPadding(0, 0, 0, 0);
						// 选中最后一条
						RefreshListView.this.setSelection(getCount() - 1);
						
						//暴露接口，让外部的代码能在这里调用
						if(onLoadingMoreListener!=null)
						{
							onLoadingMoreListener.onLoadingMore();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount)
			{

			}
		});
	}

	/**
	 * 初始化底部View
	 */
	private void initFooterView()
	{
		footerView = View.inflate(getContext(),
				R.layout.refresh_listview_footer, null);
		footerView.measure(0, 0);// 强迫系统去测量该view，两个参数没意义，测量完成后可以拿到getMeasuredHeight
		footerViewHeight = footerView.getMeasuredHeight();
		// 设置footerView的padding-top为负的footerView的高度，达到隐藏它的目的
		footerView.setPadding(0, -footerViewHeight, 0, 0);
		this.addFooterView(footerView);
	}

	/**
	 * 初始化箭头旋转动画
	 */
	private void initAnimation()
	{
		down2UpAnimation = new RotateAnimation(0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		down2UpAnimation.setDuration(300);
		down2UpAnimation.setFillAfter(true);
		up2DownAnimation = new RotateAnimation(-180, -360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		up2DownAnimation.setDuration(300);
		up2DownAnimation.setFillAfter(true);
	}

	/**
	 * 初始化顶部View padding-top 为 -1*headerViewHeight时，全部隐藏 padding-top 为
	 * 0时，HeaderView全部显示
	 */
	private void initHeadView()
	{
		headerView = View.inflate(this.getContext(),
				R.layout.refresh_listview_header, null);
		iv_arrow = (ImageView) headerView.findViewById(R.id.iv_arrow);
		pb_rotate = (ProgressBar) headerView.findViewById(R.id.pb_rotate);
		tv_state = (TextView) headerView.findViewById(R.id.tv_state);
		tv_time = (TextView) headerView.findViewById(R.id.tv_time);
		headerView.measure(0, 0);// 强迫系统去测量该view，两个参数没意义，测量完成后可以拿到getMeasuredHeight
		headerViewHeight = headerView.getMeasuredHeight();
		// 设置HeaderView的padding-top为负的HeaderView的高度，达到隐藏它的目的
		headerView.setPadding(0, -headerViewHeight, 0, 0);
		this.addHeaderView(headerView);
	}

	/**
	 * 用于记录手指每次按下的垂直方向位置(以View为参考系)
	 */
	private int startY;

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			startY = (int) event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			// 如果正在刷新，不能再拉了
			if (currentState == REFRESHING)
			{
				break;
			}
			// 在这个分支里面，startY是个常量
			// 获取移动的纵向变化量
			int detalY = (int) (event.getY() - startY);
			// 动态改变HeaderView的padding-top
			// padding-top 的容忍取值范围 : [-headerViewHeight,正无穷)
			// 分别对应HeaderView全部隐藏，和全部显示有余
			int paddingTop = -1 * headerViewHeight + detalY;
			// padding-top的合理范围，最多全部隐藏整个HeaderView，下面的子item不能隐藏
			// this.getFirstVisiblePosition()==0:表示第一个可见item的索引是0时，才执行拦截，因为如果是其它item在第一个可见的位置，下拉不需要显示HeaderView，更不需要拦截ListView
			if (paddingTop >= -1 * headerViewHeight
					&& this.getFirstVisiblePosition() == 0)
			{
				// 设置HeaderView的padding-top
				headerView.setPadding(0, paddingTop, 0, 0);

				// 能走进这个方法，代表HeaderView肯定被滑动出来了

				// 如果整个或者大于整个HeaderView都被滑动出来了，显示松开刷新
				if (paddingTop > 0 && currentState == PULL_REFRESH)// 后面一个条件是状态发生变化的依据
				{
					// 松开刷新状态
					currentState = RELEASE_REFRESH;
					refreshHeaderView();

				} else if (paddingTop <= 0 && currentState == RELEASE_REFRESH)
				{// 如果小于整个HeaderView被滑动出来了，显示下拉刷新
					// 下拉刷新状态
					currentState = PULL_REFRESH;
					refreshHeaderView();
				}

				// HeaderView滑动出来了，就隔断ListView的事件响应，如果ListView的滑动事件和HeaderView的Padding-top变化同时处理，会造成一种滑动错乱感
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			// 如果松开手指还是处于下拉刷新状态，，把HeaderView隐藏就可以了
			if (currentState == PULL_REFRESH)
			{
				headerView.setPadding(0, -1 * headerViewHeight, 0, 0);
			} else if (currentState == RELEASE_REFRESH)// 如果松开手指还是处于松开刷新状态,更新状态为刷新中状态
			{
				// HeaderView完全显示
				headerView.setPadding(0, 0, 0, 0);
				currentState = REFRESHING;
				refreshHeaderView();
				// 这里进入刷新状态，供给外部在这里调用一些代码
				if (onRefreshingListener != null)
				{
					onRefreshingListener.onRefreshing();
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 根据currentState的状态，刷新HeaderView
	 */
	private void refreshHeaderView()
	{
		switch (currentState)
		{
		case PULL_REFRESH:// 下拉刷新
			iv_arrow.startAnimation(up2DownAnimation);
			tv_state.setText("下拉刷新");
			break;
		case RELEASE_REFRESH:// 松开刷新
			iv_arrow.startAnimation(down2UpAnimation);
			tv_state.setText("松开刷新");
			break;
		case REFRESHING:// 刷新中
			iv_arrow.clearAnimation();// 清除动画
			iv_arrow.setVisibility(View.INVISIBLE);
			pb_rotate.setVisibility(View.VISIBLE);
			tv_state.setText("正在刷新...");
			break;
		}
	}

	/**
	 * 完成下拉刷新操作，重置HeaderView状态 在获取完新数据并更新adapter后，由外部调用，注意要在UI线程中调用
	 */
	public void completeRefresh()
	{
		// 更改状态
		currentState = PULL_REFRESH;
		// 隐藏HeaderView
		headerView.setPadding(0, -1 * headerViewHeight, 0, 0);
		// 隐藏旋转滚动条
		pb_rotate.setVisibility(View.INVISIBLE);
		// 显示下拉箭头
		iv_arrow.setVisibility(View.VISIBLE);
		// 更改状态文本
		tv_state.setText("下拉刷新");
		// 更新刷新时间文本
		tv_time.setText("最后更新:" + getCurrentTime());
	}

	
	/**
	 * 完成加载更多操作，重置FooterView状态 在获取完新数据并更新adapter后，由外部调用，注意要在UI线程中调用
	 */
	public void completeLoadingMore()
	{
		//隐藏footerView
		footerView.setPadding(0, -footerViewHeight, 0, 0);
		//刷新状态
		this.isLoadingMore=false;
	}
	
	/**
	 * 获取当前时间
	 * 
	 * @return 当前时间
	 */
	private String getCurrentTime()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date());
	}

	/**
	 * 下拉刷新控件进入正在刷新状态的监听器
	 */
	private OnRefreshingListener onRefreshingListener;

	/**
	 * 设置下拉刷新控件进入正在刷新状态的监听器
	 * 
	 * @param onRefreshingListener
	 *            监听下拉刷新控件进入正在刷新状态的监听器
	 */
	public void setOnRefreshingListener(
			OnRefreshingListener onRefreshingListener)
	{
		this.onRefreshingListener = onRefreshingListener;
	}

	/**
	 * 下拉刷新控件进入正在刷新状态的监听器接口
	 * 
	 * @author admin
	 *
	 */
	public interface OnRefreshingListener
	{
		/**
		 * 建议在此方法中连接网络，请求服务器数据并加载数据，更新Adapter后，应该调用completeRefresh方法，重置顶部View的状态
		 */
		public void onRefreshing();
	}

	// ===============加载更多部分================================

	/**
	 * 下拉刷新控件进入加载更多状态的监听器
	 */
	private OnLoadingMoreListener onLoadingMoreListener;

	/**
	 * 设置下拉刷新控件进入加载更多状态的监听器
	 * 
	 * @param onRefreshingListener
	 *            监听下拉刷新控件进入加载更多状态的监听器
	 */
	public void setOnLoadingMoreListener(
			OnLoadingMoreListener onLoadingMoreListener)
	{
		this.onLoadingMoreListener = onLoadingMoreListener;
	}

	/**
	 * 下拉刷新控件进入加载更多状态的监听器接口
	 * 
	 * @author admin
	 *
	 */
	public interface OnLoadingMoreListener
	{
		//建议在此方法中连接网络，请求服务器数据并加载数据，更新Adapter后，应该调用completeLoadingMore方法，重置底部View的状态
		public void onLoadingMore();
	}
}
