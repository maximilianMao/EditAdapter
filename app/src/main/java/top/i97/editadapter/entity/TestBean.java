package top.i97.editadapter.entity;

import top.i97.editadapterlib.entity.SelectedImpl;

public class TestBean extends SelectedImpl {

    private String id;
    private String title;

    public TestBean(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
