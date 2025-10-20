package com.ThreeZem.three_zem_back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
class ThreeZemBackApplicationTests {

    Random random = new Random();

    @Test
    void getRandomNumber() {

        for (int i = 0; i <= 100; i++){
            double variation = (random.nextDouble() * 2 - 1) * 0.2;
            double dd = (int) (80 * (1 + variation));
            System.out.println(dd);

        }
    }

}
