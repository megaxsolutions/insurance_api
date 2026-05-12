package com.insurance.policy.controller;

import com.insurance.policy.entity.Policy;
import com.insurance.policy.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired
    private PolicyRepository policyRepository;

    @GetMapping
    public ResponseEntity<List<Policy>> getAllPolicies() {
        return ResponseEntity.ok(policyRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        Optional<Policy> policy = policyRepository.findById(id);
        return policy.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Policy>> getPoliciesByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(policyRepository.findByCustomerId(customerId));
    }

    @PostMapping
    public ResponseEntity<Policy> createPolicy(@RequestBody Policy policy) {
        Policy savedPolicy = policyRepository.save(policy);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPolicy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable Long id, @RequestBody Policy policyDetails) {
        Optional<Policy> policyOptional = policyRepository.findById(id);
        if (policyOptional.isPresent()) {
            Policy policy = policyOptional.get();
            if (policyDetails.getPolicyNumber() != null) policy.setPolicyNumber(policyDetails.getPolicyNumber());
            if (policyDetails.getPolicyType() != null) policy.setPolicyType(policyDetails.getPolicyType());
            if (policyDetails.getPremiumAmount() != null) policy.setPremiumAmount(policyDetails.getPremiumAmount());
            if (policyDetails.getStatus() != null) policy.setStatus(policyDetails.getStatus());
            Policy updatedPolicy = policyRepository.save(policy);
            return ResponseEntity.ok(updatedPolicy);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        if (policyRepository.existsById(id)) {
            policyRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
