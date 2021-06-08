package be.pxl.paj.budgetplanner.service;

import be.pxl.paj.budgetplanner.dao.AccountRepository;
import be.pxl.paj.budgetplanner.dao.PaymentRepository;
import be.pxl.paj.budgetplanner.dto.*;
import be.pxl.paj.budgetplanner.entity.Account;
import be.pxl.paj.budgetplanner.entity.Payment;
import be.pxl.paj.budgetplanner.exception.AccountNotFoundException;
import be.pxl.paj.budgetplanner.exception.DuplicateAccountException;
import be.pxl.paj.budgetplanner.exception.PaymentNotFoundException;
import be.pxl.paj.budgetplanner.reporting.MonthlyReportInformation;
import be.pxl.paj.budgetplanner.reporting.MontlyReportPdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AccountService {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public void createAccount(AccountCreateResource resource) {
        Optional<Account> optionalAccount = accountRepository.findByIban(resource.getIban());
        if (optionalAccount.isPresent()) {
            throw new DuplicateAccountException("There already exists an account with iban [" + resource.getIban() + "]");
        }
        accountRepository.save(new Account(
                resource.getIban(),
                resource.getFirstName(),
                resource.getName()));
    }

    public List<AccountDTO> findAll() {
        return accountRepository.findAll()
                .stream()
                .map(AccountDTO::new)
                .collect(Collectors.toList());
    }

    public void addPayment(Long accountId, PaymentCreateResource resource) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isEmpty()) {
            throw new AccountNotFoundException("Could not find account with id [" + accountId + "]");
        }
        Account account = optionalAccount.get();
        account.addPayment(new Payment(resource.getDate(), resource.getCounterAccount(), resource.getAmount(), resource.getDetail()));
        accountRepository.save(account);
    }

    public PaymentOverviewDTO findPaymentsBy(Long accountId, String category, String fromDate, String toDate) {
        List<Payment> allPayments = paymentRepository.findAllByAccount_Id(accountId);
        if (allPayments.size() == 0) {
            throw new PaymentNotFoundException("Could not find any payments for account id [" + accountId + "]");
        }
        return getPaymentOverviewDTO(category, fromDate, toDate, allPayments);
    }

    private PaymentOverviewDTO getPaymentOverviewDTO(String category, String fromDate, String toDate, List<Payment> allPayments) {
        List<PaymentDTO> paymentDTOS = allPayments.stream()
                .filter(payment -> category == null || payment.getCategory().getName().equals(category))
                .filter(payment -> fromDate == null || payment.getDate().isAfter(LocalDate.parse(fromDate, FORMATTER)))
                .filter(payment -> toDate == null || payment.getDate().isBefore(LocalDate.parse(toDate, FORMATTER)))
                .map(PaymentDTO::new)
                .collect(Collectors.toList());
        PaymentOverviewDTO paymentOverviewDTO = new PaymentOverviewDTO();
        paymentOverviewDTO.setPayments(paymentDTOS);
        double receivingAmount = paymentDTOS.stream()
                .mapToDouble(PaymentDTO::getAmount)
                .filter(amount -> amount > 0)
                .sum();
        double spendingAmount = paymentDTOS.stream()
                .mapToDouble(PaymentDTO::getAmount)
                .filter(amount -> amount < 0)
                .sum();
        paymentOverviewDTO.setReceivingAmount(receivingAmount);
        paymentOverviewDTO.setSpendingAmount(spendingAmount);
        paymentOverviewDTO.setResultAmount(receivingAmount + spendingAmount);
        return paymentOverviewDTO;
    }

    public ByteArrayInputStream generateReport(Long accountId, int year, int month) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isEmpty()) {
            throw new AccountNotFoundException("Could not find account with id [" + accountId + "]");
        }
        List<Payment> allPayments = paymentRepository.findAllByAccount_Id(accountId);
        if (allPayments.isEmpty()) {
            throw new PaymentNotFoundException("Could not find any payments for account id [" + accountId + "]");
        }
        List<Payment> relevantPayments = allPayments.stream().filter(payment -> payment.getDate().getYear() == year && payment.getDate().getMonthValue() == month).collect(Collectors.toList());
        MonthlyReportInformation reportInformation = new MonthlyReportInformation();
        reportInformation.setIncoming(relevantPayments.stream().filter(Payment::isIncome).collect(Collectors.toList()));
        reportInformation.setOutgoing(relevantPayments.stream().filter(Payment::isExpense).collect(Collectors.toList()));
        reportInformation.setFullName(optionalAccount.get().getFirstName() + " " + optionalAccount.get().getName());
        reportInformation.setMonth(Month.of(month));
        reportInformation.setYear(year);
        return MontlyReportPdfWriter.generateDocument(reportInformation);
    }
}
