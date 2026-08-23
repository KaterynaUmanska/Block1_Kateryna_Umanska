package org.example.model;

public class Material {
    private Long id;
    private String name;
    private Unit unit;
    private String description;

    public Material(){

    }

    public Material(Long id, String name, Unit unit, String description) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
