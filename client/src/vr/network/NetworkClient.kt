package vr.network

import vr.network.model.Envelope
import vr.network.model.HandshakeRequest
import vr.network.model.HandshakeResponse

class NetworkClient(val url: String) {
    private val wsClient = WsClient(url)
    private val mapper = Mapper()

    var connected = false

    var onConnected: ((handshakeResponse: HandshakeResponse) -> Unit)? = null
    var onReceive: ((type: String, value: Any) -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null

    init {
        wsClient.onOpen = { startup() }
        wsClient.onMessage = { message -> onMessage(message) }
        wsClient.onError = { onError() }
        wsClient.onClose = { onClose() }
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

    private fun onReceivedValues(values: List<Pair<String, Any>>) {
        for (value in values) {
            processReceivedValue(value)
        }
    }

    private fun processReceivedValue(pair: Pair<String, Any>) {
        val type = pair.first
        val value : dynamic = pair.second
        println("$type, ${JSON.stringify(value)}")
        if ("HandshakeResponse".equals(type)) {
            val handshakeResponse: HandshakeResponse = value
            if (handshakeResponse.accepted) {
                connected = true
                if (onConnected != null) {
                    onConnected!!.invoke(handshakeResponse)
                }
            } else {
                shutdown()
            }
        } else {
            if (onReceive != null) {
                onReceive!!.invoke(type, value)
            }
        }
    }

    private fun onMessage(message: String) {
        val envelope : Envelope = mapper.readValue(message)
        val values = mapper.readValuesFromEnvelope(envelope)
        onReceivedValues(values)
    }

    private fun onError() {
        println("Network client error.")
        wsClient.close()
    }

    private fun onClose() {
        println("Network client disconnected.")
        connected = false
        if (onDisconnected != null) {
            onDisconnected!!.invoke()
        }
    }

}