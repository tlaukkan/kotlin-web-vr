package org.bubblecloud.webvr.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Envelope(var messages: List<Message>? = null, var nodes: List<Node>? = null) {

}
