package org.bubblecloud.webvr

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Gets nodes.
 */
@Path("nodes")
@Produces(MediaType.APPLICATION_JSON)
class NodesResource {

    /**
     * Gets nodes.
     * @return the nodes
     */
    @GET fun doGet(): Node {
        return Node(1)
    }

}