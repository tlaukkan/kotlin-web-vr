package vr.model

import org.glassfish.grizzly.websockets.WebSocket
import vr.Cell

data class Session(val remoteHost: String, val remotePort: Int, val socket: WebSocket, var cell: Cell? = null)