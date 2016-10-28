package cn.ifavor.switchbutton;

import cn.ifavor.switchbutton.tools.ToastUtils;
import cn.ifavor.switchbutton.view.EasySwitchButton;
import cn.ifavor.switchbutton.view.SwitchButton;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * �����ṩ�����汾�� SwitchButon��EasySwitchButton
 * 
 * @author SvenHe
 * 
 */
public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ��ʼ�����
		init();
	}

	/* ��ʼ����� */
	private void init() {
		// ����Ϊ �ɻ�����ť - SwitchButton
		((SwitchButton) findViewById(R.id.sb_button))
				.setOnCheckChangedListener(new MyOnOpenedListener());

		((SwitchButton) findViewById(R.id.sb_button1))
				.setOnCheckChangedListener(new MyOnOpenedListener());

		// ����Ϊ ���ɻ�����ť - EasySwitchButton
		((EasySwitchButton) findViewById(R.id.esb_button))
				.setOnCheckChangedListener(new MyEasyOnOpenedListener());

		((EasySwitchButton) findViewById(R.id.esb_button_1))
				.setOnCheckChangedListener(new MyEasyOnOpenedListener());

		((EasySwitchButton) findViewById(R.id.esb_button_2))
				.setOnCheckChangedListener(new MyEasyOnOpenedListener());

	}

	/**
	 * SwitchButton �ĵ���¼�
	 * 
	 * @author SvenHe
	 */
	private class MyOnOpenedListener implements SwitchButton.OnOpenedListener {

		@Override
		public void onChecked(View v, boolean isOpened) {
			ToastUtils.show(isOpened ? "�Ҵ���" : "�ҹر���");
		}
	}

	/**
	 * EasySwitchButton �ĵ���¼�
	 * 
	 * @author SvenHe
	 */
	private class MyEasyOnOpenedListener implements
			EasySwitchButton.OnOpenedListener {

		@Override
		public void onChecked(View v, boolean isOpened) {
			ToastUtils.show(isOpened ? "�Ҵ���" : "�ҹر���");
		}
	}
}
