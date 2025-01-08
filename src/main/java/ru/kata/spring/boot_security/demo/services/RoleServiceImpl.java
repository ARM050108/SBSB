package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.List;

@Transactional
@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleRepository.getById(id);
    }

    @Override
    public void save(Role role) {
        roleRepository.save(role);
    }

    @Override
    public void delete(Long id) {
        roleRepository.delete(roleRepository.getById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Role> getDemandedRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleByName(String roleAdmin) {
        return null;
    }

    @Override
    public Role findByName(String roleAdmin) {
        return null;
    }
}