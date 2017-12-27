# communic
Simple Java Client-Server socket library. Can be used to support any simple client-server communication. Currently supports only text data and will be enahnced in future to support multiple data formats.

# Classes
This library has 3 classes.
* **Driver** - the main application driver class. This has the main() method and depending on the input arguements the module would function as a server or a client.
* **CommunicClient** - the main class that enables the module to function as the client connecting to the server. Supports text input from the keyboard only.
* **CommunicServer** - the main class that enables to module to function as the server. This class binds the incoming port with the address and listens for incoming client connections. Once a new client connection is established, the data processing is handled through individual threads.

### Usage
* server mode - to run as server mode pass the following arguements **java -jar communic.jar -server -port 'port-number' -address 'ip/host-name'**
* client mode - to run as client mode pass the following arguements **java -jar communic.jar -client -port 'port-number' -address 'server-ip/server-host-name'**

### Future improvements
As of Dec/2017, This library is still work in progress and will take some more time to be fully mature. Future skope of work includes:
* Custom and extensible Client/Server protocol support.
* Data encryption and multiple data format support.
* Client connection manager - to manage the incoming connection requests and auto disconnect after some idle time etc.
* Server configuration - to handle maximum client connections, hand-off to secondary servers.
