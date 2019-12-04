package top.i97.editadapterlib.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import top.i97.editadapterlib.inter.IEditSelectedListener;
import top.i97.editadapterlib.inter.ISelected;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Plain
 * @date 2019/12/4 6:38 下午
 */
public abstract class EditAdapter<T extends ISelected, VH extends EditAdapter.EditViewHolder> extends RecyclerView.Adapter<VH> {

    private static final String TAG = EditAdapter.class.getSimpleName();

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
    public static final int TOUCH_MODE_ROOT = 0x666;

    /**
     * 点击模式 - 仅CheckBox
     */
    public static final int TOUCH_MODE_CHILD = 0x999;

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

    /**
     * 列表数据
     */
    private List<T> list;

    /**
     * 布局ID
     */
    private int layoutId = 0;

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

    public EditAdapter(List<T> list) {
        if (null == list) {
            throw new IllegalArgumentException("数据不能为空");
        }
        this.list = list;
    }

    public EditAdapter(List<T> list, int layoutId) {
        if (null == list) {
            throw new IllegalArgumentException("数据不能为空");
        }
        if (0 == layoutId) {
            throw new IllegalArgumentException("布局不能为空");
        }
        this.list = list;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return createViewHolder(inflater.inflate(layoutId, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        editKernel(list.get(i), vh);
        convert(list.get(i), vh);
    }

    /**
     * 编辑模式核心
     *
     * @param t  Data
     * @param vh ViewHolder
     */
    private void editKernel(T t, VH vh) {
        View hideView = vh.getHideView();
        if (null != hideView) {
            if (curMode == EDIT_MODE) {
                hideView.setVisibility(View.VISIBLE);
            } else {
                hideView.setVisibility(View.GONE);
            }
        }

        if (getTouchMode() == TOUCH_MODE_ROOT) {
            touchModeRootKernel(t, vh);
        } else if (getTouchMode() == TOUCH_MODE_CHILD) {
            touchModeChildKernel(t, vh);
        } else {
            Log.e(TAG, "未指定点击模式!!!");
        }
    }

    /**
     * 点击模式 {@link EditAdapter#TOUCH_MODE_ROOT} 核心
     *
     * @param t  Data
     * @param vh ViewHolder
     */
    private void touchModeRootKernel(T t, VH vh) {
        View itemView = vh.itemView;
        CheckBox checkBox = vh.getCheckBox();
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
     * 点击模式 {@link EditAdapter#TOUCH_MODE_CHILD} 核心
     *
     * @param t  Data
     * @param vh ViewHolder
     */
    private void touchModeChildKernel(T t, VH vh) {
        CheckBox checkBox = vh.getCheckBox();
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
            t.setSelected(true);
            selectedList.add(t);
        } else {
            t.setSelected(false);
            selectedList.remove(t);
        }
    }

    /**
     * 数据绑定
     *
     * @param item       Item
     * @param viewHolder ViewHolder
     */
    protected abstract void convert(T item, VH viewHolder);

    /**
     * 创建ViewHolder
     *
     * @param itemView itemView
     * @return ViewHolder
     */
    protected abstract VH createViewHolder(View itemView);

    /**
     * 获取点击模式
     *
     * @return 点击模式，有以下值可选
     * {@link EditAdapter#TOUCH_MODE_ROOT}
     * {@link EditAdapter#TOUCH_MODE_CHILD}
     */
    protected abstract int getTouchMode();

    @Override
    public int getItemViewType(int position) {
        if (curMode == EDIT_MODE) {
            return EDIT_MODE;
        }
        return SHOW_MODE;
    }

    /**
     * 变更显示模式
     *
     * @param mode 模式，有以下值可选
     *             {@link EditAdapter#SHOW_MODE} 显示模式
     *             {@link EditAdapter#EDIT_MODE} 编辑模式
     */
    public void changeMode(int mode) {
        this.curMode = mode;
        restoreUnSelected();
        notifyDataSetChanged();
    }

    /**
     * 恢复所有已选择Item为未选择状态
     */
    public void restoreUnSelected() {
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
        if (null != list && null != selectedList) {
            for (T t : list) {
                t.setSelected(true);
                if (!selectedList.contains(t)) {
                    selectedList.add(t);
                }
            }
            callBackSelectedCount();
            notifyDataSetChanged();
        }
    }

    /**
     * 取消选择全部Item
     */
    public void unSelectedAllItem() {
        if (null != list && null != selectedList) {
            for (T t : list) {
                t.setSelected(false);
                selectedList.remove(t);
            }
            callBackSelectedCount();
            notifyDataSetChanged();
        }
    }

    /**
     * 删除选中Item
     */
    public void removeSelectedItem() {
        if (null != list && null != selectedList) {
            int i = 0;
            Iterator<T> iterator = list.iterator();
            while (iterator.hasNext()) {
                T next = iterator.next();
                if (selectedList.contains(next)) {
                    iterator.remove();
                    selectedList.remove(next);
                    removeItem(i);
                }
                i++;
            }
            callBackSelectedCount();
        }
    }

    /**
     * 获取已选择Item的数量
     *
     * @return 已选择Item的数量
     */
    private int getSelectedItemCount() {
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
     * 回调当前选择数量
     */
    private void callBackSelectedCount() {
        if (null != editSelectedListener) {
            editSelectedListener.onSelectedItemCount(getSelectedItemCount());
        }
    }

    @Override
    public int getItemCount() {
        if (null == list) {
            return 0;
        }
        return list.size();
    }

    public static abstract class EditViewHolder extends RecyclerView.ViewHolder {

        public EditViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected abstract View getHideView();

        protected abstract CheckBox getCheckBox();

    }

}
