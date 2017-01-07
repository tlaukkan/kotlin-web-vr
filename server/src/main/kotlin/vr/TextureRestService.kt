package vr

import logger
import vr.network.NetworkServer
import vr.network.model.Node
import java.io.File
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("textures")
class TextureRestService() {
    val log = logger()

    @GET fun getTextures(@Context uriInfo: UriInfo): List<String> {

        val textureDirectory = File("out/resources/main/webapp/textures")

        val textureFiles = textureDirectory.listFiles()

        val textures: MutableList<String> = mutableListOf()
        for (textureFile in textureFiles) {
            if (!textureFile.isDirectory()) {
                textures.add("textures/${textureFile.name}")
            }
        }

        return textures
    }
}
