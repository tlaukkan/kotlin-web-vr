package vr.network.rest

import logger
import vr.IDENTITY_STORE
import vr.server.model.Identity
import java.net.URI
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("identities")
class IdentityRestService {
    val log = logger()

    @POST fun addIdentity(identity: Identity, @Context uriInfo: UriInfo): Response {
        val identityId = UUID.randomUUID()
        val builder = uriInfo.absolutePathBuilder
        builder.path(identityId.toString())
        identity.id = identityId
        identity.uri = URI.create("${uriInfo.baseUri}identitys/$identityId")

        IDENTITY_STORE.addIdentity(identity)

        log.fine("Created $identityId.")
        return Response.created(builder.build()).entity(identityId).build()
    }

    @Path("{identityId}")
    @PUT fun updateIdentity(@PathParam("identityId") identityId: UUID, identity: Identity, @Context uriInfo: UriInfo): Response {
        identity.id = identityId
        identity.uri = URI.create("${uriInfo.baseUri}identitys/$identityId")

        if (IDENTITY_STORE.updateIdentity(identity)) {
            log.fine("Updated $identityId.")
            return Response.ok().build()
        } else {
            log.fine("Identity to update not found: $identityId.")
            return Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @Path("{identityId}")
    @DELETE fun deleteIdentity(@PathParam("identityId") identityId: UUID, @Context uriInfo: UriInfo): Response {
        val identityUri : URI = URI.create("${uriInfo.baseUri}identitys/$identityId")
        if (IDENTITY_STORE.removeIdentity(identityUri)) {
            log.fine("Deleted $identityId.")
            return Response.ok().build()
        } else {
            log.fine("Identity to delete not found: $identityId.")
            return Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @Path("{identityId}")
    @GET fun getIdentity(@PathParam("identityId") identityId: UUID, @Context uriInfo: UriInfo): Identity? {
        val identityUri : URI = URI.create("${uriInfo.baseUri}identitys/$identityId")
        if (IDENTITY_STORE.hasIdentity(identityUri)) {
            return IDENTITY_STORE.getIdentity(identityUri)
        } else {
            return null
        }
    }

    @GET fun getIdentities(): List<Identity> {
        return IDENTITY_STORE.getIdentities()
    }
}
