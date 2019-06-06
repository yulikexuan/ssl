//: com.yulikexuan.ssl.m1.lesson6.app.v1.mapper.DateMapper.java


package com.yulikexuan.ssl.m1.lesson6.app.v1.mapper;


import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;


@Component
@Mapper
public class DateMapper {

    static final ZoneOffset ZONE_OFFSET = ZoneOffset.from(OffsetDateTime.now());

    public OffsetDateTime timestampToOffsetDateTime(Timestamp timestamp) {

        return Optional.ofNullable(timestamp)
                .map(ts -> {
                    LocalDateTime ldt = ts.toLocalDateTime();
                    return OffsetDateTime.of(ldt.getYear(),
                            ldt.getMonthValue(),
                            ldt.getDayOfMonth(),
                            ldt.getHour(),
                            ldt.getMinute(),
                            ldt.getSecond(),
                            ldt.getNano(),
                            ZONE_OFFSET);})
                .orElse(null);
    }

    public Timestamp offsetDateTimeToTimestamp(OffsetDateTime offsetDateTime) {

        return Optional.ofNullable(offsetDateTime)
                .map(offsetDateTime1 -> Timestamp.valueOf(offsetDateTime
                        .atZoneSameInstant(ZONE_OFFSET)
                        .toLocalDateTime()))
                .orElse(null);
    }

}///:~