package vr.network

import logger
import vr.network.model.Envelope
import vr.network.model.HandshakeRequest
import vr.network.model.HandshakeResponse
import vr.util.Mapper
import vr.network.WsClient
import vr.network.model.LinkResponse
import java.util.logging.Level

class NetworkClient(val url: String) {
    private val log = logger()
    private val wsClient = WsClient(url)
    private val mapper = Mapper()

    var connected = false
    var linked = false
    var serverCellUri = ""

    var onConnected: ((handshakeResponse: HandshakeResponse) -> Unit)? = null
    var onLinked: ((linkResponse: LinkResponse) -> Unit)? = null
    var onReceive: ((value: Any) -> Unit)? = null
    var onAllReceived: (() -> Unit)? = null
    var onDisconnected: ((reason: String) -> Unit)? = null

    init {
        wsClient.onOpen = { startup() }
        wsClient.onMessage = { message -> onMessage(message) }
        wsClient.onError = { e -> onError(e) }
        wsClient.onClose = { code, reason, remote -> onClose(code, reason, remote) }
    }

    fun connect() {
        wsClient.connect()
    }

    private fun startup() {
        val handshakeRequest = HandshakeRequest("kotlin-web-vr", "vr-state-synchronisation", arrayOf("0.9", "1.0"))

        val envelope = Envelope()
        val values: MutableList<Any> = mutableListOf()
        values.addAll(listOf(handshakeRequest))
        mapper.writeValuesToEnvelope(envelope, values)
        wsClient.send(mapper.writeValue(envelope))
    }

    fun shutdown() {
        wsClient.close()
    }

    fun send(values: List<Any>) {
        val envelope = Envelope()
        mapper.writeValuesToEnvelope(envelope, values)
        wsClient.send(mapper.writeValue(envelope))
    }

    fun onReceivedValues(values: List<Any>) {
        for (value in values) {
            processReceivedValue(value)
        }
        if (onAllReceived != null) {
            onAllReceived
        }
    }

    fun processReceivedValue(value: Any) {
        if (value is HandshakeResponse) {
            if (value.accepted) {
                connected = true
                if (onConnected != null) {
                    onConnected!!.invoke(value)
                }
            } else {
                shutdown()
            }
        } else if (value is LinkResponse) {
            if (value.success && value.serverCellUris.size > 0) {
                linked = true
                serverCellUri = value.serverCellUris[0]
                if (onLinked != null) {
                    onLinked!!.invoke(value)
                }
            } else {
                log.warning("Link failed ${value.errorMessage} with ${value.serverCellUris.size} linked server cells.")
                shutdown()
            }
        } else {
            if (onReceive != null) {
                onReceive!!.invoke(value)
            }
        }
    }

    private fun onMessage(message: String) {
        val envelope = mapper.readValue(message, Envelope::class.java)
        val values = mapper.readValuesFromEnvelope(envelope)
        onReceivedValues(values)
    }

    private fun onError(e: Exception) {
        log.log(Level.WARNING, "Network client error: ", e)
        wsClient.close()
        connected = false
    }

    private fun onClose(code: Int, reason: String, remote: Boolean) {
        log.log(Level.FINE, "Network client disconnected: $reason (remote: $remote)")
        connected = false
        if (onDisconnected != null) {
            onDisconnected!!.invoke(reason)
        }
    }

}