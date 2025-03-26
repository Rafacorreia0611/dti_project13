Every thing asked in the project description is implemented.

## Compile
In the root directory of the project, run the following command on macos `./gradlew installDist` and on windows `gradlew installDist`. The required jar
files and default configuration files will be available in the `build/install/library` directory.

## Run
To run the replicas (4 replicas to tolerate 1 faulty replica), run the following command in the `build/install/library` directory:
```
./smartrun.sh bftsmart.demo.counter.CounterServer 0
./smartrun.sh bftsmart.demo.counter.CounterServer 1
./smartrun.sh bftsmart.demo.counter.CounterServer 2
./smartrun.sh bftsmart.demo.counter.CounterServer 3
```
Once the replicas are running, and the message `Ready to process operations` is displayed on all replicas, run the following command in the `build/install/library` directory to start the client:
```
./smartrun.sh intol.bftmap.BFTMapInteractiveClient <client_id>
```
The client_id is an integer value that is used to identify the client. The client can be run multiple times with different client_id values.