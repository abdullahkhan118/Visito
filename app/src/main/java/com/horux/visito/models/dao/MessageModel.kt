package com.horux.visito.models.dao

class MessageModel {
    var message: String? = null
    var id: String? = null

    constructor()
    constructor(message: String?) {
        this.message = message
    }

    constructor(message: String?, id: String?) {
        this.message = message
        this.id = id
    }
}
