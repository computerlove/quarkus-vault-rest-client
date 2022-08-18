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
  "quarkus.micrometer.export.influx.bucket": "abucket",
  "quarkus.micrometer.export.influx.uri": "https://localhost/metrics",
  "quarkus.rest-client.country-api.url": "https://restcountries.com/"
}
```

# Tests and expectations
On startup the following should be printed.
```text
Using InfluxDB API version V2 to write metrics
```

```shell
curl http://localhost:8080/country/name/norway
```
should print json.

