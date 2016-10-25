package org.bubblecloud.webvr.model

import org.glassfish.grizzly.websockets.WebSocket

class Session(val remoteHost: String, val remotePort: Int, val socket: WebSocket) {

}