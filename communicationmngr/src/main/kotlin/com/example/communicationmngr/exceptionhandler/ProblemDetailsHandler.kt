package com.example.communicationmngr.exceptionhandler



import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProblemDetailsHandler : ResponseEntityExceptionHandler() {


    @ExceptionHandler(InvalidEmailException::class)
    fun handleInvalidEmail(e : InvalidEmailException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)


    @ExceptionHandler(InvalidSubjectException::class)
    fun handleInvalidJobOfferFlow(e : InvalidSubjectException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

}