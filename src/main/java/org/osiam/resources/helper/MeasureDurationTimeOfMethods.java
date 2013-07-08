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

package org.osiam.resources.helper;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.osiam.resources.exceptions.SchemaUnknownException;
import org.osiam.resources.scim.Constants;
import org.osiam.resources.scim.Resource;

import java.util.logging.Logger;

@Aspect
/**
 * This interceptor has the purpose to print out the duration time of all called methods.
 *
 */
public class MeasureDurationTimeOfMethods {
    private static final Logger LOGGER = Logger.getLogger(MeasureDurationTimeOfMethods.class.getName());

    @Around("profile()")
    public void checkUser(ProceedingJoinPoint joinPoint) throws Throwable {
        final long start, end;
        start = System.nanoTime();
        joinPoint.proceed();
        end = System.nanoTime();
        String msg = joinPoint.getSignature().getName() + " took " + (end - start);
        LOGGER.info(msg);

    }

    @Pointcut("execution(* *.*(..))")
    protected void profile() {}


}
