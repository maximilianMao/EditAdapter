package top.i97.editadapter;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import top.i97.editadapterlib.adapter.EditAdapter;
import top.i97.editadapterlib.inter.IEditSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.edit)
    TextView edit;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.rvList)
    RecyclerView rvList;
    @BindView(R.id.rlEditView)
    RelativeLayout rlEditView;
    @BindView(R.id.tvCheckItemCount)
    TextView tvCheckItemCount;
    @BindView(R.id.btnSelectAll)
    Button btnSelectAll;
    @BindView(R.id.btnDelete)
    Button btnDelete;

    private List<MyDataBean> dataBeanList = new ArrayList<>();
    private MyEditAdapter myEditAdapter;
    private boolean isSelectedAllMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

        for (int i = 0; i <= 30; i++) {
            dataBeanList.add(new MyDataBean("标题" + i));
        }

        myEditAdapter = new MyEditAdapter(dataBeanList);
        myEditAdapter.setEditSelectedListener(new IEditSelectedListener() {
            @Override
            public void onSelectedItemCount(int count) {
                tvCheckItemCount.setText(String.format("共选中%s项", count));
            }

            @Override
            public void onLongClickEnterEditMode() {
                enterEditMode();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvList.setLayoutManager(manager);
        rvList.setAdapter(myEditAdapter);

        //模拟下拉刷新
        smartRefreshLayout.setEnableOverScrollBounce(true);
        smartRefreshLayout.setEnableOverScrollDrag(true);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> rvList.postDelayed(() -> {
            myEditAdapter.updateList(getList());
            smartRefreshLayout.finishRefresh(true);
        }, 1000));

    }

    private List<MyDataBean> getList() {
        List<MyDataBean> myDataBeans = new ArrayList<>();
        for (int i = 1; i <= Math.random() * 200 + 1; i++) {
            myDataBeans.add(new MyDataBean("标题" + i));
        }
        return myDataBeans;
    }

    /**
     * 编辑模式切换
     */
    @OnClick(R.id.edit)
    public void changeEditMode() {
        tvCheckItemCount.setText("共选中0项");
        int curShowMode = myEditAdapter.getCurMode();
        Log.d(TAG, "当前模式: " + curShowMode);
        switch (curShowMode) {
            case EditAdapter.SHOW_MODE:
                enterEditMode();
                break;
            case EditAdapter.EDIT_MODE:
                enterShowMode();
                break;
        }
    }

    private void enterShowMode(){
        smartRefreshLayout.setEnableRefresh(true);
        myEditAdapter.changeMode(EditAdapter.SHOW_MODE);
        edit.setText("编辑");
        rlEditView.setVisibility(View.GONE);
    }

    private void enterEditMode(){
        smartRefreshLayout.setEnableRefresh(false);
        myEditAdapter.changeMode(EditAdapter.EDIT_MODE);
        edit.setText("完成");
        rlEditView.setVisibility(View.VISIBLE);
    }

    /**
     * 选择全部和反选
     */
    @OnClick(R.id.btnSelectAll)
    public void selectedAllItem() {
        if (isSelectedAllMode) {
            isSelectedAllMode = false;
            myEditAdapter.unSelectedAllItem();
        } else {
            isSelectedAllMode = true;
            myEditAdapter.selectedAllItem();
        }

    }

    /**
     * 删除选中
     */
    @OnClick(R.id.btnDelete)
    public void deleteSelectedItem() {
        myEditAdapter.removeSelectedItem();
    }

}
