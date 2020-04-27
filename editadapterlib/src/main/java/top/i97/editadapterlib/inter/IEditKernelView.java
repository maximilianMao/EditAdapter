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

package top.i97.editadapterlib.inter;

import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseViewHolder;

/**
 * name: IEditKernelView
 * desc: 编辑模式适配器需要实现该接口，用来获取CheckBox视图和隐藏区域
 * date: 2020/4/14 1:34 PM
 * version: v1.0
 * author: Plain
 * blog: https://plain-dev.com
 * email: support@plain-dev.com
 */
public interface IEditKernelView<K extends BaseViewHolder> {

    /**
     * 获取要隐藏的View
     *
     * @return View
     */
    View getHideView(K helper);

    /**
     * 获取CheckBox
     *
     * @return CheckBox
     */
    CheckBox getCheckBox(K helper);

}
