package top.i97.editadapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import top.i97.editadapterlib.adapter.EditAdapter;

import java.util.List;

/**
 * 演示适配器
 *
 * @author Plain
 * @date 2019/12/4 7:05 下午
 */
public class MyEditAdapter extends EditAdapter<MyDataBean, MyEditAdapter.MyViewHolder> {

    private List<MyDataBean> list;

    public MyEditAdapter(List<MyDataBean> list) {
        super(list, R.layout.item_edit);
        this.list = list;
    }

    public void updateList(List<MyDataBean> newList){
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    public MyEditAdapter(List<MyDataBean> list, int layoutId) {
        super(list, layoutId);
    }

    @Override
    protected void convert(MyDataBean item, MyViewHolder viewHolder) {
        viewHolder.tvTitle.setText(item.getTitle());
    }

    @Override
    protected MyViewHolder createViewHolder(View itemView) {
        return new MyViewHolder(itemView);
    }

    @Override
    protected int getTouchMode() {
        //指定触摸选择模式
        return EditAdapter.TOUCH_MODE_ROOT;
    }

    public static class MyViewHolder extends EditAdapter.EditViewHolder {

        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected View getHideView() {
            return checkBox;
        }

        @Override
        protected CheckBox getCheckBox() {
            return checkBox;
        }
    }

}
