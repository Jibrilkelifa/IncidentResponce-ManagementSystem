package com.example.Incident.services;

import com.example.Incident.model.Group;
import com.example.Incident.repo.GroupRepository;
import com.example.Incident.repo.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }


    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }
}
