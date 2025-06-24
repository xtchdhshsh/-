package comlisanshuxue.lisan.Assignment.entity;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理数据校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.error("参数校验失败: " + message);
    }

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException ex) {
        return Result.error(ex.getMessage());
    }

    // 处理数据库异常
    @ExceptionHandler(DataAccessException.class)
    public Result handleDataAccessException(DataAccessException ex) {
        return Result.error("数据库操作失败: " + ex.getRootCause().getMessage());
    }

    // 处理其他所有异常
    @ExceptionHandler(Exception.class)
    public Result handleGeneralException(Exception ex) {
        return Result.error("服务器错误: " + ex.getMessage());
    }
}
