package be.pxl.paj.budgetplanner.rest;

import be.pxl.paj.budgetplanner.dto.*;
import be.pxl.paj.budgetplanner.service.AccountService;
import be.pxl.paj.budgetplanner.upload.BudgetPlannerImporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping(path = "accounts")
public class AccountController {
    private static final Logger LOGGER = LogManager.getLogger(AccountController.class);
    private static final String UPLOAD = "/upload";
    private static final String ADD_GET_PAYMENT = "/{accountId}";
    private static final String PAYMENTS_REPORT = "/{accountId}/report/{year}/{month}/pdf";

    @Autowired
    private BudgetPlannerImporter importer;
    @Autowired
    private AccountService accountService;

    @PostMapping(UPLOAD)
    public ResponseEntity<String> upload(@RequestBody MultipartFile file) {
        LOGGER.info("Reading file");
        importer.readCsv(file);
        return new ResponseEntity<>("File successfully oploaded!", HttpStatus.ACCEPTED);
    }

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody AccountCreateResource resource) {
        LOGGER.info("Creating new account");
        accountService.createAccount(resource);
        return new ResponseEntity<>("Account successfully created!", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        LOGGER.info("Getting all accounts");
        return ResponseEntity.ok().body(accountService.findAll());
    }

    @PostMapping(ADD_GET_PAYMENT)
    public ResponseEntity<Void> addPayment(@RequestParam Long accountId, @RequestBody PaymentCreateResource resource) {
        LOGGER.info("Adding payment");
        accountService.addPayment(accountId, resource);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(ADD_GET_PAYMENT)
    public ResponseEntity<PaymentOverviewDTO> findPaymentsBy(@RequestParam Long accountId,
                                                             @RequestParam(required = false) String category,
                                                             @RequestParam(required = false) String fromDate,
                                                             @RequestParam(required = false) String toDate) {
        LOGGER.info("Getting filtered payments");
        return ResponseEntity.ok().body(accountService.findPaymentsBy(accountId, category, fromDate, toDate));
    }

    @GetMapping(PAYMENTS_REPORT)
    public ResponseEntity<ByteArrayInputStream> paymentsReport(@PathVariable long accountId,
                                                               @PathVariable int year,
                                                               @PathVariable int month) {
        LOGGER.info("Generating report");
        ByteArrayInputStream byteArrayInputStream = accountService.generateReport(accountId, year, month);
        return ResponseEntity.ok().body(byteArrayInputStream);
    }
}
