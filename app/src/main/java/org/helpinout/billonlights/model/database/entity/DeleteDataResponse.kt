package org.helpinout.billonlights.model.database.entity

class DeleteDataResponses {
    var status: Int = 0
    var message: String = ""
    var data: DeleteDataData? = null
}

class DeleteDataData {
    var activity_uuid: String? = null
    var status: Int = 0
}