package store.aurora.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TestController testController;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(testController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void testHandleAllExceptions() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/test/exception"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    // Test Controller for simulating exceptions
    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping("/exception")
        public void throwException() {
            throw new RuntimeException("Test exception");
        }
    }
}