package be.pxl.paj.budgetplanner.dao;

import be.pxl.paj.budgetplanner.entity.Category;
import be.pxl.paj.budgetplanner.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByCategory(Category category);
    List<Payment> findAllByAccount_Id(Long accountId);
}
