package com.asthmatick1dd0.TeleBotJava.service;

import com.asthmatick1dd0.TeleBotJava.model.UserResponse;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StateService {

    /** Enumerator that describes current user state while filling form */
    public enum State {
        NONE, WAITING_NAME, WAITING_EMAIL, WAITING_SCORE
    }

    private final Map<Long, State> userStates = new ConcurrentHashMap<>();
    private final Map<Long, UserResponse> tempResponses = new ConcurrentHashMap<>();
    public void setState(Long userId, State state) {
        userStates.put(userId, state);
        if (state == State.WAITING_NAME) {
            tempResponses.put(userId, new UserResponse());
            tempResponses.get(userId).setTelegramId(userId);
        }
    }

    public State getState(Long userId) {
        return userStates.getOrDefault(userId, State.NONE);
    }

    /** We need this to fill UserResponseRepository later */
    public UserResponse getTempResponse(Long userId) {
        return tempResponses.computeIfAbsent(userId, k -> new UserResponse());
    }

    public void clearState(Long userId) {
        userStates.remove(userId);
        tempResponses.remove(userId);
    }
}
