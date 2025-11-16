package ru.ssau.tk.swc.labs.entity;

public class AnalFun {
    private Long id;
    private String name;
    private Integer type;

    public AnalFun(){}

    public AnalFun(Long id, String name, Integer type){
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        return "AnalFun{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
