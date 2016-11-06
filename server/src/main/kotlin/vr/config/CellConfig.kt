package vr.config

import vr.network.model.DataVector3
import vr.network.model.LightFieldNode
import vr.network.model.PrimitiveNode
import java.util.*

class CellConfig(var name: String = "",
                 var neighbours: Map<String, DataVector3> = TreeMap<String, DataVector3>(),
                 var primitives: List<PrimitiveNode> = ArrayList<PrimitiveNode>(),
                 var lightFields: List<LightFieldNode> = ArrayList<LightFieldNode>())