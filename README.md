# hash_app

This app is a simple hash bucket system that writes the text pieces entered by user into files.
Every string the user types as an input gets hashed and appended to one of the bucket files. (`1.txt` -> `n.txt`)
When running the program, the user enters the number of buckets and the bucket size. When the bucket is full, the strings are appended to the overflow files of the same bucket.

## Requirements

JDK 11 or newer.<br>
The library used for hashing is installed inside the `lib/` directory so, there is no need for anything else.

## How to compile and run

The requirements said that `hash_app` must be the name of the app and the user must be starting the app with a command `hash_app 5 20`.<br>
I have created a script that compiles and runs it by this command without any specific compile-run steps required.

```
./hash_app n s
```

(If `./hash_app` says "Permission denied", run `chmod +x hash_app` once.)

Here:
- `n` is the number of buckets
- `s` is the bucket size which implies the maximum number of characters in one file.

Example run:

```
$ ./hash_app 5 20
Please enter the string: Hello
Hello added to 5.txt
Please enter the string: World
World added to 4.txt
Please enter the string: Test
Test added to 1.txt
Please enter the string:
```

## Hashing algorithm

The hashing algorithm used is **MurmurHash3** (32-bit x86 version, seed 0). It is from the **Apache Commons Codec** library.<br>
It has a deprecated method but I have used the proper one: `org.apache.commons.codec.digest.MurmurHash3.hash32x86`.

The bucket number is found by finding the remainder after dividing the hash of the string entered by the n (number of buckets).<br>
Since the requirements say we have to use bucket numbers from 1 to n, we write to the n.txt bucket when the remainder is zero.

## Overflow buckets: how files are linked

Each bucket is a chain of files:

```
3.txt  ->  3_1.txt  ->  3_2.txt  ->  3_3.txt  ...
main       overflow 1   overflow 2   overflow 3
```

- The file after `3.txt` is always `3_1.txt`, and the file after `3_k.txt` is always `3_(k+1).txt`.
- The bucket files have no extra data inside other than the input strings (no space, no separator).


**Which file to write to:** when the program uses the bucket for the first time in the run,<br>
it follows the chain for the bucket (`3.txt`, `3_1.txt`, `3_2.txt`, ...) until finding the last one.<br>
The app uses HashMaps to keep the data about buckets and the number of characters inside. So there is no need for searching every time.<br>
The app also works when the app is restarted on the existing files.


**Bucket size rule:** a file never holds more than `s` characters. If the input does not fit the remaining part of the bucket,<br>
we write until the character the file can contain and the rest of string goes to the next overflow file.