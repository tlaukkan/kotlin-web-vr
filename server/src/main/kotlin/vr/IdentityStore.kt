package vr

import vr.model.Identity
import java.net.URI
import java.util.*

/**
 * Created by tlaukkan on 7/9/2016.
 */
class IdentityStore {
    private val identitys: MutableMap<URI, Identity> = HashMap()

    @Synchronized fun addIdentity(identity: Identity) : Boolean {
        if (identitys.containsKey(identity.uri)) {
            return false
        }
        identitys[identity.uri] = identity
        return true
    }

    @Synchronized fun updateIdentity(identity: Identity) : Boolean {
        if (!identitys.containsKey(identity.uri)) {
            return false
        }
        identitys[identity.uri] = identity
        return true
    }

    @Synchronized fun removeIdentity(uri: URI) : Boolean {
        if (!identitys.containsKey(uri)) {
            return false
        }
        identitys.remove(uri)
        return true
    }

    @Synchronized fun hasIdentity(uri: URI) : Boolean {
        return identitys.containsKey(uri)
    }

    @Synchronized fun getIdentity(uri: URI) : Identity {
        if (!identitys.containsKey(uri)) {
            throw IllegalArgumentException("No such identity: " + uri)
        }
        return identitys[uri]!!
    }

    @Synchronized fun getIdentities() : List<Identity>  {
        return ArrayList<Identity>(identitys.values)
    }
}