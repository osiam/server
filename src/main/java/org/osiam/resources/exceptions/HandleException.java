/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.resources.exceptions;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class HandleException extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = Logger.getLogger(HandleException.class.getName());

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        LOGGER.log(Level.WARNING, "An exception occurred", ex);
        HttpStatus status = setStatus(ex);
        JsonErrorResult error = new JsonErrorResult(status.name(), ex.getMessage());
        return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
    }

    private HttpStatus setStatus(RuntimeException ex) {
        if (ex instanceof ResourceNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (ex instanceof SchemaUnknownException) {
            return HttpStatus.I_AM_A_TEAPOT;
        }
        if (ex instanceof UnsupportedOperationException)
            return HttpStatus.NOT_IMPLEMENTED;

        return HttpStatus.CONFLICT;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    static class JsonErrorResult {
        private String error_code;
        private String description;

        public JsonErrorResult(String name, String message) {
            this.error_code = name;
            this.description = message;
        }

        public String getError_code() {
            return error_code;
        }

        public String getDescription() {
            return description;
        }
    }
}