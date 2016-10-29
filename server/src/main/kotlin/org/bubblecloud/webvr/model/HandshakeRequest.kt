package org.bubblecloud.webvr.model

data class HandshakeRequest(var software : String = "",
                            var protocolDialect : String = "",
                            var protocolVersions : Array<String> = arrayOf())