package org.bubblecloud.webvr

import org.glassfish.grizzly.websockets.WebSocket

class Session(val remoteHost: String, val remotePort: Int, val socket: WebSocket) {

}