package com.achellies.android.wechatxposed.hook;

/**
 * Created by achellies on 16/11/18.
 */

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * 微信公共号自动关注的hook
 */
class WeChatContactInfoHook extends BaseHook {

    @Override
    public void hook(ClassLoader classLoader) throws Throwable {
        findAndHookMethod(WeChatSettings.CONTACT_INFO_UI_ACTIVITY, classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                final Activity activity = (Activity) param.thisObject;
                final ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();

                int star = activity.getIntent().getIntExtra("x_action_star", 0);
                if (star == 1) {
                    activity.getIntent().putExtra("x_action_star", 0);
                    contentView.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = findTextView(contentView, "关注");
                            if (textView != null) {
                                int[] location = new int[2];
                                textView.getLocationOnScreen(location);
                                simulateClick(location[0] + textView.getWidth() / 2, location[1] + textView.getHeight() / 2);
                            } else {
                                textView = findTextView(contentView, "添加到通讯录");
                                if (textView != null) {
                                    int[] location = new int[2];
                                    textView.getLocationOnScreen(location);
                                    simulateClick(location[0] + textView.getWidth() / 2, location[1] + textView.getHeight() / 2);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
