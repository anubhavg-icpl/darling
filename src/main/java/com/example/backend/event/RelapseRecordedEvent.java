package com.example.backend.event;

import com.example.backend.model.NoContactRelapse;
import com.example.backend.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RelapseRecordedEvent extends ApplicationEvent {

    private final User user;
    private final NoContactRelapse relapse;

    public RelapseRecordedEvent(Object source, User user, NoContactRelapse relapse) {
        super(source);
        this.user = user;
        this.relapse = relapse;
    }
}
