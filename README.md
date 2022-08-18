Follow the steps for initializing the local Vault container.

    docker run --rm --cap-add=IPC_LOCK -e VAULT_ADDR=http://localhost:8200 -p 8200:8200 --name=dev-vault vault:1.6.0 (Terminal A)
    docker exec -it dev-vault sh (in new terminal B)
    export VAULT_TOKEN=$Root Token FROM terminal A (in terminal B)
    vault kv put secret/myapps/vault-quickstart/config a-private-key=123456 (in terminal B)

     cat <<EOF | vault policy write vault-quickstart-policy -
     path "secret/data/myapps/vault-quickstart/*" {
        capabilities = ["read"]
    }
    EOF

     vault auth enable userpass
     vault write auth/userpass/users/bob password=sinclair policies=vault-quickstart-policy

    Login to http://localhost:8200 with $Root Token FROM terminal A and add secrets in myapps/vault-quickstart/ (use do it in terminal B)
