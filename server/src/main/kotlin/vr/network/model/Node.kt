package vr.network.model

data class Node(var id: String = "00000000-0000-0000-0000-000000000000",
                var url: String = "http://0.0.0.0/nodes/$id",
                var parentUri: String? = null,
                var removed: Boolean = false)
