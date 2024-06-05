# abscondo
A CRUD application that allows you to encrypt and decrypt confidential information

## Prerequisites

Ensure you have Java Runtime Environment (JRE) 10 installed on your system. You can download and install it from the following link:

- [Java SE Runtime Environment 10 Downloads](https://www.oracle.com/de/java/technologies/java-archive-javase10-downloads.html)

## Setup Instructions
1. Install Java (JRE) 10
2. Clone the repo
3. Compile the Java files: javac -d bin src/*.java
4. java -cp bin Main

## Usage Instructions
Once the project is set up and running, the system will:

- Generate a shared key using the Diffie-Hellman algorithm.
- Use the shared secret key generated by Diffie-Hellman for Triple DES encryption.
- Encrypt and decrypt a sample message using the generated keys.

Find a full easy to understand blog post on the implementation at https://adkspence.github.io/projects/abscondo/
