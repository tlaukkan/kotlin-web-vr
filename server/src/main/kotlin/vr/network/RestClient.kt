package vr.network

import vr.network.model.Node
import java.io.IOException
import java.util.*
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by tlaukkan on 10/29/2016.
 */
class RestClient<T>(url: String, val path: String, val valueObjectClass: Class<T>) {

    val client = ClientBuilder.newClient()
    val target = client.target(url)

    fun get(): List<T> {
        return target.path(path).request().get(object : GenericType<List<T>>(){
        })
    }

    fun get(id: UUID): Node {
        return target.path("$path/$id").request().get(object : GenericType<Node>() {} )
    }

    fun post(value: T): UUID {
        val response = target.path(path).request().post(Entity.entity(value, MediaType.APPLICATION_JSON))
        if (response.status!=Response.Status.CREATED.statusCode) {
            throw IOException("Rest put failed with status code: ${response.status}.")
        }
        return response.readEntity(UUID::class.java)
    }

    fun put(id: UUID, value: T) {
        target!!.path("$path/$id").request().put(Entity.entity(value, MediaType.APPLICATION_JSON))
    }

    fun delete(id: UUID) {
        target!!.path("$path/$id").request().delete()
    }

}