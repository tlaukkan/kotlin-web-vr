package org.bubblecloud.webvr

import java.util.*
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("nodes")
@Produces(MediaType.APPLICATION_JSON)
class NodesResource {

    @GET fun doGet(): List<Node> {
        return Collections.singletonList(Node(1))
    }

    @POST fun doPost(nodes: List<Node>) {
        println(nodes)
    }

}