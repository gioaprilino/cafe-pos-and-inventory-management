package com.terracafe.terracafe_backend.repository;

import com.terracafe.terracafe_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Method untuk mencari role berdasarkan nama (contoh method tambahan)
    Optional<Role> findByName(String name);

    // Anda bisa menambahkan method kustom lainnya di sini jika diperlukan di masa depan
    // Contoh:
    List<Role> findByNameContainingIgnoreCase(String name);
}
