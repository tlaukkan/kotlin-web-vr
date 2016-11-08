package vr.network

import logger
import vr.Cell
import vr.network.model.LinkRequest
import vr.network.model.Neighbour
import vr.network.model.Node
import java.util.*
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
                                period = 2000) {
            process()
        }
    }

    fun shutdown() {
        timer!!.cancel()
    }

    private fun process() {
        for (cell in networkServer.getCells()) {
            if (cell.remote) {
                if (!clients.containsKey(cell.serverUrl)) {
                    log.info("Linker connecting to remote server ${cell.serverUrl}")

                    val receivedNodes: MutableList<Node> = mutableListOf()
                    val client = NetworkClient("${cell.serverUrl}ws", networkServer.server.url)
                    client.onConnected = { handshakeResponse_ ->
                        log.info("Linker connected to remote server ${cell.serverUrl}")

                        val serverCellUris: MutableList<String> = mutableListOf()
                        val clientCellUris: MutableList<String> = mutableListOf()
                        val neighbours: MutableList<Neighbour> = mutableListOf()
                        for (candidateCell in networkServer.getCells()) {
                            if (candidateCell.remote && candidateCell.serverUrl.equals(cell.serverUrl)) {
                                serverCellUris.add(candidateCell.url)
                                for (candidateNeighbourCellUri in candidateCell.neighbours.keys) {
                                    if (networkServer.hasCell(candidateNeighbourCellUri)) {
                                        val candidateNeighbourCell = networkServer.getCell(candidateNeighbourCellUri)
                                        if (!candidateNeighbourCell.remote) {
                                            clientCellUris.add(candidateNeighbourCellUri)
                                            neighbours.add(Neighbour(candidateCell.url, candidateNeighbourCell.url, candidateCell.neighbours[candidateNeighbourCellUri]!!))
                                            for (candidateNeighbourNeighbourCellUri in candidateNeighbourCell.neighbours.keys) {
                                                if (networkServer.hasCell(candidateNeighbourNeighbourCellUri)) {
                                                    val candidateNeighbourNeighbourCell = networkServer.getCell(candidateNeighbourNeighbourCellUri)
                                                    if (!candidateNeighbourNeighbourCell.remote) {
                                                        clientCellUris.add(candidateNeighbourNeighbourCellUri)
                                                        neighbours.add(Neighbour(candidateNeighbourCell.url, candidateNeighbourNeighbourCell.url, candidateNeighbourCell.neighbours[candidateNeighbourNeighbourCellUri]!!))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        client.send(listOf(LinkRequest(clientCellUris.toTypedArray(), serverCellUris.toTypedArray(), neighbours.toTypedArray())))

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
                            if (value.url.startsWith(networkServer.server.url)) {
                                log.fine("Linker ignored received node modification ${value.url} of type ${value.javaClass.simpleName}")
                            } else {
                                receivedNodes.add(value)
                                log.info("Linker received node modification ${value.url} of type ${value.javaClass.simpleName}")
                            }
                        }
                    }
                    client.onAllReceived = {
                        if (receivedNodes.size > 0) {
                            networkServer.processReceivedNodes(receivedNodes)
                            log.info("Linker handed processing of ${receivedNodes.size} to network server.")
                            receivedNodes.clear()
                        }
                    }

                    clients.put(cell.serverUrl, client)
                    client.connect()
                }
            }
        }
    }

}