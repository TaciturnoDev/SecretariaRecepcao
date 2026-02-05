/*package com.math.taskmanager.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFound(
			ResourceNotFoundException ex,
			HttpServletRequest request) {
		
		ApiError error = new ApiError(
				LocalDateTime.now(),
				404,
				"Not Found",
				ex.getMessage(),
				request.getRequestURI()
				
	);
  
	return ResponseEntity.status(404).body(error);
	
	}
}
*/

package com.math.taskmanager.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiValidationError> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getDefaultMessage())
                                .collect(Collectors.toList());

        ApiValidationError response = new ApiValidationError(HttpStatus.BAD_REQUEST.value(), errors);

        return ResponseEntity.badRequest().body(response);
    }
}
