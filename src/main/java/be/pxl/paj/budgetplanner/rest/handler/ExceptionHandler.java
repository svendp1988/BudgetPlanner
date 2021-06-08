package be.pxl.paj.budgetplanner.rest.handler;

import be.pxl.paj.budgetplanner.exception.*;
import be.pxl.paj.budgetplanner.rest.handler.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

	@org.springframework.web.bind.annotation.ExceptionHandler(value = {
			InvalidFileExtensionException.class,
			DuplicateAccountException.class,
			DuplicateCategoryException.class,
			AccountNotFoundException.class,
			CategoryInUseException.class,
			InvalidPaymentException.class,
			PaymentNotFoundException.class,
			UnknownCategoryException.class
	})
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorMessage resourceNotFoundException(RuntimeException ex) {
		return new ErrorMessage(ex.getMessage());
	}
}

