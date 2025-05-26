package com.asthmatick1dd0.TeleBotJava.service;

import com.asthmatick1dd0.TeleBotJava.model.UserResponse;
import com.asthmatick1dd0.TeleBotJava.repository.UserResponseRepository;
import com.asthmatick1dd0.TeleBotJava.util.EmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FormService {

    private final UserResponseRepository userResponseRepository;
    private final StateService stateService;

    public FormService(UserResponseRepository userResponseRepository, StateService stateService) {
        this.userResponseRepository = userResponseRepository;
        this.stateService = stateService;
    }

    /** Method for start /form command */
    public String startForm(Long userId) {
        stateService.setState(userId, StateService.State.WAITING_NAME);
        log.info("State is " + stateService.getState(userId));
        return "Пожалуйста, введите ваше имя";
    }
    /** Handle user's name, email and score step-by-step using state */
    public String handleSteps(Long userId, String message) {
        StateService.State state = stateService.getState(userId);
        UserResponse response = stateService.getTempResponse(userId);

        switch (state) {
            case WAITING_NAME:
                if (message.trim().length() > 60) {
                    return "Превышен лимит символов. Попробуйте снова:";
                }
                response.setName(message.trim());
                stateService.setState(userId, StateService.State.WAITING_EMAIL);
                log.info("User's " + response.getName() + " state is " + stateService.getState(userId));
                return "Введите ваш email:";
            case WAITING_EMAIL:
                if (!EmailValidator.isValid(message.trim())) {
                    return "Вы ввели некорректный email. Попробуйте снова:";
                }
                response.setEmail(message.trim());
                stateService.setState(userId, StateService.State.WAITING_SCORE);
                log.info("User's " + response.getName() + " state is " + stateService.getState(userId));
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
                    log.info("Form filling completed by user " + response.getName());
                    return "Спасибо! Ваша форма отправлена. Если хотите увидеть отчёт по пройденным опросам, то напишите /rreport";
                } catch (NumberFormatException e) {
                    return "Введите число от 1 до 10:";
                }
            default:
                return "Для начала опроса используйте /form";
        }
    }
}
