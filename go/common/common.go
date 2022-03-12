package common

import (
	"net"
	"time"
)

var DeadlineDuration = time.Second * 60 * 30

//InitTCPConn initialize tcp connection to keep alive
func InitTCPConn(conn *net.TCPConn) {
	conn.SetKeepAlive(true)
	conn.SetNoDelay(true)
	conn.SetLinger(3)
	conn.SetDeadline(time.Now().Add(DeadlineDuration))
}
