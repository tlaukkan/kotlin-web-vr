package vr.network

import logger
import vr.Cell
import vr.network.model.HandshakeResponse
import vr.network.model.LinkRequest
import vr.network.model.Node
import java.net.URI
import java.net.URL
import java.util.*
import java.util.logging.Level
import kotlin.concurrent.fixedRateTimer

class NetworkLinker(val networkServer: NetworkServer) {

    private val log = logger()

    private var timer: Timer? = null

    private var clients: MutableMap<String, NetworkClient> = mutableMapOf()

    init {
    }

    fun startup() {
        timer = fixedRateTimer( name = "network-linker-timer",
                                initialDelay = 1000,
                                period = 5000) {
            process()
        }
    }

    fun shutdown() {
        timer!!.cancel()
    }

    private fun process() {
        for (cell in networkServer.getCells()) {
            if (cell.remoteCell) {
                if (!clients.containsKey(cell.serverUrl)) {
                    log.info("Linker connecting to remote server ${cell.serverUrl}")

                    val receivedNodes: MutableList<Node> = mutableListOf()
                    val client = NetworkClient("${cell.serverUrl}ws")
                    client.onConnected = { handshakeResponse_ ->
                        log.info("Linker connected to remote server ${cell.serverUrl}")

                        //TODO make sure that server expands server cell list with neighbours
                        val serverCellUris: MutableList<String> = mutableListOf()
                        val clientCellUris: MutableList<String> = mutableListOf()
                        for (candidateCell in networkServer.getCells()) {
                            if (candidateCell.remoteCell && candidateCell.serverUrl.equals(cell.serverUrl)) {
                                serverCellUris.add(candidateCell.cellUri)
                                for (candidateNeighbourCellUri in candidateCell.neighbours.keys) {
                                    if (networkServer.hasCell(candidateNeighbourCellUri)) {
                                        val candidateNeighbourCell = networkServer.getCell(candidateNeighbourCellUri)
                                        if (!candidateNeighbourCell.remoteCell) {
                                            clientCellUris.add(candidateNeighbourCellUri)
                                            for (candidateNeighbourNeighbourCellUri in candidateNeighbourCell.neighbours.keys) {
                                                if (networkServer.hasCell(candidateNeighbourNeighbourCellUri)) {
                                                    val candidateNeighbourNeighbourCell = networkServer.getCell(candidateNeighbourNeighbourCellUri)
                                                    if (!candidateNeighbourNeighbourCell.remoteCell) {
                                                        clientCellUris.add(candidateNeighbourNeighbourCellUri)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        client.send(listOf(LinkRequest(clientCellUris.toTypedArray(), serverCellUris.toTypedArray())))

                    }
                    client.onLinked = { linkResponse ->
                        log.info("Linker linked to remote server ${cell.serverUrl}")
                        for (serverCellUri in linkResponse.serverCellUris) {
                            if (!networkServer.hasCell(serverCellUri)) {
                                networkServer.addCell(Cell(serverCellUri, true))
                            }
                        }
                    }
                    client.onDisconnected = { reason ->
                        clients.remove(cell.serverUrl)
                    }
                    client.onReceive = { value ->
                        if (value is Node) {
                            receivedNodes.add(value)
                            log.info("Linker received node modification ${value.id} of type ${value.javaClass.simpleName}")
                        }
                    }
                    client.onAllReceived = {
                        networkServer.processReceivedNodes(receivedNodes)
                        receivedNodes.clear()
                    }

                    clients.put(cell.serverUrl, client)
                    client.connect()
                }
            }
        }
    }

}