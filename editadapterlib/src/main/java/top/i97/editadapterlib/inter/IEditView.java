package top.i97.editadapterlib.inter;

import android.view.View;
import android.widget.CheckBox;

/**
 * 编辑模式View接口（过时⚠️）
 *
 * @author Plain
 * @date 2019/12/5 2:11 下午
 */
@Deprecated
public interface IEditView {

    /**
     * 获取要隐藏的View
     *
     * @return View
     */
    View getHideView();

    /**
     * 获取CheckBox
     *
     * @return CheckBox
     */
    CheckBox getCheckBox();

}
