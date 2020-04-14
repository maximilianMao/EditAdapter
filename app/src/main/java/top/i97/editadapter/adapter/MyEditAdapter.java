package top.i97.editadapter.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import top.i97.editadapter.MyDataBean;
import top.i97.editadapter.R;
import top.i97.editadapterlib.adapter.EditAdapter;
import top.i97.editadapterlib.viewholder.BaseEditViewHolder;
import top.i97.editadapterlib.viewholder.EmptyViewHolder;

import java.util.List;

/**
 * 演示适配器（过时⚠️）
 *
 * @author Plain
 * @date 2019/12/4 7:05 下午
 */
@Deprecated
public class MyEditAdapter extends EditAdapter<MyDataBean> {

    private List<MyDataBean> list;

    public MyEditAdapter(List<MyDataBean> list) {
        super(list, R.layout.item_edit,R.layout.item_empty);
        this.list = list;
    }

    public void updateList(List<MyDataBean> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    protected void convert(MyDataBean item, BaseEditViewHolder vh) {
        if (vh instanceof MyViewHolder){
            MyViewHolder viewHolder = (MyViewHolder) vh;
            viewHolder.tvTitle.setText(item.getTitle());
            viewHolder.tvContent.setText(item.getContent());
        }
    }

    @Override
    protected BaseEditViewHolder createViewHolder(View itemView) {
        return new MyViewHolder(itemView);
    }

    @Override
    protected BaseEditViewHolder createEmptyViewHolder(View itemView) {
        return new EmptyViewHolder(itemView);
    }

    @Override
    protected int getTouchMode() {
        //指定触摸选择模式
        return EditAdapter.TOUCH_MODE_ROOT;
    }

    public static class MyViewHolder extends BaseEditViewHolder {

        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvContent)
        TextView tvContent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public View getHideView() {
            return checkBox;
        }

        @Override
        public CheckBox getCheckBox() {
            return checkBox;
        }
    }



}
