package com.javariga6.yugioh.service;

import com.javariga6.yugioh.model.Role;
import com.javariga6.yugioh.model.User;
import com.javariga6.yugioh.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    public Role getRoleById(Long id) {
        return roleRepository.getOne(id);
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public void deleteRole(Role role) {
        roleRepository.delete(role);
    }

    public void updateRole(Role role) {
        Role roleFromRepo = roleRepository.getOne(role.getId());
        roleFromRepo.setRole(role.getRole());
        roleRepository.save(roleFromRepo);
    }

}
