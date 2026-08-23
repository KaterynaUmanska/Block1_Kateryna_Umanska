package org.example.model;

public class PurchaseRequisition {
    private Long id;
    private Material material;
    private Double quantity;
    private Status status;
    private String tags;

    public PurchaseRequisition() {

    }

    public PurchaseRequisition(Long id, Material material, Double quantity, Status status, String tags) {
        this.id = id;
        this.material = material;
        this.quantity = quantity;
        this.status = status;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial (Material material) {this.material = material;}

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

}
