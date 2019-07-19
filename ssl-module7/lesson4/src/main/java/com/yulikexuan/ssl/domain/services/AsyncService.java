//: com.yulikexuan.ssl.domain.services.AsyncService.java


package com.yulikexuan.ssl.domain.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AsyncService {

    @Async
    @Secured("ROLE_ADMIN")
    public void asyncRun() {
        log.info(">>>>>>> [Admin] Running in async way ... ...");
    }

}///:~