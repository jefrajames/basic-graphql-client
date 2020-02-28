package org.jefrajames.basic.graphql.client;

/**
 * Represents a GraphQL Exception.
 * 
 * See: https://github.com/ejoebstl/graphql-java-client
 */
public class GraphQLException extends Exception {
    /**
     * @param message The error message.
     */
    public GraphQLException(String message) {
        super(message);
    }
}
