package org.acme.rest.client;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;

@Path("/country")
public class CountriesResource {

    @Inject
    @RestClient
    CountriesService countriesService;

    @ConfigProperty(name = "newconfig")
    String secret2;

   // @Inject
   // MeterRegistry meterRegistry;

    @Inject
    Logger logger;

    @GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Country> name(@PathParam("name") String name) {
        logger.debugf("countriesService.getByName(%s);", name);
        //meterRegistry.counter("count").increment();
        return countriesService.getByName(name);
    }

    @GET
    @Path("/name-async/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Set<Country>> nameAsync(@PathParam("name") String name) {
        return countriesService.getByNameAsync(name);
    }

    @GET
    @Path("/name-uni/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Set<Country>> nameMutiny(@PathParam("name") String name) {
        return countriesService.getByNameAsUni(name);
    }
}
