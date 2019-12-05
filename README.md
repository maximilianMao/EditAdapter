<div align="center">
  <img src="./app/src/main/ic_launcher-web.png" width='150px' alt="ic_launcher-web">
</div>

## EditAdapter

>适用于RecyclerView的适配器🚥，快速集成列表编辑模式🧾

### 演示

<img src='screenshot/demo.gif' width = '300' style="float:left"   />

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

```
//TAG替换为上方的最新版本号
dependencies {
    implementation 'com.github.plain-dev:EditAdapter:Tag'
}
```

### 如何使用

1. 在你的数据实体类中继承`SelectedBean`

```java
public class MyDataBean extends SelectedBean {

    private String title;
    private String content;

    public MyDataBean(String title, String content) {
        this.title = title;
        this.content = content;
    }

  	......
    
}
```

2. 创建一个适配器，继承自`EditAdapter<T extends ISelected>`

```java
public class MyEditAdapter extends EditAdapter<MyDataBean> {
    
  	......
      
}
```

3. 在构造方法中指定数据和布局

```java
public MyEditAdapter(List<MyDataBean> list) {
    super(list, R.layout.item_edit,R.layout.item_empty);
}
```

4. 创建数据视图和空数据视图

```java
@Override
protected BaseEditViewHolder createViewHolder(View itemView) {
    return new MyViewHolder(itemView);
}

@Override
protected BaseEditViewHolder createEmptyViewHolder(View itemView) {
    return new EmptyViewHolder(itemView);
}
```

5. 指定触摸模式

```java
@Override
protected int getTouchMode() {
    //指定触摸选择模式
    return EditAdapter.TOUCH_MODE_ROOT;
}
```

6. 数据绑定

```java
@Override
protected void convert(MyDataBean item, BaseEditViewHolder vh) {
    if (vh instanceof MyViewHolder){
        MyViewHolder viewHolder = (MyViewHolder) vh;
        viewHolder.tvTitle.setText(item.getTitle());
        viewHolder.tvContent.setText(item.getContent());
    }
}
```

7、自定义ViewHolder，继承`BaseEditViewHolder`，并重写`getHideView`和`getCheckBox`方法，返回隐藏区域View和选择按钮🔘

```java
public static class MyViewHolder extends BaseEditViewHolder {

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        ......
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
```

### 其它

1. 有问题欢迎提交`issues`

2. 状态图标来源[iconfont](https://www.iconfont.cn/)，侵删

