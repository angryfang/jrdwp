# 说明
- 代码拷贝自[jrdwp](https://github.com/leonlee/jrdwp)
- 为了减轻包大小只保留了client部分代码，server部分由spring boot websocket实现

# jrdwp
**J**ava **R**emote **D**ebugging through **W**ebsocket **P**roxy is a proxy for Java remote debugging. It likes Microsoft's [azure-websites-java-remote-debugging] (https://github.com/Azure/azure-websites-java-remote-debugging), but includes all of client and server side implementation(**azure-websites-java-remote-debugging repo** only published client side, the serverside is not opensource now).

# Prerequisites:
* Enable websocket endpoint in web
* JDWP compatible debugger like Eclipse/Netbeans

# Compiling & Building
```bash
go mod download
go build jrdwp.go 
```

# Usage
## Start Java application with JDWP
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:5005 -jar foo.jar
```

## Copy public key from remote host
jrdwp server will generate .jrdwp_key under the working directory, please copy it's content and save as .jrdwp_key to jrdwp client working directory.

## [Start jrdwp client on local box] (start-client)
```bash
./jrdwp -bind-port=8876 -server-host=localhost -server-port=8080 -ws-origin=http://localhost/ -schema=ws -ws-path=/debug/localhost_8081 -access-token=123456
```
## Open IDEA/Eclipse to connect to jrdwp client on localhost:8876
```bash
 _________________
< Enjoy yourself! >
 -----------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||
```

# Options
## Flags of jrdwp client
```bash
    -bind-host string
        bind host, default ''
    -bind-port int
        bind port, default 9876 (default 9876)
    -server-host string
        remote server host
    -server-port int
        remote server port, default 9877 (default 9877)
    -ws-origin string
        websocket request origin header
    -ws-path string
        websocket server path (default "jrdwp")
    -ws-schema string
        websocket schema, ws|wss (default "ws")
    -access-token string
        websocket server can simpley verify field access-token in head
```

# Security
* simply verfiy field access-token in head in spring boot filter