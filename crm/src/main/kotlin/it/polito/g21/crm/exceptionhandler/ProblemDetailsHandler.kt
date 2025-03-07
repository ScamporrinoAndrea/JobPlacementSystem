package it.polito.g21.crm.exceptionhandler



import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProblemDetailsHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ContactNotFoundException::class)
    fun handleContactNotFound(e: ContactNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(EmailNotFoundException::class)
    fun handleEmailNotFound(e : EmailNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(AddressNotFoundException::class)
    fun handleAddressNotFound(e : AddressNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(TelephoneNotFoundException::class)
    fun handleTelephoneNotFound(e : TelephoneNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(EmailAlreadyPresentException::class)
    fun handleEmailAlreadyPresent(e : EmailAlreadyPresentException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_MODIFIED, e.message!!)

    @ExceptionHandler(AddressAlreadyPresentException::class)
    fun handleAddressAlreadyPresent(e : AddressAlreadyPresentException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_MODIFIED, e.message!!)

    @ExceptionHandler(TelephoneAlreadyPresentException::class)
    fun handleTelephoneAlreadyPresent(e : TelephoneAlreadyPresentException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_MODIFIED, e.message!!)

    @ExceptionHandler(MessageNotFoundException::class)
    fun handleMessageNotFound(e : MessageNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(InvalidStateException::class)
    fun handleInvalidState(e : InvalidStateException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(InvalidPriorityException::class)
    fun handleInvalidPriority(e : InvalidPriorityException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(MismatchChannelException::class)
    fun handleMismatchChannel(e : MismatchChannelException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(InvalidEmailException::class)
    fun handleInvalidEmail(e : InvalidEmailException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(InvalidAddressException::class)
    fun handleInvalidAddress(e : InvalidAddressException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(InvalidTelephoneException::class)
    fun handleInvalidTelephone(e : InvalidTelephoneException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(InvalidCategoryException::class)
    fun handleInvalidCategory(e : InvalidCategoryException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(InvalidEmpStateException::class)
    fun handleInvalidEmpState(e : InvalidEmpStateException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(JobOfferNotFoundException::class)
    fun handleJobOfferNotFound(e : JobOfferNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(InvalidFilterParamsException::class)
    fun handleInvalidFilterParams(e : InvalidFilterParamsException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(InvalidJobOfferFlowException::class)
    fun handleInvalidJobOfferFlow(e : InvalidJobOfferFlowException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(InvalidSubjectException::class)
    fun handleInvalidJobOfferFlow(e : InvalidSubjectException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

}