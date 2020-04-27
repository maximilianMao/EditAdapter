/*
MIT License

Copyright (c) 2020 Plain

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package top.i97.editadapterlib.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import top.i97.editadapterlib.inter.IEditKernelView;
import top.i97.editadapterlib.inter.IEditSelectedListener;
import top.i97.editadapterlib.inter.ISelected;

/**
 * name: BaseQuickEditModeAdapter
 * desc: 基础编辑列表适配器
 * date: 2020/4/14 1:06 PM
 * version: v1.0
 * author: Plain
 * blog: https://plain-dev.com
 * email: im@i97.top
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
     *
     * <p>
     * 在此模式下，列表为正常显示的状态
     * </p>
     */
    public static final int SHOW_MODE = 0x101;
    /**
     * 编辑模式
     *
     * <p>
     * 在此模式下，列表为等待编辑状态
     * </p>
     */
    public static final int EDIT_MODE = 0x102;

    /**
     * 点击模式 - 整个Item
     *
     * <p>
     * 当列表处于编辑模式{@link BaseQuickEditModeAdapter#EDIT_MODE}时，指定此点击模式，点击的区域为整个Item
     * </p>
     */
    static final int TOUCH_MODE_ROOT = 0x666;

    /**
     * 点击模式 - 仅CheckBox
     *
     * <p>
     * 当列表处于编辑模式{@link BaseQuickEditModeAdapter#EDIT_MODE}时，指定此点击模式，点击的区域为指定的<br />
     * 多选框{@link IEditKernelView#getCheckBox(BaseViewHolder)}
     * </p>
     */
    static final int TOUCH_MODE_CHILD = 0x999;

    /**
     * 当前模式
     *
     * <p>
     * {@link BaseQuickEditModeAdapter#SHOW_MODE} 或者 {@link BaseQuickEditModeAdapter#EDIT_MODE}
     * </p>
     */
    private int curMode = SHOW_MODE;

    /**
     * 编辑模式选择监听器
     *
     * <p>
     * 编辑模式下{@link BaseQuickEditModeAdapter#EDIT_MODE}列表选择监听器, 包括一下内容
     * <ul>
     *     <li>{@link IEditSelectedListener#onSelectedItemCount(int)}: 回调当前选择的item数量</li>
     *     <li>{@link IEditSelectedListener#onLongClickEnterEditMode()}: 回调长按进入编辑模式</li>
     * </ul>
     * </p>
     */
    private IEditSelectedListener editSelectedListener;

    /**
     * 已选择Item列表
     *
     * <p>
     * 用来存放以选择的Item{@link BaseQuickAdapter#mData}
     * </p>
     */
    private List<ISelected> selectedList = new ArrayList<>();

    public BaseQuickEditModeAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    /**
     * 获取当前模式
     *
     * <p>
     * {@link BaseQuickEditModeAdapter#SHOW_MODE} 或者 {@link BaseQuickEditModeAdapter#EDIT_MODE}
     * </p>
     *
     * @return CurMode
     */
    public int getCurMode() {
        return curMode;
    }

    /**
     * 设置当前模式
     *
     * <p>
     * {@link BaseQuickEditModeAdapter#SHOW_MODE} 或者 {@link BaseQuickEditModeAdapter#EDIT_MODE}
     * </p>
     *
     * @param curMode 模式
     */
    public void setCurMode(int curMode) {
        this.curMode = curMode;
    }

    /**
     * 设置编辑模式选择监听器
     *
     * @param editSelectedListener 编辑模式选择监听器{@link BaseQuickEditModeAdapter#editSelectedListener}
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
        // 处理编辑模式的核心方法
        editKernel(helper, item);
        // 外部重写该方法，填充Item的内容
        convertView(helper, item);
    }

    /**
     * 填充Item的内容
     *
     * @param helper {@link BaseViewHolder}
     * @param item   {@link BaseQuickAdapter#mData}
     */
    protected abstract void convertView(K helper, T item);

    /**
     * 获取指定的复选框
     *
     * @param helper {@link BaseViewHolder}
     * @return {@link CheckBox}复选框
     */
    @Override
    public abstract CheckBox getCheckBox(K helper);

    /**
     * 获取当切换显示模式{@link BaseQuickEditModeAdapter#SHOW_MODE}和<br />
     * 编辑模式{@link BaseQuickEditModeAdapter#EDIT_MODE}时, 需要隐藏的View, <br />
     * 一般为复选框{@link IEditKernelView#getCheckBox(BaseViewHolder)}
     *
     * @param helper {@link BaseViewHolder}
     * @return Hide View
     */
    @Override
    public abstract View getHideView(K helper);

    /**
     * 编辑模式核心
     *
     * @param vh {@link BaseViewHolder}
     * @param t  {@link BaseQuickAdapter#mData}
     */
    private void editKernel(K vh, T t) {
        View hideView = getHideView(vh);
        if (null == hideView) {
            throw new IllegalArgumentException("未指定HideView!!!");
        }
        if (curMode == EDIT_MODE) {
            hideView.setVisibility(View.VISIBLE);
            // 如果进入编辑模式，则进入编辑模式核心方法
            touchModeKernel(vh, t);
        } else {
            hideView.setVisibility(View.GONE);
            // 长按item进入编辑模式，进入的操作由外部实现
            vh.itemView.setOnLongClickListener(v -> {
                // modify 2020-4-16: 长按item判断下当前所处模式，如果是编辑模式就不响应
                if (!checkEditMode() && null != editSelectedListener) {
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
     * 根据当前{@link BaseQuickEditModeAdapter#getTouchMode()}点击模式，调用不用的方案
     *
     * @param vh {@link BaseViewHolder}
     * @param t  {@link BaseQuickAdapter#mData}
     */
    private void touchModeKernel(K vh, T t) {
        if (getTouchMode() == TOUCH_MODE_ROOT) {
            touchModeRootKernel(vh, t);
        } else if (getTouchMode() == TOUCH_MODE_CHILD) {
            touchModeChildKernel(vh, t);
        } else {
            Log.e(TAG, "未指定点击模式!!!");
        }
    }

    /**
     * 点击模式{@link BaseQuickEditModeAdapter#TOUCH_MODE_ROOT}核心
     *
     * @param vh {@link BaseViewHolder}
     * @param t  {@link BaseQuickAdapter#mData}
     */
    private void touchModeRootKernel(K vh, T t) {
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
     * 点击模式{@link BaseQuickEditModeAdapter#TOUCH_MODE_CHILD}核心
     *
     * @param vh {@link BaseViewHolder}
     * @param t  {@link BaseQuickAdapter#mData}
     */
    private void touchModeChildKernel(K vh, T t) {
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
     * @param t          {@link BaseQuickAdapter#mData}
     * @param isSelected 选择状态, true: 选中, false: 未选中
     */
    private void processSelected(T t, boolean isSelected) {
        if (isSelected) {
            appendItemForSelectedList(t);
        } else {
            removeItemForSelectedList(t);
        }
    }

    /**
     * 添加Item到已选择列表{@link BaseQuickEditModeAdapter#selectedList}
     *
     * @param t {@link BaseQuickAdapter#mData}
     */
    private void appendItemForSelectedList(T t) {
        t.setSelected(true);
        if (!selectedList.contains(t)) {
            selectedList.add(t);
        }
    }

    /**
     * 删除Item从已选择列表{@link BaseQuickEditModeAdapter#selectedList}
     *
     * @param t {@link BaseQuickAdapter#mData}
     */
    private void removeItemForSelectedList(T t) {
        t.setSelected(false);
        selectedList.remove(t);
    }

    /**
     * 变更显示模式
     *
     * @param mode 显示模式: {@link BaseQuickEditModeAdapter#SHOW_MODE}<br />
     *             编辑模式: {@link BaseQuickEditModeAdapter#EDIT_MODE}
     */
    public void changeMode(int mode) {
        this.curMode = mode;
        restoreUnSelected();
        notifyDataSetChanged();
    }

    /**
     * 恢复所有已选择Item{@link BaseQuickEditModeAdapter#selectedList}为未选择状态
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
        if (null != mData && null != selectedList && checkEditMode()) {
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
        if (null != mData && null != selectedList && checkEditMode()) {
            for (int i = 0; i < mData.size(); i++) {
                T t = mData.get(i);
                removeItemForSelectedList(t);
                notifyItemChanged(i);
            }
            callBackSelectedCount();
        }
    }

    /**
     * 检查当前是否处于{@link BaseQuickEditModeAdapter#EDIT_MODE}编辑模式
     *
     * @return true: 编辑模式{@link BaseQuickEditModeAdapter#EDIT_MODE}<br />
     * false: 显示模式{@link BaseQuickEditModeAdapter#SHOW_MODE}
     */
    private boolean checkEditMode() {
        return getCurMode() == EDIT_MODE;
    }

    /**
     * 获取要删除的Item (外部实现)
     *
     * <p>
     * 一般删除item都是需要传给接口id的，可以在适配器中重写此方法，来实现自己的逻辑<br />
     * 获取以选择的item集合可通过{@link BaseQuickEditModeAdapter#getSelectedList()}<br />
     * 获得，示例写法如下<br />
     * <pre>
     * {@code
     * public String getDeleteParams() {
     *      List<ISelected> selectedList = getSelectedList();
     *      if (!ListUtils.isEmpty(selectedList)) {
     *          StringBuilder sb = new StringBuilder();
     *          for (ISelected iSelected : selectedList) {
     *              if (iSelected instanceof TestBean) {
     *                  sb.append(((TestBean) iSelected).getId()).append(",");
     *              }
     *          }
     *          return sb.toString();
     *       }
     *      return null;
     * }
     * }
     * </pre>
     * </p>
     *
     * @return You decide
     */
    public String getDeleteParams() {
        return null;
    }

    /**
     * 获取已选择的Item{@link BaseQuickEditModeAdapter#selectedList}
     *
     * @return List<ISelected> {@link BaseQuickEditModeAdapter#selectedList}
     */
    public List<ISelected> getSelectedList() {
        return selectedList;
    }

    /**
     * 删除选中Item{@link BaseQuickEditModeAdapter#selectedList}
     */
    public void removeSelectedItem() {
        if (null != mData && null != selectedList && checkEditMode()) {
            // 循环内删除元素需要倒序删除
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
     * 获取已选择Item{@link BaseQuickEditModeAdapter#selectedList}的数量
     *
     * @return 已选择Item{@link BaseQuickEditModeAdapter#selectedList}的数量
     */
    public int getSelectedItemCount() {
        if (null != selectedList && checkEditMode()) {
            return selectedList.size();
        }
        return 0;
    }

    /**
     * 删除Item{@link BaseQuickAdapter#mData}
     *
     * @param pos Item position
     */
    private void removeItem(int pos) {
        notifyItemRemoved(pos);
        notifyItemRangeChanged(0, getItemCount());
    }

    /**
     * 获取是否选择全部数据
     *
     * @return isSelectedAllItem true: 选择全部, false: 未选中全部
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
     * 追加列表{@link BaseQuickAdapter#mData}数据
     *
     * @param data New data
     */
    public void addData(@NonNull List<T> newData) {
        if (null != newData) {
            super.addData(newData);
        }
    }

    /**
     * 更新列表{@link BaseQuickAdapter#mData}数据
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
