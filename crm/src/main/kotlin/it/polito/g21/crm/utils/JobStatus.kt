package it.polito.g21.crm.utils

enum class JobStatus(val value : String) {
    CREATED("created"),
    SELECTION_PHASE("selection_phase"),
    CANDIDATE_PROPOSAL("candidate_proposal"),
    CONSOLIDATED("consolidated"),
    DONE("done"),
    ABORTED("aborted")
}