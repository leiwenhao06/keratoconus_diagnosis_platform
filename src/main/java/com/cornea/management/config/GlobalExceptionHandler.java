package com.cornea.management.config;

import com.cornea.management.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.ServletException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBadRequest(IllegalArgumentException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "请求参数错误";
        log.warn("Bad request: {}", msg);
        return ApiResponse.error(400, msg);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalState(IllegalStateException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "操作状态异常";
        log.warn("Illegal state: {}", msg);
        return ApiResponse.error(400, msg);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNotFound(NoSuchElementException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "请求的资源不存在";
        log.warn("Resource not found: {}", msg);
        return ApiResponse.error(404, msg);
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MissingServletRequestParameterException.class,
        MethodArgumentTypeMismatchException.class,
        HttpRequestMethodNotSupportedException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleSpringValidation(Exception e) {
        log.warn("Spring validation error: {}", e.getMessage());
        return ApiResponse.error(400, "请求格式错误，请检查请求参数");
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMultipartError(MultipartException e) {
        log.warn("Multipart parse error: {}", e.getMessage());
        return ApiResponse.error(400, "文件上传请求解析失败，请检查文件格式或重试");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleFileTooLarge(MaxUploadSizeExceededException e) {
        log.warn("File upload exceeds size limit: {}", e.getMessage());
        return ApiResponse.error(400, "上传文件过大，请压缩后重试");
    }

    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleServletError(ServletException e) {
        log.warn("Servlet error (likely multipart parse failure): {}", e.getMessage());
        return ApiResponse.error(400, "文件上传请求异常，请确认文件格式正确后重试");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleDataIntegrity(DataIntegrityViolationException e) {
        log.error("Data integrity violation: {}", e.getMessage(), e);
        return ApiResponse.error(409, "数据冲突，可能因重复数据或违反约束导致，请检查后重试");
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleIOError(IOException e) {
        log.error("File I/O error: {}", e.getMessage(), e);
        return ApiResponse.error(500, "文件读写错误，请稍后重试");
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleDatabaseError(SQLException e) {
        log.error("Database error: {}", e.getMessage(), e);
        return ApiResponse.error(500, "数据库操作失败，请稍后重试");
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleNullPointer(NullPointerException e) {
        log.error("Unexpected null pointer: {}", e.getMessage(), e);
        return ApiResponse.error(500, "服务器内部数据处理错误，请联系管理员");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneral(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ApiResponse.error(500, "服务器内部错误，请稍后重试");
    }
}
