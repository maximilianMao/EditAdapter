package top.i97.editadapterlib.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import top.i97.editadapterlib.inter.IEditKernelView;
import top.i97.editadapterlib.inter.IEditSelectedListener;
import top.i97.editadapterlib.inter.ISelected;
import top.i97.editadapterlib.util.ListUtils;

/**
 * name: BaseQuickEditModeAdapter
 * desc: 基础编辑列表适配器
 * date: 2020/4/14 1:06 PM
 * version: v1.0
 * author: Plain
 * blog: https://plain-dev.com
 * email: support@plain-dev.com
 */
@SuppressWarnings("ALL")
public abstract class BaseQuickEditModeAdapter
        <T extends ISelected, K extends BaseViewHolder>
        extends BaseQuickAdapter
        <T, K>
        implements IEditKernelView
        <K> {

    /**
     * 显示模式
     */
    public static final int SHOW_MODE = 0x101;
    /**
     * 编辑模式
     */
    public static final int EDIT_MODE = 0x102;

    /**
     * 点击模式 - 整个Item
     */
    static final int TOUCH_MODE_ROOT = 0x666;

    /**
     * 点击模式 - 仅CheckBox
     */
    static final int TOUCH_MODE_CHILD = 0x999;

    /**
     * 当前模式
     */
    private int curMode = SHOW_MODE;

    /**
     * 编辑模式选择监听
     */
    private IEditSelectedListener editSelectedListener;

    /**
     * 已选择Item列表
     */
    private List<ISelected> selectedList = new ArrayList<>();

    public BaseQuickEditModeAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    /**
     * 获取当前模式
     *
     * @return CurMode
     */
    public int getCurMode() {
        return curMode;
    }

    /**
     * 设置当前模式
     *
     * @param curMode 模式
     */
    public void setCurMode(int curMode) {
        this.curMode = curMode;
    }

    /**
     * 设置编辑模式选择监听器
     *
     * @param editSelectedListener IEditSelectedListener
     */
    public void setEditSelectedListener(IEditSelectedListener editSelectedListener) {
        this.editSelectedListener = editSelectedListener;
    }

    @Override
    public void setOnItemChildClickListener(BaseQuickAdapter.OnItemChildClickListener listener) {
        // 只有在展示模式才相应item点击事件
        if (getCurMode() == SHOW_MODE) {
            super.setOnItemChildClickListener(listener);
        }
    }

    @Override
    protected void convert(K helper, T item) {
        editKernel(helper, item);
        convertView(helper, item);
    }

    protected abstract void convertView(K helper, T item);

    @Override
    public abstract CheckBox getCheckBox(K helper);

    @Override
    public abstract View getHideView(K helper);

    /**
     * 编辑模式核心
     *
     * @param vh ViewHolder
     * @param t  Data
     */
    private void editKernel(K vh, T t) {
        View hideView = getHideView(vh);
        if (null == hideView) {
            throw new IllegalArgumentException("未指定HideView!!!");
        }
        if (curMode == EDIT_MODE) {
            hideView.setVisibility(View.VISIBLE);
            // 如果进入编辑模式，则进入编辑模式核心方法
            touchModeKernel(t, vh);
        } else {
            hideView.setVisibility(View.GONE);
            // 长按item进入编辑模式，进入的操作由外部实现
            vh.itemView.setOnLongClickListener(v -> {
                // modify 2020-4-16: 长按item判断下当前所处模式，如果是编辑模式就不响应
                if (getCurMode() != EDIT_MODE && null != editSelectedListener) {
                    editSelectedListener.onLongClickEnterEditMode();
                    appendItemForSelectedList(t);
                    callBackSelectedCount();
                    return true;
                }
                return false;
            });
        }

    }

    /**
     * 获取点击模式
     *
     * <p>
     * 默认 {@link BaseQuickEditModeAdapter#TOUCH_MODE_ROOT}
     * </p>
     *
     * @return 点击模式，有以下值可选
     */
    int getTouchMode() {
        return TOUCH_MODE_ROOT;
    }

    /**
     * 根据当前getTouchMode()点击模式，调用不用的方案
     *
     * @param t  Data
     * @param vh ViewHolder
     */
    private void touchModeKernel(T t, K vh) {
        if (getTouchMode() == TOUCH_MODE_ROOT) {
            touchModeRootKernel(t, vh);
        } else if (getTouchMode() == TOUCH_MODE_CHILD) {
            touchModeChildKernel(t, vh);
        } else {
            Log.e(TAG, "未指定点击模式!!!");
        }
    }

    /**
     * 点击模式 TOUCH_MODE_ROOT 核心
     *
     * @param t  Data
     * @param vh ViewHolder
     */
    private void touchModeRootKernel(T t, K vh) {
        View itemView = vh.itemView;
        CheckBox checkBox = getCheckBox(vh);
        if (null == checkBox) {
            throw new IllegalArgumentException("未指定CheckBox!!!");
        }
        checkBox.setClickable(false);
        checkBox.setChecked(t.isSelected());
        itemView.setOnClickListener(v -> {
            boolean selected = !t.isSelected();
            checkBox.setChecked(selected);
            processSelected(t, selected);
            callBackSelectedCount();
        });
    }

    /**
     * 点击模式 TOUCH_MODE_CHILD 核心
     *
     * @param t  Data
     * @param vh ViewHolder
     */
    private void touchModeChildKernel(T t, K vh) {
        CheckBox checkBox = getCheckBox(vh);
        checkBox.setClickable(true);
        checkBox.setChecked(t.isSelected());
        if (curMode == EDIT_MODE) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                //过滤非人为点击
                if (!buttonView.isPressed()) {
                    return;
                }
                processSelected(t, isChecked);
                callBackSelectedCount();
            });
        }
    }

    /**
     * 选择状态设置
     *
     * @param t          Data
     * @param isSelected isSelected
     */
    private void processSelected(T t, boolean isSelected) {
        if (isSelected) {
            appendItemForSelectedList(t);
        } else {
            removeItemForSelectedList(t);
        }
    }

    /**
     * 添加Item到已选择列表
     *
     * @param t Data
     */
    private void appendItemForSelectedList(T t) {
        t.setSelected(true);
        if (!selectedList.contains(t)) {
            selectedList.add(t);
        }
    }

    /**
     * 删除Item从已选择列表
     *
     * @param t Data
     */
    private void removeItemForSelectedList(T t) {
        t.setSelected(false);
        selectedList.remove(t);
    }

    /**
     * 变更显示模式
     *
     * @param mode 模式，有以下值可选
     */
    public void changeMode(int mode) {
        this.curMode = mode;
        restoreUnSelected();
        notifyDataSetChanged();
    }

    /**
     * 恢复所有已选择Item为未选择状态
     */
    private void restoreUnSelected() {
        if (null != selectedList) {
            for (ISelected selected : selectedList) {
                if (selected.isSelected()) {
                    selected.setSelected(false);
                }
            }
            selectedList.clear();
        }
    }

    /**
     * 选择全部Item
     */
    public void selectedAllItem() {
        if (null != mData && null != selectedList) {
            for (int i = 0; i < mData.size(); i++) {
                T t = mData.get(i);
                appendItemForSelectedList(t);
                notifyItemChanged(i);
            }
            callBackSelectedCount();
        }
    }

    /**
     * 取消选择全部Item
     */
    public void unSelectedAllItem() {
        if (null != mData && null != selectedList) {
            for (int i = 0; i < mData.size(); i++) {
                T t = mData.get(i);
                removeItemForSelectedList(t);
                notifyItemChanged(i);
            }
            callBackSelectedCount();
        }
    }

    /**
     * 获取要删除的Item (外部实现)
     *
     * <p>
     * 一般删除item都是需要传给接口id的，可以在适配器中重写此方法，来实现自己的逻辑
     * 获取以选择的item集合可通过{@link BaseQuickEditModeAdapter#getSelectedList()}
     * 获得，示例写法如下
     * <code>
     * public String getDeleteParams() {
     * List<ISelected> selectedList = getSelectedList();
     * if (!ListUtils.isEmpty(selectedList)) {
     * StringBuilder sb = new StringBuilder();
     * for (ISelected iSelected : selectedList) {
     * if (iSelected instanceof TestBean) {
     * sb.append(((TestBean) iSelected).getId()).append(",");
     * }
     * }
     * return sb.toString();
     * }
     * return null;
     * }
     * </code>
     * </p>
     *
     * @return Select item
     */
    public String getDeleteParams() {
        return null;
    }

    /**
     * 获取已选择的Item
     *
     * @return List<ISelected>
     */
    public List<ISelected> getSelectedList() {
        return selectedList;
    }

    /**
     * 删除选中Item
     */
    public void removeSelectedItem() {
        if (null != mData && null != selectedList) {
            //循环内删除元素需要倒序删除
            for (int i = mData.size() - 1; i >= 0; i--) {
                T t = mData.get(i);
                if (selectedList.contains(t)) {
                    mData.remove(i);
                    selectedList.remove(t);
                    removeItem(i);
                }
            }
            callBackSelectedCount();
        }
    }

    /**
     * 获取已选择Item的数量
     *
     * @return 已选择Item的数量
     */
    public int getSelectedItemCount() {
        if (null != selectedList) {
            return selectedList.size();
        }
        return 0;
    }

    /**
     * 删除Item
     *
     * @param pos item position
     */
    private void removeItem(int pos) {
        notifyItemRemoved(pos);
        notifyItemRangeChanged(0, getItemCount());
    }

    /**
     * 获取是否选择全部数据
     *
     * @return isSelectedAllItem
     */
    public boolean isSelectedAllItem() {
        return getSelectedItemCount() == mData.size();
    }

    /**
     * 回调当前选择数量
     */
    private void callBackSelectedCount() {
        if (null != editSelectedListener) {
            editSelectedListener.onSelectedItemCount(getSelectedItemCount());
        }
    }

    /**
     * 追加列表数据
     *
     * @param data New data
     */
    public void addData(@NonNull List<T> newData) {
        if (null != newData) {
            super.addData(newData);
        }
    }

    /**
     * 更新列表数据
     *
     * @param list List
     */
    public void updateData(List<T> list) {
        mData.clear();
        if (null != list) {
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

}
