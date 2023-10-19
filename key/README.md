## Listing Fingerprint 
- Debug
    ```
         keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android
    ```

## Import cert for production Using JAVA Open JDK 
- Use OpenJDK (existing use openJDK 11)
- Run this command
- Prefer to use manual generate cause need to match app fingerprint with third party dependencies
```
    java -jar pepk.jar --keystore=florist.jks --alias=florist --output=florist.zip --include-cert --rsa-aes-encryption --encryption-key-path=encryption_public_key.pem
```