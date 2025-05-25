package com.asthmatick1dd0.TeleBotJava.service;

import com.asthmatick1dd0.TeleBotJava.repository.UserResponseRepository;
import org.springframework.stereotype.Service;

@Service
public class FormService {

    private final UserResponseRepository userResponseRepository;
    private final StateService stateService;

    public FormService(UserResponseRepository userResponseRepository, StateService stateService) {
        this.userResponseRepository = userResponseRepository;
        this.stateService = stateService;
    }

    public String startForm(Long userId) {
        stateService.setState(userId, StateService.State.WAITING_NAME);
        return "Пожалуйста, введите ваше имя";
    }

}
