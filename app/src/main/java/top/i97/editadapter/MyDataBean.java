package top.i97.editadapter;

import top.i97.editadapterlib.entity.SelectedImpl;

/**
 * 演示数据Bean
 *
 * @author Plain
 * @date 2019/12/4 7:06 下午
 */
public class MyDataBean extends SelectedImpl {

    private String title;
    private String content;

    public MyDataBean(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
