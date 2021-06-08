package be.pxl.paj.budgetplanner.rest;

import be.pxl.paj.budgetplanner.service.AccountService;
import be.pxl.paj.budgetplanner.upload.BudgetPlannerImporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountService service;
    @MockBean
    private BudgetPlannerImporter importer;

    @Test
    void name() throws Exception {
        when(service.generateReport(anyLong(), anyInt(), anyInt())).thenCallRealMethod();
        mockMvc.perform(get("/accounts/46/report/2021/2/pdf"))
//                .andExpect(status().isAccepted())
                .andExpect(content().string("is Created"));
    }
}