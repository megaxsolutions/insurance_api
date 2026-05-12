package com.insurance.claims.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "claim_number", nullable = false, unique = true, length = 50)
    private String claimNumber;
    
    @Column(name = "policy_id", nullable = false)
    private Long policyId;
    
    @Column(name = "claim_type", nullable = false, length = 50)
    private String claimType;
    
    @Column(name = "claim_amount", nullable = false)
    private Double claimAmount;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "submission_date")
    private String submissionDate;
    
    @Column(name = "approval_date")
    private String approvalDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
