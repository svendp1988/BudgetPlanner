package be.pxl.paj.budgetplanner.rest;

import be.pxl.paj.budgetplanner.service.PaymentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("payments")
public class PaymentController {
    private static final Logger LOGGER = LogManager.getLogger(PaymentController.class);
    private static final String LINK_PAYMENTS = "/{paymentId}/link/{labelId}";
    private static final String DELETE = "/{paymentId}";

    @Autowired
    private PaymentService paymentService;

    @PostMapping(LINK_PAYMENTS)
    public ResponseEntity<Void> linkPayments(@RequestParam Long paymentId, @RequestParam Long labelId) {
        LOGGER.info("Linking payment with id [" + paymentId + "] to label with id [" + labelId + "]");
        paymentService.linkPayments(paymentId, labelId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(DELETE)
    public ResponseEntity<Void> deletePayment(@RequestParam Long paymentId) {
        LOGGER.info("Deleting payment");
        paymentService.deletePayment(paymentId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
