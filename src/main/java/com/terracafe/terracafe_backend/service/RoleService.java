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
        // TODO: Add validation (e.g., ensure name is unique)
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        // TODO: Check if role is associated with any users before deleting
        roleRepository.deleteById(id);
    }

    // Optional: Method untuk mencari role berdasarkan nama
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}
