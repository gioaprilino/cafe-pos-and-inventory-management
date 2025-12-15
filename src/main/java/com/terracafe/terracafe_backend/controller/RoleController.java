package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.model.Role;
import com.terracafe.terracafe_backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles") // Base path untuk endpoint role
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        // TODO: Add authorization check (Manager only)
        // TODO: Add validation (@Valid)
        try {
            Role savedRole = roleService.saveRole(role);
            return ResponseEntity.ok(savedRole);
        } catch (Exception e) {
            // Handle potential errors like duplicate name
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role roleDetails) {
        // TODO: Add authorization check (Manager only)
        Optional<Role> existingRoleOpt = roleService.getRoleById(id);
        if (existingRoleOpt.isPresent()) {
            Role existingRole = existingRoleOpt.get();
            
            // Update hanya field yang tidak null
            if (roleDetails.getName() != null) {
                if (roleDetails.getName().isBlank()) {
                    return ResponseEntity.badRequest().build(); // Reject empty name
                }
                existingRole.setName(roleDetails.getName());
            }
            
            try {
                Role updatedRole = roleService.saveRole(existingRole);
                return ResponseEntity.ok(updatedRole);
            } catch (Exception e) {
                // Handle potential errors like duplicate name
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        // TODO: Add authorization check (Manager only)
        // TODO: Check if role is associated with users before deletion
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok("Role deleted successfully");
        } catch (RuntimeException e) {
            // Handle error if role is associated with users
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
