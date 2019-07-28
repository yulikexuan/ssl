//: com.yulikexuan.ssl.app.model.ActiveUserStore.java


package com.yulikexuan.ssl.app.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;


@Setter
@Getter
@NoArgsConstructor
@Component
public class ActiveUserStore {

    private Collection<String> users = new CopyOnWriteArraySet<>();

}///:~