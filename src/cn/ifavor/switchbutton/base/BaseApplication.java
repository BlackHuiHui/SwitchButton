package cn.ifavor.switchbutton.base;

import android.app.Application;
import android.os.Handler;

public class BaseApplication extends Application{
	public static Application mApplication;
	public static long mainTid;
	public static Handler mHandler;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// 获取全局 Context
		mApplication = this;
		
		// 获取主线程 id
		mainTid = Thread.currentThread().getId();
		
		// 创建主线程的 handler
		mHandler = new Handler();
	}

	/** 获取 Application 对象 */
	public static Application getApplication() {
		return mApplication;
	}

	public static long getMainTid() {
		return mainTid;
	}

	public static Handler getHandler() {
		return mHandler;
	}
	
}
