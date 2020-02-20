# How to create secrets in several repositories

## Prerequisites

* NodeJS
* `npm install tweetsodium`

## Script to encrypt the secret value

The secret value must be encrypted with LibSodium using the public key retrieved from the Get your public key endpoint:

* `encrypt-secret.js`
```
const sodium = require('tweetsodium');

const key = process.argv[2];
const secretValue = process.argv[3];

const messageBytes = Buffer.from(secretValue);
const keyBytes = Buffer.from(key, 'base64');
const encryptedBytes = sodium.seal(messageBytes, keyBytes);
const encrypted = Buffer.from(encryptedBytes).toString('base64');

console.log(encrypted);
```

## Script to create all the secrets

It's necessary to fill the user value, the token value and the secrets values:

* `create-secrets.sh`
```
#!/bin/bash

OWNER=arrow-kt
REPOSITORIES=(arrow-core arrow-fx arrow-ank arrow-incubator arrow-docs arrow-ui arrow-integrations arrow-optics arrow-test arrow-site)
SECRETS=(AWS_ACCESS_KEY_ID AWS_CLOUDFRONT_ID AWS_SECRET_ACCESS_KEY BINTRAY_API_KEY BINTRAY_USER S3_BUCKET)

AWS_ACCESS_KEY_ID=
AWS_CLOUDFRONT_ID=
AWS_SECRET_ACCESS_KEY=
BINTRAY_API_KEY=
BINTRAY_USER=
S3_BUCKET=

USER=
TOKEN=

for repository in ${REPOSITORIES[*]}
do
    key_info=$(curl -u $USER:$TOKEN "https://api.github.com/repos/$OWNER/$repository/actions/secrets/public-key")
    key_id=$(echo $key_info | jq '.key_id')
    key=$(echo $key_info | jq '.key')
    
    for secret in ${SECRETS[*]}
    do
        encrypted_value=$(node encrypt-secret.js $key ${!secret})
        curl -H "Content-Type: application/json" -X PUT -d "{\"encrypted_value\":\"$encrypted_value\",\"key_id\":$key_id}" -u $USER:$TOKEN "https://api.github.com/repos/$OWNER/$repository/actions/secrets/$secret"
    done
done
```
