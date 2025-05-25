package com.asthmatick1dd0.TeleBotJava.service;

import com.asthmatick1dd0.TeleBotJava.model.UserResponse;
import com.asthmatick1dd0.TeleBotJava.repository.UserResponseRepository;
import com.asthmatick1dd0.TeleBotJava.util.EmailValidator;
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

    public String handleSteps(Long userId, String message) {
        StateService.State state = stateService.getState(userId);
        UserResponse response = stateService.getTempResponse(userId);

        switch (state) {
            case WAITING_NAME:
                if (message.trim().length() > 254) {
                    return "Превышен лимит символов. Попробуйте снова:";
                }
                response.setName(message.trim());
                stateService.setState(userId, StateService.State.WAITING_EMAIL);
                return "Введите ваш email:";
            case WAITING_EMAIL:
                if (!EmailValidator.isValid(message.trim())) {
                    return "Вы ввели некорректный email. Попробуйте снова:";
                }
                response.setEmail(message.trim());
                stateService.setState(userId, StateService.State.WAITING_SCORE);
                return "Поставьте оценку от 1 до 10";
            case WAITING_SCORE:
                try {
                    int score = Integer.parseInt(message.trim());
                    if (score < 1 || score > 10) {
                        return "Введите число от 1 до 10:";
                    }
                    response.setScore(score);
                    userResponseRepository.save(response);
                    stateService.clearState(userId);
                    return "Спасибо! Ваша форма отправлена.";
                } catch (NumberFormatException e) {
                    return "Введите число от 1 до 10:";
                }
            default:
                return "Для начала опроса используйте /form";

        }
    }
}
