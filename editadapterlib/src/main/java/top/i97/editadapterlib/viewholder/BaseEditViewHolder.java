package top.i97.editadapterlib.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import top.i97.editadapterlib.inter.IEditView;

/**
 * 基础编辑模式ViewHolder
 *
 * @author Plain
 * @date 2019/12/5 2:09 下午
 */
public class BaseEditViewHolder extends RecyclerView.ViewHolder implements IEditView {

    public BaseEditViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public View getHideView() {
        return null;
    }

    @Override
    public CheckBox getCheckBox() {
        return null;
    }
}
