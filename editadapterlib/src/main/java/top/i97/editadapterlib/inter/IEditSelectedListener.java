package top.i97.editadapterlib.inter;

/**
 * 编辑模式 选择监听器
 *
 * @author Plain
 * @date 2019/12/4 6:44 下午
 */
public interface IEditSelectedListener {

    /**
     * 回调当前选中的Item数量
     *
     * @param count Count
     */
    void onSelectedItemCount(int count);

    /**
     * 回调长按进入编辑模式
     */
    void onLongClickEnterEditMode();

}
