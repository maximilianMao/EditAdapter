package top.i97.editadapterlib.inter;

import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseViewHolder;

/**
 * name: IEditKernelView
 * desc: 编辑模式适配器需要实现该接口，用来获取CheckBox视图和隐藏区域
 * date: 2020/4/14 1:34 PM
 * version: v1.0
 * author: Plain
 * blog: https://plain-dev.com
 * email: support@plain-dev.com
 */
public interface IEditKernelView<K extends BaseViewHolder> {

    /**
     * 获取要隐藏的View
     *
     * @return View
     */
    View getHideView(K helper);

    /**
     * 获取CheckBox
     *
     * @return CheckBox
     */
    CheckBox getCheckBox(K helper);

}
