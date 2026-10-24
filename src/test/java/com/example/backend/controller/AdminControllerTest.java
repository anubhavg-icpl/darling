package com.example.backend.controller;

import com.example.backend.dto.GlobalMetricsDto;
import com.example.backend.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    @DisplayName("Unauthenticated user receives 401 or 403")
    void unauthenticatedAccessIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/admin/global-metrics"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("User with ROLE_USER receives 403 Forbidden on admin endpoints")
    void roleUserGetsForbiddenOnAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/admin/global-metrics").with(user("morty@c137.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User with ROLE_ADMIN gets access (200 OK)")
    void roleAdminGetsOkOnAdminEndpoints() throws Exception {
        when(adminService.getGlobalMetrics()).thenReturn(GlobalMetricsDto.builder()
                .totalUsers(10)
                .totalRelapsesRecorded(3)
                .build());

        mockMvc.perform(get("/api/v1/admin/global-metrics").with(user("rickprime@omega.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
