package top.i97.editadapter.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import top.i97.editadapter.R;
import top.i97.editadapter.entity.TestBean;
import top.i97.editadapterlib.adapter.BaseQuickEditModeAdapter;

public class TestEditAdapter extends BaseQuickEditModeAdapter<TestBean, BaseViewHolder> {

    public TestEditAdapter(@Nullable List<TestBean> data) {
        super(R.layout.item_test_edit, data);
    }

    @Override
    protected void convertView(BaseViewHolder helper, TestBean item) {
        helper.setText(R.id.tvTitle, item.getTitle());
    }

    @Override
    public CheckBox getCheckBox(BaseViewHolder helper) {
        return helper.getView(R.id.checkBox);
    }

    @Override
    public View getHideView(BaseViewHolder helper) {
        return helper.getView(R.id.checkBox);
    }
}
