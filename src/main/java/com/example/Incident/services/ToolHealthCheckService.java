package com.example.Incident.services;

import com.example.Incident.model.ToolCheckItem;
import com.example.Incident.model.ToolHealthCheckDTO;
import com.example.Incident.model.ToolHealthCheckSession;
import com.example.Incident.repo.ToolHealthCheckSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToolHealthCheckService {

    private final ToolHealthCheckSessionRepository sessionRepo;

    public ToolHealthCheckSession saveSession(ToolHealthCheckDTO dto) {
        ToolHealthCheckSession session = new ToolHealthCheckSession();
        session.setAnalystName(dto.getAnalystName());
        session.setShiftTime(dto.getShiftTime());
        session.setSubmittedAt(LocalDateTime.now());

        List<ToolCheckItem> items = dto.getItems().stream().map(itemDto -> {
            ToolCheckItem item = new ToolCheckItem();
            item.setToolName(itemDto.getToolName());
            item.setCheckItem(itemDto.getCheckItem());
            item.setResponse(itemDto.getResponse());
            item.setSession(session);
            return item;
        }).collect(Collectors.toList());

        session.setItems(items);
        return sessionRepo.save(session);
    }
}

