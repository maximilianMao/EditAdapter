package top.i97.editadapter.adapter;

import androidx.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import java.util.List;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import top.i97.editadapter.R;
import top.i97.editadapter.entity.TestBean;
import top.i97.editadapterlib.adapter.BaseQuickEditModeAdapter;
import top.i97.editadapterlib.inter.ISelected;
import top.i97.editadapterlib.util.ListUtils;

public class TestEditAdapter extends BaseQuickEditModeAdapter<TestBean, BaseViewHolder> {

    public TestEditAdapter(@Nullable List<TestBean> data) {
        super(R.layout.item_test_edit, data);
    }

    @Override
    protected void convertView(BaseViewHolder helper, TestBean item) {
        helper.setText(R.id.tvTitle, item.getTitle());
    }

    @Override
    public String getDeleteParams() {
        List<ISelected> selectedList = getSelectedList();
        if (!ListUtils.isEmpty(selectedList)) {
            StringBuilder sb = new StringBuilder();
            for (ISelected iSelected : selectedList) {
                if (iSelected instanceof TestBean) {
                    sb.append(((TestBean) iSelected).getId()).append(",");
                }
            }
            return sb.toString();
        }
        return null;
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
