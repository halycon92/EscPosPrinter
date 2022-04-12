package it.cretu.escposprinter.model

/**
 * Represents Connection Attempt Result
 * If connection is not successfull, Exception will be thrown
 */
class ConnectionResult {
    var connectionOk : Boolean = true
    var connectionError : String = ""
}