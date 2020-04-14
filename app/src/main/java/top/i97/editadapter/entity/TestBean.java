package top.i97.editadapter.entity;

import top.i97.editadapterlib.entity.SelectedImpl;

public class TestBean extends SelectedImpl {

    private String title;

    public TestBean(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
