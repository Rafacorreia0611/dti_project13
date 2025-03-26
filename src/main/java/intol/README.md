Everything asked in the project description was implemented.

## Compile
In the root directory of the project, run the following command on macOs `./gradlew installDist` and on windows `gradlew installDist`. The required jar
files and default configuration files will be available in the `build/install/library` directory. The source code that we developed is in the `src/main/java/intol/bftmap` directory.

## Run
To run the replicas (4 replicas to tolerate 1 faulty replica), run the following command in the `build/install/library` directory:
```
./smartrun.sh intol.bftmap.BFTMapServer 0
./smartrun.sh intol.bftmap.BFTMapServer 1
./smartrun.sh intol.bftmap.BFTMapServer 2
./smartrun.sh intol.bftmap.BFTMapServer 3
```
Once the replicas are running, and the message `Ready to process operations` is displayed on all replicas, run the following command in the `build/install/library` directory to start the client:
```
./smartrun.sh intol.bftmap.BFTMapInteractiveClient <client_id>
```
The client_id is an integer value that is used to identify the client (use id 4 onwards to run the clients). The client can be run multiple times with different client_id values (Only client with id 4 can mint coins).