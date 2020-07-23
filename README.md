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

### 使用文档

[使用文档](https://github.com/plain-dev/EditAdapter/wiki/使用文档)

### 更新日志

[更新日志](https://github.com/plain-dev/EditAdapter/wiki/更新日志)

### 其它

- 有问题欢迎提交`issues`

- 图标来源[iconfont](https://www.iconfont.cn/)，侵删

- 感谢[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)，本项目继承自该库。

---

### [LICENSE](https://github.com/plain-dev/EditAdapter/blob/master/LICENSE)
