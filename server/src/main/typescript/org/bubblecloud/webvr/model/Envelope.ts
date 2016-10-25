import {Message} from "./Message";
import {Node} from "./Node";

export class Envelope {
    messages: Message[] = [];
    nodes: Node[] = [];
}