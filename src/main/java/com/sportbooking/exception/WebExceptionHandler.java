package com.sportbooking.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class WebExceptionHandler {
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  String business(BusinessException e, Model m) {
    m.addAttribute("message", e.getMessage());
    return "error/business";
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  String integrity(Model m) {
    m.addAttribute(
        "message",
        "Dữ liệu bị trùng hoặc đang được tham chiếu. Không thể xóa lịch sử. Hãy khóa bản ghi nếu"
            + " cần.");
    return "error/business";
  }

  @ExceptionHandler({
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
    org.springframework.web.bind.MissingServletRequestParameterException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  String input(Model m) {
    m.addAttribute("message", "Dữ liệu nhập không đúng định dạng. Vui lòng kiểm tra lại.");
    return "error/business";
  }
}
