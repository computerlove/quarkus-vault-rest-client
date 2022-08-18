# Reproducer for missing config when using Hashicorp Vault

When using the extension some config keys needs to be present in application.properties in order for the config to
be visible when running the application.

For instance with the following in application.properties:
```properties
%dev.quarkus.smallrye-graphql-client.star-wars-typesafe.url=https://development.star.wars
```
The application will crash on startup when deployed with a Vault with
```properties
quarkus.smallrye-graphql-client.star-wars-typesafe.url=https://production.star.wars
```

These problems is leftovers not fixed in https://github.com/quarkusio/quarkus/issues/21336.

Follow the steps for initializing the local Vault container.

```shell
docker run --rm --cap-add=IPC_LOCK -e VAULT_ADDR=http://localhost:8200 -p 8200:8200 --name=dev-vault vault:1.6.0 (Terminal A)
```
```shell
docker exec -it dev-vault sh (in new terminal B)
export VAULT_ADDR='http://0.0.0.0:8200'
export VAULT_TOKEN=$Root Token FROM terminal A (in terminal B)

vault kv put secret/myapps/vault-quickstart/config a-private-key=123456 (in terminal B)
cat <<EOF | vault policy write vault-quickstart-policy -
 path "secret/data/myapps/vault-quickstart/*" {
    capabilities = ["read"]
}
EOF

vault auth enable userpass
vault write auth/userpass/users/bob password=sinclair policies=vault-quickstart-policy
```    
    
Login to http://localhost:8200 with $Root Token FROM terminal A and add secrets in myapps/vault-quickstart/ 
```json
{
  "a-private-key": "123456",
  "newconfig": "newconfig",
  "quarkus.micrometer.export.influx.api-version": "V2",
  "quarkus.micrometer.export.influx.org": "org",
  "quarkus.micrometer.export.influx.token": "atoken",
  "quarkus.micrometer.export.influx.bucket": "abucket",
  "quarkus.micrometer.export.influx.uri": "https://localhost/metrics",
  "quarkus.rest-client.country-api.url": "https://restcountries.com/",
  "quarkus.smallrye-graphql-client.star-wars-typesafe.url": "https://swapi-graphql.netlify.app/.netlify/functions/index",
  "quarkus.log.category.\"org.acme.rest.client.CountriesResource\".level": "DEBUG",
  "eventing.config": "the config"
}
```

Start the app with `mvn quarkus:dev`.


# Tests and expectations
On startup the following should be printed. (And print errors)
```text
Using InfluxDB API version V2 to write metrics
```

```shell
curl http://localhost:8080/country/name/norway
```
should print json.

```shell
curl http://localhost:8080/starward
```
should print json, not fail with
```text
2022-08-18 10:53:18,778 ERROR [org.jbo.res.rea.com.cor.AbstractResteasyReactiveContext] (executor-thread-0) Request failed: java.lang.RuntimeException: URL not configured for client. Please define the property quarkus.smallrye-graphql-client.star-wars-typesafe.url or pass it to your client builder dynamically
        at io.quarkus.smallrye.graphql.client.runtime.QuarkifiedErrorMessageProvider.urlMissingErrorForNamedClient(QuarkifiedErrorMessageProvider.java:14)
        at io.smallrye.graphql.client.vertx.typesafe.VertxTypesafeGraphQLClientBuilder.build(VertxTypesafeGraphQLClientBuilder.java:124)
        at io.quarkus.smallrye.graphql.client.runtime.SmallRyeGraphQLClientRecorder.lambda$typesafeClientSupplier$0(SmallRyeGraphQLClientRecorder.java:20)
        at org.acme.graphql.client.StarWarsClientApi_eafe9a3b2d3e2a2588a03c49fa86dfd0a5185761_Synthetic_Bean.create(Unknown Source)
```

```shell
curl 'http://localhost:8080/graphql' \
  -H 'Accept: application/json' \
  --data-raw '{"query":"query {\n  something {\n    value\n  }\n}","variables":null}'
```
should print json and print in console
```text
2022-08-18 12:44:01,405 INFO  [org.acm.gra.Service] (vert.x-eventloop-thread-0) value the config
```

## Workaround
Adding the following dummy config to application.properties 
```properties
quarkus.micrometer.export.influx.api-version=
quarkus.micrometer.export.influx.org=
quarkus.micrometer.export.influx.bucket=
quarkus.micrometer.export.influx.uri=
quarkus.smallrye-graphql-client.star-wars-typesafe.url=
quarkus.log.category."org.acme.rest.client.CountriesResource".level=INFO
```