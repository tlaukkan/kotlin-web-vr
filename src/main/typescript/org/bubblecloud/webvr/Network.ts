import {Envelope} from "./model/Envelope";
import {Message} from "./model/Message";
export class Network {

    webSocketUrl : string = "ws://" + window.location.hostname+(location.port ? ':' + location.port : "") + "/ws";
    socket : WebSocket;

    constructor() {

    }

    connect = () => {

        this.socket = new WebSocket(this.webSocketUrl);

        this.socket.onopen = () => {
            console.log("Socket opened: " + this.socket.url);

            var handshakeRequest = new Message();
            handshakeRequest.type = "handshake-request";
            handshakeRequest.properties = {
                "software" : "kotlin-web-vr",
                "protocol-dialect": "vr-state-synchronisation",
                "protocol-versions" : ["0.9", "1.0"]
            };

            var envelope = new Envelope();
            envelope.messages = [handshakeRequest];

            this.send(envelope);
        };

        this.socket.onclose = () =>  {
            console.log("Socket closed: " + this.socket.url);
        };

        this.socket.onerror = (event: Event) => {
            console.log("Socket error: " + this.socket.url + " - " + event);
        };

        this.socket.onmessage = (messageEvent: MessageEvent) => {
            this.onMessage(messageEvent);
        }

    };

    private onMessage = (messageEvent: MessageEvent) => {
        this.receive(JSON.parse(messageEvent.data))
    }

    receive = (envelope : Envelope) => {
        console.log("Received: " + JSON.stringify(envelope));
    }

    send = (envelope : Envelope) => {
        this.socket.send(JSON.stringify(envelope));
    }

}