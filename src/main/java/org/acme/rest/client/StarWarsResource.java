package org.acme.rest.client;

import io.smallrye.common.annotation.Blocking;
import org.acme.graphql.client.StarWarsClientApi;
import org.acme.graphql.client.model.Film;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
public class StarWarsResource {
    private final StarWarsClientApi typesafeClient;

    public StarWarsResource(StarWarsClientApi typesafeClient) {
        this.typesafeClient = typesafeClient;
    }

    @GET
    @Path("/starwars")
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public List<Film> getAllFilmsUsingTypesafeClient() {
        return typesafeClient.allFilms().films();
    }
}