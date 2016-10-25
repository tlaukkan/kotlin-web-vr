package org.bubblecloud.webvr.model

import java.util.*

data class Message(var type: String = "console-message",
                   var properties: Map<String, Any> = TreeMap()) {

}
