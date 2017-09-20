/*
 * Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except
 * in compliance with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package ${package};

import com.amazonaws.services.lambda.runtime.Context;
#if (${handlerType} == 'pojo')
import com.amazonaws.services.lambda.runtime.RequestHandler;
#elseif (${handlerType} == 'stream')
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
#end
#if (${logger} == 'log4j2')

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
#elseif (${logger} == 'lambda')

import com.amazonaws.services.lambda.runtime.LambdaLogger;
#end

#if (${handlerType} == 'pojo')
public class ${className} implements RequestHandler<${className}.Request, ${className}.Response> {
#elseif (${handlerType} == 'simple')
public class ${className} {
#elseif (${handlerType} == 'stream')
public class ${className} implements RequestStreamHandler {
#end
#if (${logger} == 'log4j2')
    static final Logger logger = LogManager.getLogger(${className}.class);

#end
#if (${handlerType} == 'pojo')
    public ${className}.Response handleRequest(${className}.Request request, Context context) {
        String greetingString = String.format("Hello %s %s.", request.firstName, request.lastName);
#elseif (${handlerType} == 'simple')
    public String handler(String name, Context context) {
        String greetingString = String.format("Hello %s.", name);
#elseif (${handlerType} == 'stream')
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        String greetingString = "Hello ";
        int letter;
        while((letter = inputStream.read()) != -1) {
            outputStream.write(Character.toUpperCase(letter));
            greetingString += (char) Character.toUpperCase(letter);
        }
#end
#if (${logger} == 'lambda')
        LambdaLogger logger = context.getLogger();
        logger.log(String.format("Log output: Greeting is '%s'\n", greetingString));
#elseif (${logger} == 'log4j2')
        logger.debug(String.format("Log output: Greeting is '%s'\n", greetingString));
#end
#if (${handlerType} == 'pojo')
        return new ${className}.Response(greetingString);
#elseif (${handlerType} == 'simple')
        return greetingString;
#end
    }
#if (${handlerType} == 'pojo')

    static class Request {
        String firstName;
        String lastName;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Request(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Request() {
        }
    }

    static class Response {
        String greetings;

        public String getGreetings() {
            return greetings;
        }

        public void setGreetings(String greetings) {
            this.greetings = greetings;
        }

        public Response(String greetings) {
            this.greetings = greetings;
        }

        public Response() {
        }
    }
#end
}
