package model;
import java.time.LocalDate;

public class PurchaseRequisition {
    private Integer id;
    private Material material;
    private LocalDate createdAt;
    private Double quantity;
    private LocalDate deliveryDate;
    private Status status;
    private Priority priority;
    private String createdBy;
    private String tags;

    public PurchaseRequisition() {

    }

    public PurchaseRequisition(Integer id, Material material, LocalDate createdAt, Double quantity, LocalDate deliveryDate, Status status, Priority priority, String createdBy, String tags) {
        this.id = id;
        this.material = material;
        this.createdAt = createdAt;
        this.quantity = quantity;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.priority = priority;
        this.createdBy = createdBy;
        this.tags = tags;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
