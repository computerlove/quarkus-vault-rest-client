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
  "quarkus.log.category.\"org.acme.rest.client.CountriesResource\".level": "DEBUG"
}
```

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