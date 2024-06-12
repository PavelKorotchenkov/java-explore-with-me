package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.model.ApiError;
import ru.practicum.util.LocalDateTimeStringParser;

import javax.persistence.EntityNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({NumberFormatException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleException(final Exception e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "Incorrectly made request";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleException(final EntityNotFoundException e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "Entity not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(ParticipationDuplicateRequestException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(final ParticipationDuplicateRequestException e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "Request has already been sent";
        HttpStatus status = HttpStatus.CONFLICT;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(ParticipationLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(final ParticipationLimitExceededException e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "Participant limit exceeded";
        HttpStatus status = HttpStatus.CONFLICT;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(ParticipationRequestByInitiatorException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(final ParticipationRequestByInitiatorException e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "Request for event participation by it's initiator is forbidden";
        HttpStatus status = HttpStatus.CONFLICT;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(PaticipationNotPublishedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(final PaticipationNotPublishedException e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "Request for unpublished event is forbidden";
        HttpStatus status = HttpStatus.CONFLICT;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(ParticipationRequestUpdateNotPendingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(final ParticipationRequestUpdateNotPendingException e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "Only pending participation requests updateByAdmin allowed";
        HttpStatus status = HttpStatus.CONFLICT;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(CategoryInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(final CategoryInUseException e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "Cannot deleteByAuthor the category because there are events related to the category";
        HttpStatus status = HttpStatus.CONFLICT;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(EventUpdateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(final EventUpdateException e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "Incorrect arguments passed";
        HttpStatus status = HttpStatus.CONFLICT;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(EventDateViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(final EventDateViolationException e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "Event date must be at least in two hours after creating";
        HttpStatus status = HttpStatus.CONFLICT;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(CommentUpdateNotByAuthorException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(final CommentUpdateNotByAuthorException e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "Only author can change his comment";
        HttpStatus status = HttpStatus.CONFLICT;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(CommentUpdateTimeLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(final CommentUpdateTimeLimitExceededException e) {
        log.warn("Exception: {}", e.getMessage());
        String reason = "It's not allowed to change a comment after 1 hour is passed";
        HttpStatus status = HttpStatus.CONFLICT;
        return createApiError(e, reason, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(DataIntegrityViolationException e) {
        String reason = "Data integrity violation.";
        Throwable rootCause = e.getCause();

        if (rootCause instanceof ConstraintViolationException) {
            ConstraintViolationException constraintEx = (ConstraintViolationException) rootCause;
            String constraintName = constraintEx.getConstraintName();

            switch (constraintName) {
                case "categories_category_name_key":
                    reason = "Category name already exists.";
                    break;
                case "users_email_key":
                    reason = "User email already exists.";
                    break;
                default:
                    reason = "Unique constraint violation: " + constraintName;
            }
        }
        HttpStatus status = HttpStatus.CONFLICT;
        return createApiError(e, reason, status);
    }

    private ApiError createApiError(Exception e, String reason, HttpStatus status) {
        ApiError error = new ApiError();

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        error.setErrors(sw.toString());
        error.setMessage(e.getMessage());
        error.setReason(reason);
        error.setStatus(status.toString());
        error.setTimestamp(LocalDateTimeStringParser.parseLocalDateTimeToString(LocalDateTime.now()));

        return error;
    }
}
