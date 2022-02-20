package com.air.wuyunliuqi.model

class PielItem {
    var topText: String
    var secondaryText: String? = null
    var icon = 0
    var color = 0

    constructor(topText: String) {
        this.topText = topText
    }

    constructor(topText: String, secondaryText: String) {
        this.topText = topText
        this.secondaryText = secondaryText
    }
}