package it.polito.g21.crm.utils

enum class MachineStateType(val value : String){
    RECEIVED("received"),
    READ("read"),
    DISCARDED("discarded"),
    PROCESSING("processing"),
    DONE("done"),
    FAILED("failed")
}