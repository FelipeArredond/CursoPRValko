package org.example;

import org.example.dto.CashRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MainApplicationTest {
    @Autowired
    WebTestClient client;

    @Test
    void cashInTest() {
        //Arrange
        CashRequestDto requestDto = new CashRequestDto(
                BigDecimal.valueOf(100L),
                "USD",
                "ext-123"
        );
        //Act
        //Assert
        client.post().uri("/cash-in")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("POSTED");
    }

    @Test
    void testCashOut() {
        Map<String, Object> req = Map.of("amount", 100, "currency", "USD");

        client.post().uri("/cash-out")
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("POSTED");
    }

}