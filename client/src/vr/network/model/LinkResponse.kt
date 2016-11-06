package vr.network.model

import java.util.*

data class LinkResponse(var success : Boolean = false, var clientCellUris:Array<String> = arrayOf(), var serverCellUris:Array<String> = arrayOf(), var errorMessage : String = "")