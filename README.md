# GoutTogther Backend

## Related command

### Build jar and get OpenTelemetry Agent
```shell

./gradlew clean build
```
### Generate RSA Keypair
```shell
openssl genrsa -out private_key.pem 4096 ### generate public key 
openssl rsa -pubout -in private_key.pem -out public_key.pem ### generate private key
openssl pkcs8 -topk8 -in private_key.pem -inform pem -out private_key_pkcs8.pem -outform pem -nocrypt

```