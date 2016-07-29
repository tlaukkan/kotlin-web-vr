export class Node {
    id: string;
    uri: string;
    parentUri: string;
    removed: boolean;
    properties: {[key: string]: any} = {}
}