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

package top.i97.editadapterlib.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import top.i97.editadapterlib.entity.Selected
import top.i97.editadapterlib.inter.IEditSelectedListener

/**
 * 基础编辑列表适配器
 *
 * @author Plain
 */
abstract class BaseQuickEditModeAdapter<T : Selected, VH : BaseViewHolder>(
        @LayoutRes private val layoutResId: Int,
        data: MutableList<T>? = null
) : BaseQuickAdapter<T, VH>(layoutResId, data) {

    companion object {

        const val SHOW_MODE = 0x101
        const val EDIT_MODE = 0x202
        const val SELECT_MODE_PARENT = 0x201
        const val SELECT_MODE_CHILD = 0x202

    }

    var currentMode = SHOW_MODE
        set(value) {
            if (field != value) field = value
        }

    private var oldItemClickListener: OnItemClickListener? = null

    var editSelectedListener: IEditSelectedListener? = null

    private val selectedList = mutableMapOf<Any, Selected>()

    private var externalCheckBox: CheckBox? = null

    override fun convert(holder: VH, item: T) {
        // 处理编辑模式的核心方法
        editKernel(holder, item)
        // 外部重写该方法，填充Item的内容
        convertView(holder, item)
    }

    /**
     * 填充Item的内容
     */
    protected abstract fun convertView(helper: VH, item: T)

    /**
     * 获取指定的复选框
     *
     * @param helper [BaseViewHolder]
     * @return [CheckBox]复选框
     */
    abstract fun getCheckBox(helper: VH): CheckBox?

    /**
     * 获取当切换显示模式[BaseQuickEditModeAdapter.SHOW_MODE]和
     * 编辑模式[BaseQuickEditModeAdapter.EDIT_MODE]时, 需要隐藏的View,
     * 一般为复选框[IEditKernelView.getCheckBox]
     */
    abstract fun getHideView(helper: VH): View?

    /**
     * 获取选择模式
     *
     * 默认 [SELECT_MODE_PARENT]
     *
     */
    open fun getSelectMode(): Int {
        return SELECT_MODE_PARENT
    }

    /**
     * 编辑模式核心
     */
    private fun editKernel(vh: VH, t: T) {
        val hideView = getHideView(vh) ?: throw IllegalArgumentException("未指定HideView!!!")
        if (currentMode == EDIT_MODE) {
            hideView.visibility = View.VISIBLE
            // 如果进入编辑模式，则进入编辑模式核心方法
            touchModeKernel(vh, t)
        } else {
            hideView.visibility = View.GONE
            // 将切换到编辑模式前的 `ItemClickListener`，复原
            if (oldItemClickListener != null) {
                vh.itemView.setOnClickListener {
                    var position: Int = vh.adapterPosition
                    if (position == RecyclerView.NO_POSITION) {
                        return@setOnClickListener
                    }
                    position -= headerLayoutCount
                    setOnItemClickListener(oldItemClickListener)
                    setOnItemClick(it, position)
                }
            }
            // 长按item进入编辑模式，进入的操作由外部实现
            vh.itemView.setOnLongClickListener {
                // 长按item判断下当前所处模式，如果是编辑模式就不响应
                if (!checkEditMode() && null != editSelectedListener) {
                    editSelectedListener?.onLongClickEnterEditMode()
                    appendItemForSelectedList(t)
                    callBackSelectedCount()
                    return@setOnLongClickListener true
                }
                false
            }
        }
    }

    /**
     * 根据当前[getSelectMode]选择模式，调用不用的方案
     */
    private fun touchModeKernel(vh: VH, t: T) {
        when {
            getSelectMode() == SELECT_MODE_PARENT -> {
                selectModeParentKernel(vh, t)
            }
            getSelectMode() == SELECT_MODE_CHILD -> {
                selectModeChildKernel(vh, t)
            }
            else -> {
                throw IllegalArgumentException("未指定触摸模式，重写 `getTouchMode()` 进行指定，" +
                        "可选 `SELECT_MODE_PARENT` 和 `SELECT_MODE_CHILD` !")
            }
        }
    }

    /**
     * 选择模式[SELECT_MODE_PARENT]核心
     */
    private fun selectModeParentKernel(vh: VH, t: T) {
        val itemView: View = vh.itemView
        val checkBox = getCheckBox(vh) ?: throw IllegalArgumentException("未指定CheckBox!!!")
        checkBox.isClickable = false
        checkBox.isChecked = t.isSelected
        // 缓存在 `SHOW_MODE` 时设置的 `OnItemClickListener`, 切换回去后进行还原
        oldItemClickListener = getOnItemClickListener()
        itemView.setOnClickListener {
            val selected = !t.isSelected
            checkBox.isChecked = selected
            processSelected(t, selected)
            callBackSelectedCount()
        }
    }

    /**
     * 选择模式[SELECT_MODE_CHILD]核心
     */
    private fun selectModeChildKernel(vh: VH, t: T) {
        val checkBox = getCheckBox(vh) ?: throw IllegalArgumentException("未指定CheckBox!!!")
        checkBox.isClickable = true
        checkBox.isChecked = t.isSelected
        if (currentMode == EDIT_MODE) {
            checkBox.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                //过滤非人为点击
                if (!buttonView.isPressed) {
                    return@setOnCheckedChangeListener
                }
                processSelected(t, isChecked)
                callBackSelectedCount()
            }
        }
    }

    /**
     * 选择状态设置
     */
    private fun processSelected(t: T, isSelected: Boolean) {
        if (isSelected) {
            appendItemForSelectedList(t)
        } else {
            removeItemForSelectedList(t)
        }
    }

    /**
     * 添加Item到已选择列表[selectedList]
     */
    private fun appendItemForSelectedList(t: T) {
        t.isSelected = true
        val identityHashCode = System.identityHashCode(t)
        if (!selectedList.contains(identityHashCode)) {
            selectedList[identityHashCode] = t
        }
    }

    /**
     * 删除Item从已选择列表[selectedList]
     */
    private fun removeItemForSelectedList(t: T) {
        val identityHashCode = System.identityHashCode(t)
        t.isSelected = false
        selectedList.remove(identityHashCode)
    }

    /**
     * 变更显示模式
     *
     * 显示模式: [SHOW_MODE]
     * 编辑模式: [EDIT_MODE]
     */
    open fun changeMode(mode: Int) {
        // 切换到显示模式，外部 `CheckBox` 复原到未选择状态
        externalCheckBox?.apply {
            if (mode == SHOW_MODE && isChecked) {
                isChecked = false
            }
        }
        currentMode = mode
        restoreUnSelected()
        notifyDataSetChanged()
    }

    /**
     * 恢复所有已选择Item[selectedList]为未选择状态
     */
    private fun restoreUnSelected() {
        if (selectedList.isNotEmpty()) {
            for (selected in selectedList) {
                if (selected.value.isSelected) {
                    selected.value.isSelected = false
                }
            }
            selectedList.clear()
        }
    }

    /**
     * 选择全部Item
     */
    open fun selectedAllItem() {
        if (data.isNotEmpty() && checkEditMode()) {
            for (i in data.indices) {
                val t = data[i]
                appendItemForSelectedList(t)
                notifyItemChanged(i)
            }
            callBackSelectedCount()
        }
    }

    /**
     * 取消选择全部Item
     */
    open fun unSelectedAllItem() {
        if (data.isNotEmpty() && checkEditMode()) {
            for (i in data.indices) {
                val t = data[i]
                removeItemForSelectedList(t)
                notifyItemChanged(i)
            }
            callBackSelectedCount()
        }
    }

    /**
     * 删除选中Item[selectedList]
     */
    open fun removeSelectedItem() {
        if (data.isNotEmpty() && checkEditMode()) {
            // 循环内删除元素需要倒序删除
            for (i in data.indices.reversed()) {
                val t = data[i]
                val identityHashCode = System.identityHashCode(t)
                if (selectedList.contains(identityHashCode)) {
                    selectedList.remove(data.removeAt(i))
                    removeItem(i)
                }
            }
            callBackSelectedCount()
        }
    }

    /**
     * 获取已选择Item[selectedList]的数量
     */
    open fun getSelectedItemCount(): Int {
        return if (selectedList.isNotEmpty() && checkEditMode()) selectedList.size else 0
    }

    /**
     * 删除Item[BaseQuickAdapter.data]
     */
    private fun removeItem(pos: Int) {
        notifyItemRemoved(pos)
        notifyItemRangeChanged(0, itemCount)
    }

    /**
     * 获取是否选择全部数据
     *
     * true: 选择全部
     * false: 未选中全部
     */
    open fun isSelectedAllItem(): Boolean {
        return getSelectedItemCount() == data.size
    }

    /**
     * 回调当前选择数量
     */
    private fun callBackSelectedCount() {
        if (null != editSelectedListener) {
            val count = getSelectedItemCount()
            // 检查是否选中全部，并联动外部 `CheckBox` 状态
            externalCheckBox?.isChecked = count > 0 && count == data.size
            editSelectedListener?.onSelectedItemCount(count)
        }
    }

    /**
     * 检查当前是否处于[EDIT_MODE]编辑模式
     *
     * true: 编辑模式[EDIT_MODE]
     * false: 显示模式[SHOW_MODE]
     */
    private fun checkEditMode(): Boolean {
        return currentMode == EDIT_MODE
    }

    /**
     * 获取已选择的Item[selectedList]
     */
    open fun getSelectedList(): List<Selected> {
        return selectedList.values.toList()
    }

    /**
     * 获取要删除的Item (外部实现)
     *
     * 一般删除item都是需要传给接口id的，可以在适配器中重写此方法，来实现自己的逻辑
     * 获取以选择的item集合可通过[getSelectedList]获得
     *
     */
    open fun getDeleteParams(): String? {
        return null
    }

    /**
     * 绑定外部CheckBox[externalCheckBox]，使其跟随列表联动
     */
    @JvmOverloads
    open fun bindExternalCheckBox(externalCheckBox: CheckBox?, checkedChangeListener: CompoundButton.OnCheckedChangeListener? = null) {
        if (null != externalCheckBox) {
            this.externalCheckBox = externalCheckBox
            externalCheckBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                checkedChangeListener?.onCheckedChanged(buttonView, isChecked)
                // 过滤非任务点击行为
                if (!buttonView.isPressed) return@OnCheckedChangeListener
                if (data.isNotEmpty()) {
                    if (!isChecked || isSelectedAllItem()) {
                        unSelectedAllItem() // 取消全选
                    } else {
                        selectedAllItem() // 全选
                    }
                } else {
                    buttonView.isChecked = false
                }
            })
        }
    }

}