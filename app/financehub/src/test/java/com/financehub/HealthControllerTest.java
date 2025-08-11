package com.financehub;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.financehub.controller.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = HealthController.class)
class HealthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void health_shouldReturnOk() throws Exception {
    mockMvc.perform(get("/health")).andExpect(status().isOk());
  }
}


