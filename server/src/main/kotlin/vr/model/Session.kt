package vr.model

import org.glassfish.grizzly.websockets.WebSocket

data class Session(val remoteHost: String, val remotePort: Int, val socket: WebSocket)