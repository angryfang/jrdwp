package client

import (
	"errors"
	"fmt"
	"github.com/gorilla/websocket"
	"log"
	"net/http"
	"net/url"
)

var (
	//ErrConnFailed connection failure
	ErrConnFailed = errors.New("can't connect to ws server")
)

//WSClient websocket client
type WSClient struct {
	host     string
	port     int
	path     string
	origin   string
	scheme   string
	token    string
}

//NewWSClient create WsClient
func NewWSClient(host string, port int, path string, origin string, schema string, token string) *WSClient {
	return &WSClient{
		host:     host,
		port:     port,
		path:     path,
		origin:   origin,
		scheme:   schema,
		token:    token,
	}
}

//Connect connect to remote websocket server
func (client *WSClient) Connect() (*websocket.Conn, error) {
	url := url.URL{
		Scheme: client.scheme,
		Host:   fmt.Sprintf("%s:%d", client.host, client.port),
		Path:   client.path,
	}

	log.Printf("connecting ws server: %s\n", url.String())

	header := client.assembleHeader()

	conn, _, err := websocket.DefaultDialer.Dial(url.String(), header)
	if err != nil {
		log.Println(err)
		return nil, err
	}

	return conn, err
}

func (client *WSClient) assembleHeader() http.Header {
	header := http.Header{}
	header.Add("access-token", client.token)

	return header
}
