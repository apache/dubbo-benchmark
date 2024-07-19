namespace java service.demo
struct HelloMessage {
    1: required string message,
}

struct HelloResponse {
    1: required string message,
}

service HelloService {

    HelloResponse sayHello(1: HelloMessage request);
}