package com.example.Incident.services;


import com.example.Incident.model.Userr;
import com.example.Incident.repo.UserrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class UserrService {

    @Autowired
    private UserrRepository userrRepository;

    public List<Userr> getAllUsers() {
        return userrRepository.findAll();
    }

    public Optional<Userr> getUserById(Long id) {
        return userrRepository.findById(id);
    }

    public Userr addUser(Userr userr) {
        return userrRepository.save(userr);
    }

    public Userr updateUser(Long id, Userr userr) {
        if (userrRepository.existsById(id)) {
            userr.setId(id);
            return userrRepository.save(userr);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userrRepository.deleteById(id);
    }
}
