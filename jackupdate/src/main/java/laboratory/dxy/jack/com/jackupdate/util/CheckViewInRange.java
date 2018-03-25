package laboratory.dxy.jack.com.jackupdate.util;

import android.view.MotionEvent;
import android.view.View;


/**
 * 检查触摸事件是否在控件上
 *
 * */
public class CheckViewInRange {
    public CheckViewInRange() {
        throw new RuntimeException("cannot be instantiated");
    }

    public static boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        if (view.getVisibility() != View.VISIBLE) {
            return false;
        } else {
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            return ev.getRawX() >= (float) x && ev.getRawX() <= (float) (x + view.getWidth()) && ev.getRawY() >= (float) y && ev.getRawY() <= (float) (y + view.getHeight());
        }
    }
}