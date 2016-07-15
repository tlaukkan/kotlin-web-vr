package org.bubblecloud.webvr

import logger
import org.bubblecloud.webvr.model.Node
import java.net.URI
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("nodes")
class NodesResource {
    val log = logger()

    @POST fun addNode(node: Node, @Context uriInfo: UriInfo): Response {
        val nodeId = UUID.randomUUID()
        val builder = uriInfo.absolutePathBuilder
        builder.path(nodeId.toString())
        node.id = nodeId
        node.uri = URI.create("${uriInfo.baseUri}nodes/$nodeId")

        CELL.addNode(node)

        log.debug("Created $nodeId.")
        return Response.created(builder.build()).entity(nodeId).build()
    }

    @Path("{nodeId}")
    @PUT fun updateNode(@PathParam("nodeId") nodeId: UUID, node: Node, @Context uriInfo: UriInfo): Response {
        node.id = nodeId
        node.uri = URI.create("${uriInfo.baseUri}nodes/$nodeId")

        if (CELL.updateNode(node)) {
            log.debug("Updated $nodeId.")
            return Response.ok().build()
        } else {
            log.debug("Node to update not found: $nodeId.")
            return Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @Path("{nodeId}")
    @DELETE fun deleteNode(@PathParam("nodeId") nodeId: UUID, @Context uriInfo: UriInfo): Response {
        val nodeUri : URI = URI.create("${uriInfo.baseUri}nodes/$nodeId")
        if (CELL.removeNode(nodeUri)) {
            log.debug("Deleted $nodeId.")
            return Response.ok().build()
        } else {
            log.debug("Node to delete not found: $nodeId.")
            return Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @Path("{nodeId}")
    @GET fun getNode(@PathParam("nodeId") nodeId: UUID, @Context uriInfo: UriInfo): Node? {
        val nodeUri : URI = URI.create("${uriInfo.baseUri}nodes/$nodeId")
        if (CELL.hasNode(nodeUri)) {
            return CELL.getNode(nodeUri)
        } else {
            return null
        }
    }

    @GET fun getNodes(): List<Node> {
        return CELL.getNodes()
    }
}
