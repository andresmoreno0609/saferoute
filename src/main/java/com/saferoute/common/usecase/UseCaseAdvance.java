package com.saferoute.common.usecase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Template for application use cases with a structured execution flow and centralized exception handling.
 * <p>
 * This abstract class defines a standard lifecycle for use cases:
 * <ol>
 *   <li><b>preConditions</b> – validate the request and load any required context (e.g. authenticated user).</li>
 *   <li><b>core</b> – perform the main business logic and return the result.</li>
 *   <li><b>postConditions</b> – optional side effects after success (e.g. audit, notifications).</li>
 * </ol>
 * If any step throws an exception, the flow is aborted and {@link #handleException(Exception)} maps it
 * to the appropriate HTTP-oriented exception before propagation to the controller layer.
 * <p>
 * <b>Motivation</b>: this template was introduced to standardize how use cases are implemented across the
 * application. By enforcing a common structure (input → validation → core logic → side effects) and a 
 * single place for logging and exception mapping, it:
 * <ul>
 *   <li>Improves readability and onboarding: every use case follows the same execution pattern.</li>
 *   <li>Promotes good practices: clear separation of concerns between validation, business logic and I/O.</li>
 *   <li>Centralizes cross-cutting concerns: logging and HTTP error mapping are implemented once
 *       and reused everywhere.</li>
 *   <li>Makes testing easier: the core logic is isolated in {@code core(...)} and can be unit tested
 *       without dealing with controllers or HTTP details.</li>
 * </ul>
 * <p>
 * <b>Exception handling</b>: Exceptions are logged, failure notifications are triggered, and then
 * rethrown or wrapped so that existing application handlers are used:
 *
 * @param <RQ> the request type (input to the use case).
 * @param <RS> the response type (output of the use case).
 */
@Slf4j
public abstract class UseCaseAdvance<RQ, RS> {

    /**
     * Runs the use case: preConditions → core → postConditions.
     * Any exception is passed to {@link #handleException(Exception)}, which logs, notifies, and then
     * rethrows or wraps it for the REST layer.
     *
     * @param request the use case input.
     * @return the use case result from {@link #core(Object)}.
     * @throws ResponseStatusException for HTTP errors (400, 404, 422, 500) as per handleException.
     */
    public final RS execute(RQ request) {
        try {
            preConditions(request);
            RS response = core(request);
            postConditions(response);
            return response;
        } catch (Exception ex) {
            handleException(ex);
            return null;
        }
    }

    /**
     * Validates the request and prepares context before running the core logic.
     * Override to add validation, load the authenticated user, or resolve dependencies.
     * Default implementation does nothing.
     *
     * @param request the use case request.
     */
    protected void preConditions(RQ request) {}

    /**
     * Implements the main business logic of the use case. Must be implemented by subclasses.
     *
     * @param request the use case request.
     * @return the use case result.
     */
    protected abstract RS core(RQ request);

    /**
     * Optional hook run after success. Override for audit, notifications, or other side effects.
     *
     * @param response the use case result.
     */
    protected void postConditions(RS response) {}

    /**
     * Centralized exception handling: logs the error, sends failure notification, then rethrows or wraps
     * the exception so that existing {@code @ControllerAdvice} and Spring handling produce the correct HTTP response.
     * Override only if you need a different mapping; otherwise use the built-in rules.
     *
     * @param ex the exception thrown during execute.
     * @throws ResponseStatusException for 400/500 or when rethrowing existing ResponseStatusException.
     */
    protected void handleException(Exception ex) {
        log.error("UseCase error: {} in [{}]",
                  ex.getMessage(),
                  this.getClass().getSimpleName(), ex);

        sendFailureNotification(ex);

        // Map or rethrow so @ControllerAdvice / Spring produce the correct HTTP response
        if (ex instanceof ResponseStatusException rse) {
            throw rse;
        }
        if (ex instanceof IllegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
        if (ex instanceof RuntimeException) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
        }
        // Checked exceptions: expose as 500
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
    }

    /**
     * Placeholder for failure notifications (e.g. email, metrics). Default implementation only logs.
     */
    private void sendFailureNotification(Exception ex) {
        log.warn("Failure notification for [{}]: {}",
                 this.getClass().getSimpleName(), ex.getMessage());
    }
}
