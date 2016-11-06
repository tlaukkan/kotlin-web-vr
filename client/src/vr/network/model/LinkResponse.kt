package vr.network.model

data class LinkResponse(var success : Boolean = false, var clientCellUris:Array<String> = arrayOf(), var serverCellUris:Array<String> = arrayOf(),
                        var neighbours:Array<Neighbour> = arrayOf(), var errorMessage : String = "")