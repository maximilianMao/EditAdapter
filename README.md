<div align="center">
  <img src="./app/src/main/ic_launcher-web.png" width='150px' alt="ic_launcher-web">
</div>

## EditAdapter

>适用于RecyclerView的适配器🚥，快速集成列表编辑模式🧾

### 演示

[👉查看演示](screenshot/demo.gif)

### 添加`EditAdapter`到你的项目

1. 在项目的 `build.gradle` 中添加：

```
allprojects {
    repositories {
	    ...
	    maven { url 'https://jitpack.io' }
    }
}
```

2. 添加依赖 [![](https://jitpack.io/v/plain-dev/EditAdapter.svg)](https://jitpack.io/#plain-dev/EditAdapter)

- Android X

```
dependencies {
    // 基于此库，必须引入
    implementation 'com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4'
    // TAG替换为上方的最新版本号
    implementation 'com.github.plain-dev:EditAdapter:TAG'
}
```

- Android Support (1.0.5-stable)

```
dependencies {
    // 基于此库，必须引入
    implementation 'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.50'
    implementation 'com.github.plain-dev:EditAdapter:1.0.5-stable'
}
```

### 如何使用

1. 在你的数据实体类中继承`SelectedImpl`

```java
public class MyDataBean extends SelectedImpl {

    private String title;
    private String content;

    public MyDataBean(String title, String content) {
        this.title = title;
        this.content = content;
    }

  	......
    
}
```

2. 创建一个适配器，继承自`BaseQuickEditModeAdapter<T extends ISelected>` ~~创建一个适配器，继承自`EditAdapter<T extends ISelected>`~~

```java
public class TestEditAdapter extends BaseQuickEditModeAdapter<TestBean, BaseViewHolder> {
    
  	......
      
}
```

3. 在构造方法中设置一些数据和属性

```java
public class TestEditAdapter extends BaseQuickEditModeAdapter<TestBean, BaseViewHolder> {

    // 指定布局和数据
    public TestEditAdapter(@Nullable List<TestBean> data) {
        super(R.layout.item_test_edit, data);
    }

    // 指定CheckBox
    @Override
    public CheckBox getCheckBox(BaseViewHolder helper) {
        return helper.getView(R.id.checkBox);
    }

    // 指定HideView隐藏的区域，一般为CheckBox
    @Override
    public View getHideView(BaseViewHolder helper) {
        return helper.getView(R.id.checkBox);
    }
}
```

4. 数据绑定(和BaseQuickAdapter的使用方法一致)

```java
public class TestEditAdapter extends BaseQuickEditModeAdapter<TestBean, BaseViewHolder> {

    @Override
    protected void convertView(BaseViewHolder helper, TestBean item) {
        helper.setText(R.id.tvTitle, item.getTitle());
    }

}
```

5. 在`Activity`或`Fragment`中更新UI

  - 指定模式为`EDIT_MODE`（进入编辑模式）

    ```java
    myEditAdapter.changeMode(BaseQuickEditModeAdapter.EDIT_MODE);
    ```

  - 指定模式为`SHOW_MODE`（退出编辑模式）

    ```java
    myEditAdapter.changeMode(BaseQuickEditModeAdapter.SHOW_MODE);
    ```

  - 全选所有项

    ```java
    myEditAdapter.selectedAllItem();
    ```

  - 反选所有项

    ```java
    myEditAdapter.unSelectedAllItem();
    ```

  - 删除选择项

    ```java
    myEditAdapter.removeSelectedItem();
    ```

  - 判断是否选择全部 (用来判断点击全选按钮🔘时，应该调用`selectedAllItem`还是`unSelectedAllItem`)

    ```java
    myEditAdapter.isSelectedAllItem()
    ```
    
  - 获取删除item所需的参数 (外部实现)
  
    比如删除接口要求，参数为item的`id`用`,`号隔开的字符串，可以像下面这么实现，在适配器中重写`getDeleteParams`方法
    
    ```java
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
    ```
    
    可以根据接口的要求来实现不同的

6. 注册事件监听器

```java
myEditAdapter.setEditSelectedListener(new IEditSelectedListener() {
  	//回调当前选中项数量
    @Override
    public void onSelectedItemCount(int count) {
        tvCheckItemCount.setText(String.format("共选中%s项", count));
    }
		
  	//回调长按进入编辑模式
    @Override
    public void onLongClickEnterEditMode() {
        myEditAdapter.changeMode(BaseQuickEditModeAdapter.EDIT_MODE);
    }
});
```

7、如果使用下拉刷新控件，记得在进入编辑模式后，关闭下拉刷新，以[SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout)为例

进入编辑模式后

```java
smartRefreshLayout.setEnableRefresh(false);
```

退出编辑模式后

```java
smartRefreshLayout.setEnableRefresh(true);
```

### 其它

<details close>

<summary>
更新日志
</summary>

#### v1.0.4-stable

本次更新内容如下

重构代码，改为继承`BaseQuickAdapter`实现，功能更强大

#### v1.0.3-alpha

本次更新内容如下

- fix:手动选择全部item后，再次点击全选按钮无效的问题
- add:为解决上述bug，新增一个方法判断当前是否选择全部

#### v1.0.2-alpha

本次更新内容如下

- add:长按进入编辑模式
- add:设置空数据视图
- modify:优化代码逻辑

#### v1.0.1-alpha

本次更新内容如下

- fix:滑到最底部删除元素时发生"Inconsistency detected"的问题

#### v1.0-alpha

- ~~首个预览版~~
- 有些问题，请使用新版

</details>

有问题欢迎提交`issues`

图标来源[iconfont](https://www.iconfont.cn/)，侵删

感谢[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)，本项目继承自该库，并遵循[LICENSE](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/blob/master/LICENSE)

---

### [LICENSE](https://github.com/plain-dev/EditAdapter/blob/master/LICENSE)
