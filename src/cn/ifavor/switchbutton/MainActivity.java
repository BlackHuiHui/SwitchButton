package cn.ifavor.switchbutton;

import cn.ifavor.switchbutton.tools.ToastUtils;
import cn.ifavor.switchbutton.view.EasySwitchButton;
import cn.ifavor.switchbutton.view.SwitchButton;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * 这里提供两个版本的 SwitchButon、EasySwitchButton
 * 
 * @author SvenHe
 * 
 */
public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 初始化组件
		init();
	}

	/* 初始化组件 */
	private void init() {
		// 以下为 可滑动按钮 - SwitchButton
		((SwitchButton) findViewById(R.id.sb_button))
				.setOnCheckChangedListener(new MyOnOpenedListener());

		((SwitchButton) findViewById(R.id.sb_button1))
				.setOnCheckChangedListener(new MyOnOpenedListener());

		// 以下为 不可滑动按钮 - EasySwitchButton
		((EasySwitchButton) findViewById(R.id.esb_button))
				.setOnCheckChangedListener(new MyEasyOnOpenedListener());

		((EasySwitchButton) findViewById(R.id.esb_button_1))
				.setOnCheckChangedListener(new MyEasyOnOpenedListener());

		((EasySwitchButton) findViewById(R.id.esb_button_2))
				.setOnCheckChangedListener(new MyEasyOnOpenedListener());

	}

	/**
	 * SwitchButton 的点击事件
	 * 
	 * @author SvenHe
	 */
	private class MyOnOpenedListener implements SwitchButton.OnOpenedListener {

		@Override
		public void onChecked(View v, boolean isOpened) {
			ToastUtils.show(isOpened ? "我打开了" : "我关闭了");
		}
	}

	/**
	 * EasySwitchButton 的点击事件
	 * 
	 * @author SvenHe
	 */
	private class MyEasyOnOpenedListener implements
			EasySwitchButton.OnOpenedListener {

		@Override
		public void onChecked(View v, boolean isOpened) {
			ToastUtils.show(isOpened ? "我打开了" : "我关闭了");
		}
	}
}
