package org.bubblecloud.webvr

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

    @POST fun createNode(node: Node, @Context uriInfo: UriInfo): Response {
        val nodeId = UUID.randomUUID()
        val builder = uriInfo.absolutePathBuilder
        builder.path(nodeId.toString())
        node.id = nodeId
        println("Created $node.")
        return Response.created(builder.build()).build()
    }

    @Path("{nodeId}")
    @PUT fun updateNode(@PathParam("nodeId") nodeId: UUID, node: Node): Response {
        println("Updated $nodeId : $node.")
        return Response.ok().build()
    }

    @Path("{nodeId}")
    @DELETE fun deleteNode(@PathParam("nodeId") nodeId: UUID): Response {
        println("Deleted $nodeId.")
        return Response.ok().build()
    }

    @Path("{nodeId}")
    @GET fun getNode(@PathParam("nodeId") nodeId: UUID): Node {
        return Node(nodeId)
    }

    @GET fun getNodes(): List<Node> {
        return Collections.singletonList(Node(UUID.randomUUID()))
    }
}
