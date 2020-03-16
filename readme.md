### Environnemnt 

- Oracle NoSQL : kvstore v18.1.27
    - Running in "insecure mode" with `java -jar lib/kvstore.jar kvlite -secure-config disable`
    - Add the `kvstore/lib` folder to the project dependencies
    
- MongoDB : v4.2.3
    - Run mongo db in terminal

- Java : JDK 1.8
- IntelliJ 11.0.9

### Potential issues:

- Starting Oracle KVLite server:

    1. Check that the path to the jre/bin folder is set in order the `keytool.exe` file is reachable from command line ([source](https://stackoverflow.com/questions/13064336/cannot-run-program-keytool-exe-createprocess-error-2))
       ```
       Cannot run program “…\keytool.exe”: CreateProcess error=2
       ```   