package vr.network.model

data class HandshakeResponse(var software : String = "",
                             var protocolDialect : String = "",
                             var protocolVersion : String = "",
                             var serverCellUris: Array<String> = emptyArray(),
                             var accepted: Boolean = false)