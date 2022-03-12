package main

import (
	"jrdwp/client"
	"encoding/json"
	"flag"
	"log"
)

const (
	//ModeClient client mode
	ModeClient = "client"
	//PortListen default port of tcp server on client or ws server on remote server
	PortListen = 9876
	//PortServer default port of ws server on client
	PortServer = 9877
	//WsPath default websocket server path
	WsPath = "jrdwp"
)

//Config configuration struct
type Config struct {
	Mode             *string        `json:"mode"`
	BindHost         *string        `json:"bindHost"`
	BindPort         *int           `json:"bindPort"`
	ServerHost       *string        `json:"serverHost"`
	ServerPort       *int           `json:"serverPort"`
	Schema           *string        `json:"schema"`
	WsPath           *string        `json:"wsPath"`
	WsOrigin         *string        `json:"wsOrigin"`
	Token            *string        `json:"token"`
}

func (conf *Config) String() string {
	confJSON, err := json.Marshal(*conf)
	if err != nil {
		return err.Error()
	}
	return string(confJSON)
}

func main() {
	conf := parseFlags()
	start(conf)
}

func parseFlags() *Config {
	log.Printf("initializing with %v ...", flag.Args())

	conf := &Config{}
	conf.Mode = flag.String("mode", ModeClient, "jrdwp mode, \"client\" or \"server\"")
	conf.BindHost = flag.String("bind-host", "", "bind host, default \"\"")
	conf.BindPort = flag.Int("bind-port", PortListen, "bind port, default 9876")
	conf.ServerHost = flag.String("server-host", "", "server host")
	conf.ServerPort = flag.Int("server-port", PortServer, "server port, default 9877")
	conf.WsPath = flag.String("ws-path", WsPath, "websocket server path, default \"/jrdwp\"")
	conf.WsOrigin = flag.String("ws-origin", "", "websocket request origin header, default \"\"")
	conf.Schema = flag.String("ws-schema", "ws", "webscoket schema, ws or wsss")
	conf.Token = flag.String("access-token", "", "access-token in head")
	flag.Parse()

	if *conf.WsPath == "" {
		log.Fatal("invalid ws-path")
	}

	if *conf.WsOrigin == "" {
		log.Fatal("invalid ws-origin")
	}

	log.Printf("initialized by %s \n", conf)

	return conf
}

func start(conf *Config) {
	log.Println("starting jrdwp...")

	startClient(conf)

	log.Printf("jrdwp started in %v mode\n", conf.Mode)
}

func startClient(conf *Config) {
	wsClient := client.NewWSClient(
		*conf.ServerHost,
		*conf.ServerPort,
		*conf.WsPath,
		*conf.WsOrigin,
		*conf.Schema,
		*conf.Token)

	tcpServer := client.NewTCPServer(wsClient, *conf.BindPort)
	if err := tcpServer.Start(); err != nil {
		log.Fatalln("can't start tcp server", err.Error())
	}
}
