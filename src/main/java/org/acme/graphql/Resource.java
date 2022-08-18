package org.acme.graphql;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class Resource {

    @Query
    public Something getSomething() {
        return new Something("something");
    }

    public record Something(String value) {

    }
}
