package top.i97.editadapter;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.ButterKnife;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import top.i97.editadapter.adapter.TestEditAdapter;
import top.i97.editadapter.entity.TestBean;
import top.i97.editadapterlib.adapter.BaseQuickEditModeAdapter;
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

    private List<TestBean> dataBeanList = new ArrayList<>();
    private TestEditAdapter myEditAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

        for (int i = 0; i <= 10; i++) {
            dataBeanList.add(new TestBean("100" + i, "标题" + i));
        }

        myEditAdapter = new TestEditAdapter(dataBeanList);
        myEditAdapter.setEditSelectedListener(new IEditSelectedListener() {
            @Override
            public void onSelectedItemCount(int count) {
                tvCheckItemCount.setText(String.format("共选中%s项", count));
            }

            @Override
            public void onLongClickEnterEditMode() {
                // 长按时进入编辑模式
                enterEditMode();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvList.setLayoutManager(manager);
        rvList.addItemDecoration(new LineItemDecoration(this, LineItemDecoration.VERTICAL_LIST));
        rvList.setAdapter(myEditAdapter);

        smartRefreshLayout.setEnableLoadMore(true);
        smartRefreshLayout.setEnableOverScrollBounce(true);
        smartRefreshLayout.setEnableOverScrollDrag(true);
        // 模拟下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> rvList.postDelayed(() -> {
            myEditAdapter.updateData(getList());
            smartRefreshLayout.finishRefresh(true);
        }, 1000));
        // 模拟上拉加载更多
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> rvList.postDelayed(() -> {
            myEditAdapter.addData(getList());
            smartRefreshLayout.finishLoadMore(true);
        }, 1000));

    }

    /**
     * 随机获取数据
     *
     * @return List<TestBean>
     */
    private List<TestBean> getList() {
        List<TestBean> myDataBeans = new ArrayList<>();
        for (int i = 1; i <= Math.random() * 20; i++) {
            myDataBeans.add(new TestBean("100" + i, "标题" + i));
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
            case BaseQuickEditModeAdapter.SHOW_MODE:
                enterEditMode();
                break;
            case BaseQuickEditModeAdapter.EDIT_MODE:
                enterShowMode();
                break;
        }
    }

    /**
     * 进入展示模式
     */
    private void enterShowMode() {
        // 退出编辑模式后，重新启用下拉刷新和上拉加载更多
        smartRefreshLayout.setEnableRefresh(true);
        smartRefreshLayout.setEnableLoadMore(true);

        myEditAdapter.changeMode(BaseQuickEditModeAdapter.SHOW_MODE);
        edit.setText("编辑");
        rlEditView.setVisibility(View.GONE);
    }

    /**
     * 进入编辑模式
     */
    private void enterEditMode() {
        // 进入编辑模式后，临时关闭下拉刷新和上拉加载更多
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setEnableLoadMore(false);

        myEditAdapter.changeMode(BaseQuickEditModeAdapter.EDIT_MODE);
        edit.setText("完成");
        rlEditView.setVisibility(View.VISIBLE);
    }

    /**
     * 选择全部和反选
     */
    @OnClick(R.id.btnSelectAll)
    public void selectedAllItem() {
        if (myEditAdapter.isSelectedAllItem()) {
            // 反选
            myEditAdapter.unSelectedAllItem();
        } else {
            // 选择全部
            myEditAdapter.selectedAllItem();
        }

    }

    /**
     * 删除选中
     */
    @OnClick(R.id.btnDelete)
    public void deleteSelectedItem() {
        if (0 == myEditAdapter.getSelectedItemCount()) {
            Toast.makeText(this, "请先选择一项!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取删除所需参数
        String deleteParams = myEditAdapter.getDeleteParams();
        Toast.makeText(this, "删除所需参数为: " + deleteParams, Toast.LENGTH_LONG).show();

        // 本地删除
        myEditAdapter.removeSelectedItem();
    }

}
