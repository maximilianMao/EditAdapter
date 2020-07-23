package top.i97.editadapter

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_main.*
import top.i97.editadapter.adapter.TestEditAdapter
import top.i97.editadapter.entity.MyDataBean
import top.i97.editadapter.view.LineItemDecoration
import top.i97.editadapterlib.adapter.BaseQuickEditModeAdapter
import top.i97.editadapterlib.inter.IEditSelectedListener
import java.util.*

class MainActivity : AppCompatActivity() {

    private val dataBeanList: MutableList<MyDataBean> = mutableListOf()

    /**
     * 随机获取数据
     */
    private val list: List<MyDataBean>
        get() {
            val myDataBeans: MutableList<MyDataBean> = ArrayList()
            var i = 1
            while (i <= Math.random() * 20) {
                myDataBeans.add(MyDataBean("100$i", "标题$i"))
                i++
            }
            return myDataBeans
        }

    private val myEditAdapter by lazy {
        TestEditAdapter(dataBeanList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initListener()
    }

    private fun init() {

        // 模拟数据
        for (i in 0..10) {
            dataBeanList.add(MyDataBean("100$i", "标题$i"))
        }

        myEditAdapter.apply {
            editSelectedListener = object : IEditSelectedListener {
                override fun onSelectedItemCount(count: Int) {
                    tvCheckItemCount.text = String.format("共选中%s项", count)
                }

                override fun onLongClickEnterEditMode() {
                    Toast.makeText(this@MainActivity, "长按进入编辑模式", Toast.LENGTH_SHORT).show()
                    enterEditMode()
                }
            }
            setOnItemClickListener { _, _, position -> Toast.makeText(this@MainActivity, "position: $position", Toast.LENGTH_SHORT).show() }
            addChildClickViewIds(R.id.tvTitle)
            setOnItemChildClickListener { _: BaseQuickAdapter<*, *>?, view: View, position: Int ->
                // 判断一下当前模式
                if (myEditAdapter.currentMode == BaseQuickEditModeAdapter.EDIT_MODE) return@setOnItemChildClickListener
                if (view.id == R.id.tvTitle) {
                    Toast.makeText(this@MainActivity, "title: " + dataBeanList[position].title, Toast.LENGTH_SHORT).show()
                }
            }
            bindExternalCheckBox(checkBox)
        }

        rvList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(LineItemDecoration(this@MainActivity, LineItemDecoration.VERTICAL_LIST))
            adapter = myEditAdapter
        }

        smartRefreshLayout.apply {
            setEnableLoadMore(true)
            setEnableOverScrollBounce(true)
            setEnableOverScrollDrag(true)
            // 模拟下拉刷新
            setOnRefreshListener {
                rvList.postDelayed({
                    myEditAdapter.setList(list)
                    smartRefreshLayout!!.finishRefresh(true)
                }, 1000)
            }
            // 模拟上拉加载更多
            setOnLoadMoreListener {
                rvList.postDelayed({
                    myEditAdapter.addData(list)
                    smartRefreshLayout!!.finishLoadMore(true)
                }, 1000)
            }
        }

    }

    private fun initListener() {
        edit.setOnClickListener { changeEditMode() }
        btnDelete.setOnClickListener { deleteSelectedItem() }
    }

    /**
     * 编辑模式切换
     */
    private fun changeEditMode() {
        tvCheckItemCount.text = "共选中0项"
        val curShowMode = myEditAdapter.currentMode
        Log.d(TAG, "当前模式: $curShowMode")
        when (curShowMode) {
            BaseQuickEditModeAdapter.SHOW_MODE -> enterEditMode()
            BaseQuickEditModeAdapter.EDIT_MODE -> enterShowMode()
        }
    }

    /**
     * 进入展示模式
     */
    private fun enterShowMode() {
        // 退出编辑模式后，重新启用下拉刷新和上拉加载更多
        smartRefreshLayout.apply {
            setEnableRefresh(true)
            setEnableLoadMore(true)
        }
        myEditAdapter.changeMode(BaseQuickEditModeAdapter.SHOW_MODE)
        edit.text = "编辑"
        rlEditView.visibility = View.GONE
    }

    /**
     * 进入编辑模式
     */
    private fun enterEditMode() {
        // 进入编辑模式后，临时关闭下拉刷新和上拉加载更多
        smartRefreshLayout.apply {
            setEnableRefresh(false)
            setEnableLoadMore(false)
        }
        myEditAdapter.changeMode(BaseQuickEditModeAdapter.EDIT_MODE)
        edit.text = "完成"
        rlEditView.visibility = View.VISIBLE
    }

    /**
     * 删除选中
     */
    private fun deleteSelectedItem() {
        if (0 == myEditAdapter.getSelectedItemCount()) {
            Toast.makeText(this, "请先选择一项!!!", Toast.LENGTH_SHORT).show()
            return
        }

        // 获取删除所需参数
        val deleteParams = myEditAdapter.getDeleteParams()
        Toast.makeText(this, "删除所需参数为: $deleteParams", Toast.LENGTH_LONG).show()

        // 本地删除
        myEditAdapter.removeSelectedItem()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

}