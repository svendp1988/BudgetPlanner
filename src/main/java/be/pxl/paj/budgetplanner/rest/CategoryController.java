package be.pxl.paj.budgetplanner.rest;

import be.pxl.paj.budgetplanner.dto.CategoryCreateResource;
import be.pxl.paj.budgetplanner.dto.CategoryDTO;
import be.pxl.paj.budgetplanner.service.CategoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("categories")
public class CategoryController {
    private static final Logger LOGGER = LogManager.getLogger(CategoryController.class);
    private static final String DELETE = "/{id}";

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll() {
        LOGGER.info("Getting all categories");
        return ResponseEntity.ok().body(categoryService.findAll());
    }

    @PostMapping
    public ResponseEntity<Void> addLabel(@RequestBody CategoryCreateResource resource) {
        LOGGER.info("Creating new Category: [" + resource.getName() + "]");
        categoryService.addLabel(resource);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(DELETE)
    public ResponseEntity<Void> removeLabel(@RequestParam Long id) {
        LOGGER.info("Deleting Category with id: [" + id + "]");
        categoryService.removeLabel(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
