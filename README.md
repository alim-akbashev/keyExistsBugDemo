## Demo of the keyExists() bug

### Steps to reproduce

- Run FillDbApp to put the key "123" + 5M of other records - it seems that relatively big amount of data is required to reproduce
- Run TryTheKeyApp to check if the key "123" exists by calling `keyExists()`. It has to display "Key found" if no bug, but it displays "Key not found" in my environment (M1, Sonoma 14.6.1, java 17-21, the latest rocksdb 9.4.0 + the same behaviour is in my production with similiar db settings). 