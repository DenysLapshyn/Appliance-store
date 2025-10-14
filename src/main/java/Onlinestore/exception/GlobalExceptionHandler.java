package Onlinestore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle specific exception — e.g. Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFound(ResourceNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", ex.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgument(IllegalArgumentException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "Invalid input: " + ex.getMessage());
        mav.setStatus(HttpStatus.BAD_REQUEST);
        return mav;
    }

    // Handle AccessDeniedException (security)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ModelAndView handleAccessDenied(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "You do not have permission to access this page.");
        mav.setStatus(HttpStatus.FORBIDDEN);
        return mav;
    }

    // Catch-all fallback (for unexpected errors)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalException(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "Unexpected error: " + ex.getMessage());
        ex.printStackTrace(new PrintWriter(System.out)); // prints to System.out
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}
