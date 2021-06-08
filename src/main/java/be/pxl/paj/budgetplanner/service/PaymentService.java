package be.pxl.paj.budgetplanner.service;

import be.pxl.paj.budgetplanner.dao.CategoryRepository;
import be.pxl.paj.budgetplanner.dao.PaymentRepository;
import be.pxl.paj.budgetplanner.entity.Category;
import be.pxl.paj.budgetplanner.entity.Payment;
import be.pxl.paj.budgetplanner.exception.CategoryNotFoundException;
import be.pxl.paj.budgetplanner.exception.PaymentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CategoryRepository categoryRepository;


    public void linkPayments(Long paymentId, Long labelId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isEmpty()) {
            throw new PaymentNotFoundException("Could not find payment with id [" + paymentId + "]");
        }
        Optional<Category> optionalCategory = categoryRepository.findById(labelId);
        if (optionalCategory.isEmpty()) {
            throw new CategoryNotFoundException("Could not find category with id [" + labelId + "]");
        }
        Payment payment = optionalPayment.get();
        payment.setCategory(optionalCategory.get());
        paymentRepository.save(payment);
    }

    public void deletePayment(Long paymentId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isEmpty()) {
            throw new PaymentNotFoundException("Could not find payment with id [" + paymentId + "]");
        }
        paymentRepository.deleteById(paymentId);
    }
}
