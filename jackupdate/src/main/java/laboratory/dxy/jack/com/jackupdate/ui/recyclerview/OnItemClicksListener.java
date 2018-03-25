package laboratory.dxy.jack.com.jackupdate.ui.recyclerview;

import android.view.View;

/**
 * Created by Oden on 2016/6/18.
 */
public interface OnItemClicksListener<T> {
    void onItemClick(ViewHolder viewHolder, View view, T t, int position);
    boolean onItemLongClick(ViewHolder viewHolder, View view, T t, int position);
}
