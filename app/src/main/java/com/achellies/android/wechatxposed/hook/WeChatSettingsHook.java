package com.achellies.android.wechatxposed.hook;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by achellies on 16/11/24.
 */
class WeChatSettingsHook extends BaseHook {
    @Override
    public void hook(ClassLoader classLoader) throws Throwable {
        findAndHookMethod(WeChatSettings.SETTING_ACTIVITY, classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                final Activity activity = (Activity) param.thisObject;
                final ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();

                final Handler handler = new Handler(Looper.getMainLooper());

                int logout = activity.getIntent().getIntExtra("x_logout", 0);
                if (logout == 1) {
                    activity.getIntent().putExtra("x_logout", 0);
                    contentView.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = findTextView(contentView, "退出");
                            if (textView != null) {
                                int[] location = new int[2];
                                textView.getLocationOnScreen(location);
                                simulateClick(location[0] + textView.getWidth() / 2, location[1] + textView.getHeight() / 2);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        findDialogInstance(activity);
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                findDialogInstance(activity);
                                            }
                                        }, WeChatRouterHook.POST_DELAYED_TIME);
                                    }
                                }, WeChatRouterHook.POST_DELAYED_TIME);
                            }
                        }
                    });
                }
            }
        });
    }

    void findDialogInstance(Activity activity) {
        try {
            Class<?> clazz = activity.getClassLoader().loadClass(WeChatSettings.SETTING_ACTIVITY);
            Field[] fields = clazz.getDeclaredFields();
            if (fields != null) {
                for (int index = 0; index < fields.length; ++index) {
                    Field field = fields[index];
                    if (field.getType().getCanonicalName().contentEquals(Dialog.class.getCanonicalName())) {
                        field.setAccessible(true);
                        Dialog dialog = (Dialog) field.get(activity);
                        if (dialog != null) {
                            ViewGroup decorView = (ViewGroup) dialog.getWindow().getDecorView();
                            if (decorView != null) {
                                simulateUserClick(decorView);
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException ignore) {
        } catch (IllegalAccessException ignore) {
        }
    }

    void simulateUserClick(ViewGroup contentView) {
        TextView textView = findTextView(contentView, "退出当前帐号");
        if (textView != null) {
            int[] location = new int[2];
            textView.getLocationOnScreen(location);
            simulateClick(location[0] + textView.getWidth() / 2, location[1] + textView.getHeight() / 2);
        }

        Button button = findButton(contentView, "退出");
        if (button != null) {
            button.performClick();
        }
    }
}
