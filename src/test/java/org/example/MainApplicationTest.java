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
        CashRequestDto requestDto = new CashRequestDto(
                BigDecimal.valueOf(100L),
                "USD",
                "ext-123"
        );
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

    @Test
    void testFindById() {
        client.get().uri("/transactions/686bea0eb07e487c292ea8e9")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("686bea0eb07e487c292ea8e9")
                .jsonPath("$.status").isEqualTo("POSTED")
                .jsonPath("$.type").isEqualTo("CASH_OUT")
                .jsonPath("$.currency").isEqualTo("USD");
    }

}