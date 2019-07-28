//: com.yulikexuan.ssl.app.events.LoggedInUserListener.java


package com.yulikexuan.ssl.app.events;


import com.yulikexuan.ssl.app.model.ActiveUserStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.Collection;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class LoggedInUserListener implements HttpSessionBindingListener {

    private String username;
    private ActiveUserStore activeUserStore;

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        Collection<String> users = this.activeUserStore.getUsers();
        LoggedInUserListener user = (LoggedInUserListener) event.getValue();
        if (!users.contains(user.getUsername())) {
            users.add(user.getUsername());
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        Collection<String> users = activeUserStore.getUsers();
        LoggedInUserListener user = (LoggedInUserListener) event.getValue();
        if (users.contains(user.getUsername())) {
            users.remove(user.getUsername());
        }
    }

}///:~