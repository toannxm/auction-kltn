package hvcntt.org.shoppingweb.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hvcntt.org.shoppingweb.dao.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByName(String rolename);
}
