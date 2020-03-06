### Potential issues:

- Starting Oracle KVLite server:

    1. Check that the path to the jre/bin folder is set in order the `keytool.exe` file is reachable from command line ([source](https://stackoverflow.com/questions/13064336/cannot-run-program-keytool-exe-createprocess-error-2))
       ```
       Cannot run program “…\keytool.exe”: CreateProcess error=2
       ```   