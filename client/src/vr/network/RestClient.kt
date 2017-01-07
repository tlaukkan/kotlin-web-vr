package vr.network

import org.w3c.dom.events.Event
import org.w3c.xhr.XMLHttpRequest
import vr.util.fromJson

/**
 * Created by tlaukkan on 1/7/2017.
 */

class RestClient(var apiUrl: String) {

    fun <T : Any> get(resourceUrlFragment: String, callback: (response: T?) -> Unit) : Unit {
        val url = "$apiUrl/$resourceUrlFragment"
        val xmlHttp = XMLHttpRequest()
        xmlHttp.onreadystatechange = fun (event : Event) : Unit {
            if (xmlHttp.readyState as Int == 4) {
                if (xmlHttp.status as Int == 200) {
                    callback(fromJson<T>(xmlHttp.responseText))
                } else {
                    error("Response code not 200 for REST API GET at $url: ${xmlHttp.status}.")
                    callback(null)
                }
            }
            return
        }
        xmlHttp.open("GET", "$apiUrl/$resourceUrlFragment", true)
        xmlHttp.send()

    }

}