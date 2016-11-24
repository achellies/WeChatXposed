package com.achellies.android.wechatxposed.hook;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by achellies on 16/11/18.
 */
abstract class BaseHook {
    private Instrumentation mInstrumentation = new Instrumentation();

    public abstract void hook(ClassLoader classLoader) throws Throwable;

    protected void simulateClick(final float x, final float y) {
        new Thread() {
            public void run() {
                try {
                    long downTime = SystemClock.uptimeMillis();
                    final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                            MotionEvent.ACTION_DOWN, x, y, 0);
                    downTime += 1000;
                    final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                            MotionEvent.ACTION_UP, x, y, 0);

                    mInstrumentation.sendPointerSync(downEvent);

                    mInstrumentation.sendPointerSync(upEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    protected void simulateScroll(final float startX, final float startY, final float endX, final float endY) {
        new Thread() {
            public void run() {
                try {
                    long downTime = SystemClock.uptimeMillis();
                    final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                            MotionEvent.ACTION_SCROLL, startX, startY, 0);
                    downTime += 5000;
                    final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                            MotionEvent.ACTION_UP, endX, endY, 0);

                    mInstrumentation.sendPointerSync(downEvent);

                    mInstrumentation.sendPointerSync(upEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    protected TextView findTextView(ViewGroup contentView, String text) {
        int count = contentView.getChildCount();
        if (count == 0) {
            return null;
        }
        for (int index = 0; index < count; ++index) {
            View childView = contentView.getChildAt(index);
            if (childView instanceof ViewGroup) {
                TextView tv = findTextView((ViewGroup) childView, text);
                if (tv != null) {
                    return tv;
                }
            } else if (childView instanceof TextView) {
                TextView tv = (TextView) childView;
                if (tv.getText() != null && tv.getText().toString().contentEquals(text)) {
                    return tv;
                }
            }
        }
        return null;
    }

    protected Button findButton(ViewGroup contentView, String text) {
        int count = contentView.getChildCount();
        if (count == 0) {
            return null;
        }
        for (int index = 0; index < count; ++index) {
            View childView = contentView.getChildAt(index);
            if (childView instanceof ViewGroup) {
                Button btn = findButton((ViewGroup) childView, text);
                if (btn != null) {
                    return btn;
                }
            } else if (childView instanceof Button) {
                Button btn = (Button) childView;
                if (btn.getText() != null && btn.getText().toString().contentEquals(text)) {
                    return btn;
                }
            }
        }
        return null;
    }

    protected List<EditText> findEditText(ViewGroup contentView) {
        List<EditText> editTexts = new ArrayList<EditText>();
        int count = contentView.getChildCount();
        if (count == 0) {
            return editTexts;
        }
        for (int index = 0; index < count; ++index) {
            View childView = contentView.getChildAt(index);
            if (childView instanceof ViewGroup) {
                editTexts.addAll(findEditText((ViewGroup) childView));
            } else if (childView instanceof EditText) {
                EditText et = (EditText) childView;
                editTexts.add(et);
            }
        }
        return editTexts;
    }

    protected View findView(ViewGroup contentView, String viewClass) {
        int count = contentView.getChildCount();
        if (count == 0) {
            return null;
        }
        for (int index = 0; index < count; ++index) {
            View childView = contentView.getChildAt(index);
            if (childView instanceof ViewGroup) {
                View btn = findView((ViewGroup) childView, viewClass);
                if (btn != null) {
                    return btn;
                }
            } else {
                try {
                    Class<?> clazz = contentView.getClass().getClassLoader().loadClass(viewClass);
                    if (clazz.isAssignableFrom(childView.getClass())) {
                        return childView;
                    }
                } catch (ClassNotFoundException ignore) {
                }
            }
        }
        return null;
    }
}
