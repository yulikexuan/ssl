//: com.yulikexuan.ssl.app.model.UserListDtoTest.java


package com.yulikexuan.ssl.app.model;


import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


class UserListDtoTest {


    private List<Integer> numberList;

    @BeforeEach
    void setUp() {
        numberList = IntStream.range(0, 10)
                .boxed()
                .collect(Collectors.toList());
    }

    @Test
    void test_Core_Java_Immutable_List() {

        // Given
        List<Integer> immutableList = Collections.unmodifiableList(numberList);

        // When
        numberList.add(11);

        // Then
        assertThat(numberList.size()).isEqualTo(11);
        assertThat(immutableList.size()).isEqualTo(11);
        Assertions.assertThrows(Exception.class, () -> immutableList.add(12));
    }

    @Test
    void test_Guava_Immutable_List() {

        // Given
        List<Integer> immutableList = ImmutableList.copyOf(this.numberList);

        // When
        numberList.add(11);

        // Then
        assertThat(numberList.size()).isEqualTo(11);
        assertThat(immutableList.size()).isEqualTo(10);
        Assertions.assertThrows(Exception.class, () -> immutableList.add(12));
    }

}///:~