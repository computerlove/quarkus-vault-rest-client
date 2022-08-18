package org.acme.graphql.client;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import org.acme.graphql.client.model.FilmConnection;

@GraphQLClientApi(configKey = "star-wars-typesafe")
public interface StarWarsClientApi {

    FilmConnection allFilms();

}