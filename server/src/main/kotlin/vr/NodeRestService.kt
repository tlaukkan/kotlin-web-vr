package vr

import logger
import vr.network.model.Node
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("nodes")
class NodeRestService {
    val log = logger()

    @POST fun addNode(node: Node, @Context uriInfo: UriInfo): Response {
        val nodeId = UUID.randomUUID().toString()
        val builder = uriInfo.absolutePathBuilder
        builder.path(nodeId.toString())
        node.id = nodeId
        node.url = "${uriInfo.baseUri}nodes/$nodeId"

        CELL.addNode(node)

        log.fine("Created $nodeId.")
        return Response.created(builder.build()).entity(nodeId).build()
    }

    @Path("{nodeId}")
    @PUT fun updateNode(@PathParam("nodeId") nodeId: String, node: Node, @Context uriInfo: UriInfo): Response {
        node.id = nodeId
        node.url = "${uriInfo.baseUri}nodes/$nodeId"

        if (CELL.updateNode(node)) {
            log.fine("Updated $nodeId.")
            return Response.ok().build()
        } else {
            log.fine("Node to update not found: $nodeId.")
            return Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @Path("{nodeId}")
    @DELETE fun deleteNode(@PathParam("nodeId") nodeId: String, @Context uriInfo: UriInfo): Response {
        val nodeUrl : String = "${uriInfo.baseUri}nodes/$nodeId"
        if (CELL.removeNode(nodeUrl)) {
            log.fine("Deleted $nodeId.")
            return Response.ok().build()
        } else {
            log.fine("Node to delete not found: $nodeId.")
            return Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @Path("{nodeId}")
    @GET fun getNode(@PathParam("nodeId") nodeId: UUID, @Context uriInfo: UriInfo): Node? {
        val nodeUrl : String = "${uriInfo.baseUri}nodes/$nodeId"
        if (CELL.hasNode(nodeUrl)) {
            return CELL.getNode(nodeUrl)
        } else {
            return null
        }
    }

    @GET fun getNodes(): List<Node> {
        return CELL.getNodes()
    }
}
