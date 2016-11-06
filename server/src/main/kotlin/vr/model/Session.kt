package vr.model

import org.glassfish.grizzly.websockets.WebSocket
import vr.Cell
import java.util.*

data class Session(val remoteHost: String,
                   val remotePort: Int,
                   val socket: WebSocket,
                   var clientCellUris:List<String> = ArrayList<String>(),
                   var serverCellUris:List<String> = ArrayList<String>())