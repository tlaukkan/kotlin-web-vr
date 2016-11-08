package vr.network.model

open class Node(var url: String = "",
                var parentUrl: String? = null,
                var removed: Boolean = false,
                var position: DataVector3 = DataVector3(),
                var orientation: DataQuaternion = DataQuaternion(),
                var scale: DataVector3 = DataVector3(1.0, 1.0, 1.0))
