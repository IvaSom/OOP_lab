package ru.ssau.tk.swc.labs.entity;

public class CompFun {
    private Long id;
    private String name;

    public CompFun(){}

    public CompFun(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "CompFun{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
