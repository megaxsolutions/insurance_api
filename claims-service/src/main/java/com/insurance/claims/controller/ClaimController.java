package com.insurance.claims.controller;

import com.insurance.claims.entity.Claim;
import com.insurance.claims.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired
    private ClaimRepository claimRepository;

    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims() {
        return ResponseEntity.ok(claimRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long id) {
        Optional<Claim> claim = claimRepository.findById(id);
        return claim.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/policy/{policyId}")
    public ResponseEntity<List<Claim>> getClaimsByPolicyId(@PathVariable Long policyId) {
        return ResponseEntity.ok(claimRepository.findByPolicyId(policyId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Claim>> getClaimsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(claimRepository.findByStatus(status));
    }

    @PostMapping
    public ResponseEntity<Claim> createClaim(@RequestBody Claim claim) {
        Claim savedClaim = claimRepository.save(claim);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClaim);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Claim> updateClaim(@PathVariable Long id, @RequestBody Claim claimDetails) {
        Optional<Claim> claimOptional = claimRepository.findById(id);
        if (claimOptional.isPresent()) {
            Claim claim = claimOptional.get();
            if (claimDetails.getClaimNumber() != null) claim.setClaimNumber(claimDetails.getClaimNumber());
            if (claimDetails.getClaimType() != null) claim.setClaimType(claimDetails.getClaimType());
            if (claimDetails.getClaimAmount() != null) claim.setClaimAmount(claimDetails.getClaimAmount());
            if (claimDetails.getStatus() != null) claim.setStatus(claimDetails.getStatus());
            Claim updatedClaim = claimRepository.save(claim);
            return ResponseEntity.ok(updatedClaim);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        if (claimRepository.existsById(id)) {
            claimRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
