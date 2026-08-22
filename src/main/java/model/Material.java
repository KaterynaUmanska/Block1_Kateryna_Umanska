package model;

public class Material {
    private int id;
    private String name;
    private String sku;
    private Unit unit;
    private Double currentStock;
    private Double minStock;
    private String description;

    public Material(){

    }

    public Material(int id, String name, String sku, Unit unit, Double currentStock, Double minStock, String description) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.unit = unit;
        this.currentStock = currentStock;
        this.minStock = minStock;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Double getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Double currentStock) {
        this.currentStock = currentStock;
    }

    public Double getMinStock() {
        return minStock;
    }

    public void setMinStock(Double minStock) {
        this.minStock = minStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
