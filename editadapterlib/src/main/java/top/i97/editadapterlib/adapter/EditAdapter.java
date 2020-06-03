package top.i97.editadapterlib.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import top.i97.editadapterlib.inter.IEditSelectedListener;
import top.i97.editadapterlib.inter.ISelected;
import top.i97.editadapterlib.viewholder.BaseEditViewHolder;
import top.i97.editadapterlib.viewholder.EmptyViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑模式适配器（过时⚠️）
 *
 * <p>
 * 推荐使用{@link BaseQuickEditModeAdapter}
 * </p>
 *
 * @author Plain
 * @date 2019/12/4 6:38 下午
 */
@Deprecated
public abstract class EditAdapter<T extends ISelected> extends RecyclerView.Adapter<BaseEditViewHolder> {

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
     * 无数据
     */
    private static final int EMPTY_DATA = 0x10001;

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
     * 空数据布局ID
     */
    private int emptyLayoutId = 0;

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
        this(list, 0);
    }

    public EditAdapter(List<T> list, int layoutId) {
        this(list, layoutId, 0);
    }

    public EditAdapter(List<T> list, int layoutId, int emptyLayoutId) {
        this.list = list;
        this.layoutId = layoutId;
        this.emptyLayoutId = emptyLayoutId;
    }

    @NonNull
    @Override
    public BaseEditViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        Log.e(TAG, i + "");
        if (i == EMPTY_DATA) {
            return createEmptyViewHolder(inflater.inflate(emptyLayoutId, viewGroup, false));
        }
        return createViewHolder(inflater.inflate(layoutId, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseEditViewHolder vh, int i) {
        if (!(vh instanceof EmptyViewHolder)) {
            editKernel(list.get(i), vh);
            convert(list.get(i), vh);
        }
    }

    /**
     * 编辑模式核心
     *
     * @param t  Data
     * @param vh ViewHolder
     */
    private void editKernel(T t, BaseEditViewHolder vh) {
        View hideView = vh.getHideView();
        if (null != hideView) {
            if (curMode == EDIT_MODE) {
                hideView.setVisibility(View.VISIBLE);
            } else {
                hideView.setVisibility(View.GONE);
                vh.itemView.setOnLongClickListener(v -> {
                    if (null != editSelectedListener) {
                        editSelectedListener.onLongClickEnterEditMode();
                    }
                    appendItemForSelectedList(t);
                    return true;
                });
            }
        }

        touchModeKernel(t, vh);
    }

    /**
     * 根据当前{@link EditAdapter#getTouchMode()}点击模式，调用不用的方案
     *
     * @param t  Data
     * @param vh ViewHolder
     */
    private void touchModeKernel(T t, BaseEditViewHolder vh) {
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
    private void touchModeRootKernel(T t, BaseEditViewHolder vh) {
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
    private void touchModeChildKernel(T t, BaseEditViewHolder vh) {
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
     * 数据绑定
     *
     * @param item Item
     * @param vh   ViewHolder
     */
    protected abstract void convert(T item, BaseEditViewHolder vh);

    /**
     * 创建ViewHolder
     *
     * @param itemView itemView
     * @return ViewHolder
     */
    protected abstract BaseEditViewHolder createViewHolder(View itemView);

    /**
     * 创建空数据ViewHolder
     *
     * @param itemView itemView
     * @return ViewHolder
     */
    protected abstract BaseEditViewHolder createEmptyViewHolder(View itemView);

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
        if (null == list || 0 == list.size()) {
            return EMPTY_DATA;
        }
        return super.getItemViewType(position);
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
            for (int i = 0; i < list.size(); i++) {
                T t = list.get(i);
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
        if (null != list && null != selectedList) {
            for (int i = 0; i < list.size(); i++) {
                T t = list.get(i);
                removeItemForSelectedList(t);
                notifyItemChanged(i);
            }
            callBackSelectedCount();
        }
    }

    /**
     * 删除选中Item
     */
    public void removeSelectedItem() {
        if (null != list && null != selectedList) {
            //循环内删除元素需要倒序删除
            for (int i = list.size() - 1; i >= 0; i--) {
                T t = list.get(i);
                if (selectedList.contains(t)) {
                    list.remove(i);
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
     * 获取是否选择全部数据
     *
     * @return isSelectedAllItem
     */
    public boolean isSelectedAllItem() {
        return getSelectedItemCount() == list.size();
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
        if (null == list || 0 == list.size()) {
            return 1;
        }
        return list.size();
    }

}
