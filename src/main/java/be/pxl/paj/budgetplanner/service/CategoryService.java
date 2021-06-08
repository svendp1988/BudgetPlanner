package be.pxl.paj.budgetplanner.service;

import be.pxl.paj.budgetplanner.dao.CategoryRepository;
import be.pxl.paj.budgetplanner.dao.PaymentRepository;
import be.pxl.paj.budgetplanner.dto.CategoryCreateResource;
import be.pxl.paj.budgetplanner.dto.CategoryDTO;
import be.pxl.paj.budgetplanner.entity.Category;
import be.pxl.paj.budgetplanner.entity.Payment;
import be.pxl.paj.budgetplanner.exception.CategoryInUseException;
import be.pxl.paj.budgetplanner.exception.DuplicateCategoryException;
import be.pxl.paj.budgetplanner.exception.UnknownCategoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    public void addLabel(CategoryCreateResource resource) {
        String label = upperCaseFirstLetter(resource);
        Optional<Category> optionalCategory = categoryRepository.findByName(label);
        if (optionalCategory.isPresent()) {
            throw new DuplicateCategoryException("there already exists a label with name [" + label + "]");
        }
        categoryRepository.save(new Category(label));
    }

    private String upperCaseFirstLetter(CategoryCreateResource resource) {
        return resource.getName().substring(0, 1).toUpperCase() + resource.getName().substring(1);
    }

    public void removeLabel(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            throw new UnknownCategoryException("Could not find Category with id [" + id + "]");
        }
        List<Payment> paymentsByCategory = paymentRepository.findAllByCategory(optionalCategory.get());
        if (paymentsByCategory.size() > 0) {
            throw new CategoryInUseException("Label [" + optionalCategory.get().getName() + "] is in use. Remove the payments first or change their label.");
        }
        categoryRepository.delete(optionalCategory.get());
    }
}
