package top.i97.editadapterlib.inter;

/**
 * 条目选择接口
 *
 * @author Plain
 * @date 2019/12/4 6:35 下午
 */
public interface ISelected {

    /**
     * 获取选择状态
     *
     * @return isSelected
     */
    boolean isSelected();

    /**
     * 设置选择状态
     *
     * @param isSelected isSelected
     */
    void setSelected(boolean isSelected);

}
