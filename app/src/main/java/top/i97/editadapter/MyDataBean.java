package top.i97.editadapter;

import top.i97.editadapterlib.entity.SelectedBean;

/**
 * 演示数据Bean
 *
 * @author Plain
 * @date 2019/12/4 7:06 下午
 */
public class MyDataBean extends SelectedBean {

    private String title;

    public MyDataBean(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
