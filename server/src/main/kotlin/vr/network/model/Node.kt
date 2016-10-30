package vr.network.model

open class Node(var id: String = "00000000-0000-0000-0000-000000000000",
                var url: String = "http://0.0.0.0/nodes/$id",
                var parentUrl: String? = null,
                var removed: Boolean = false,
                var position: DataVector3 = DataVector3(),
                var orientation: DataQuaternion = DataQuaternion(),
                var scale: DataVector3 = DataVector3(1.0, 1.0, 1.0))
