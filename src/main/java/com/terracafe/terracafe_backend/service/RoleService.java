package com.terracafe.terracafe_backend.service;


import com.terracafe.terracafe_backend.model.Role;
import com.terracafe.terracafe_backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public Role saveRole(Role role) {
        // Add validation (e.g., ensure name is unique and not empty)
        if (role.getName() == null || role.getName().isBlank()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        // Check if role name already exists
        Optional<Role> existingRole = getRoleByName(role.getName());
        if (existingRole.isPresent() && (role.getId() == null || !existingRole.get().getId().equals(role.getId()))) {
            throw new IllegalArgumentException("Role name '" + role.getName() + "' already exists");
        }
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        // Check if role is associated with any users before deleting
        Optional<Role> roleOpt = roleRepository.findById(id);
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            if (role.getUsers() != null && !role.getUsers().isEmpty()) {
                throw new RuntimeException("Cannot delete role '" + role.getName() + "' because it has " + 
                                         role.getUsers().size() + " associated user(s). Please delete or reassign the users first.");
            }
            roleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Role not found with id: " + id);
        }
    }

    // Optional: Method untuk mencari role berdasarkan nama
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}
