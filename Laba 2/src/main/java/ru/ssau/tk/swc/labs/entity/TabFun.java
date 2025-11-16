package ru.ssau.tk.swc.labs.entity;

public class TabFun {
    private Long id;
    private String type;

    public TabFun(){}

    public TabFun(Long id, String type){
        this.id = id;
        this.type = type;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TabFun{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }
}
