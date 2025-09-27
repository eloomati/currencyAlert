package io.mhetko.dataprovider.repository;

import io.mhetko.dataprovider.model.Role;
import io.mhetko.dataprovider.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName name);
}
