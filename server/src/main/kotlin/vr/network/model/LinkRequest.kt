package vr.network.model

import java.util.*

data class LinkRequest(var clientCellUris:Array<String> = arrayOf(),
                       var serverCellUris:Array<String> = arrayOf(),
                       var neighbours:Array<Neighbour> = arrayOf())