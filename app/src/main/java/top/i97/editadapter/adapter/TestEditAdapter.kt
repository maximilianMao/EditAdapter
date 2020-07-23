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

package top.i97.editadapter.adapter

import android.view.View
import android.widget.CheckBox
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import top.i97.editadapter.entity.MyDataBean
import top.i97.editadapter.R
import top.i97.editadapterlib.adapter.BaseQuickEditModeAdapter

/**
 * 演示适配器
 *
 * @author Plain
 */
class TestEditAdapter(data: MutableList<MyDataBean>) : BaseQuickEditModeAdapter<MyDataBean, BaseViewHolder>(R.layout.item_test_edit, data) {

    override fun convertView(helper: BaseViewHolder, item: MyDataBean) {
        helper.setText(R.id.tvTitle, item.title)
    }

    override fun getCheckBox(helper: BaseViewHolder): CheckBox? {
        return helper.getView(R.id.checkBox)
    }

    override fun getHideView(helper: BaseViewHolder): View? {
        return helper.getView(R.id.checkBox)
    }

    override fun getDeleteParams(): String? {
        return getSelectedList().let {
            val stringBuilder = StringBuilder()
            for (iSelected in it) {
                if (iSelected is MyDataBean) {
                    // 拼接 `id` 为删除所需参数
                    stringBuilder.append(iSelected.title).append(",")
                }
            }
            stringBuilder.toString()
        }
    }

}