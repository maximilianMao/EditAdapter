package top.i97.editadapterlib.entity;

import top.i97.editadapterlib.inter.ISelected;

/**
 * 条目选择实体
 *
 * <p>
 * 目标列表需继承此类
 * </p>
 *
 * @author Plain
 * @date 2019/12/4 6:34 下午
 */
public class SelectedImpl implements ISelected {

    private boolean isSelected;

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
