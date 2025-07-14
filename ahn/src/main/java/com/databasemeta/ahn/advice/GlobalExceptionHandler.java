package com.databasemeta.ahn.advice;

import java.io.IOException;
import java.sql.SQLException;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.thymeleaf.exceptions.TemplateInputException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLException.class)
    public ModelAndView handleSQLException(SQLException ex) {
        ModelAndView mav = new ModelAndView("error/common"); // templates/error/common.html
        mav.addObject("exception", ex.getMessage());
        mav.addObject("errorMessage", "데이타 베이스 및 접속 에러로 보이며, 관련 입력 정보를 확인하시기 바랍니다");
        return mav;
    }
    
    
    @ExceptionHandler(IOException.class)
    public ModelAndView handleOException(IOException ex) {
        ex.printStackTrace();
        ModelAndView mav = new ModelAndView("error/common"); // templates/error/common.html
        mav.addObject("exception", ex.getMessage());
        mav.addObject("errorMessage", "파일관련 작업이 실패했습니다. 작업 권한등을 확인하시기 바랍니다.");
        return mav;
    }

     // 404 Not Found: 매핑되지 않은 URL 접근 시 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFound(NoHandlerFoundException ex) {
        ex.printStackTrace();
        ModelAndView mav = new ModelAndView("error/common"); 
        mav.addObject("exception", ex.getMessage());
        mav.addObject("errorMessage", "요청하신 페이지를 찾을 수 없습니다");
        return mav;
    }

    @ExceptionHandler(TemplateInputException.class)
    public ModelAndView handleTemplateInputException(TemplateInputException ex) {
        // 에러 메시지 등 필요한 정보를 모델에 담아 전달
        ModelAndView mav = new ModelAndView("error/common"); 
        mav.addObject("exception", ex.getMessage());
        mav.addObject("errorMessage", "요입력정보가 누락되었습니다");
        return mav;
   
    }

    // @ExceptionHandler(Exception.class)
    // public ModelAndView handleAllException3(Exception ex) {
    //     ModelAndView mav = new ModelAndView("error/common"); // templates/error/common.html
    //     mav.addObject("exception", ex.getMessage());
    //     mav.addObject("errorMessage", "에러가 발생했습니다");
    //     return mav;
    // }
}
