package it.polito.g21.analytics.services

import it.polito.g21.analytics.models.CountDTO
import it.polito.g21.analytics.models.TimeDTO

interface AnalyticsService {
    fun getJobs(status: String, month: Int?, year: Int) : List<CountDTO>

    fun getContacts(cat: String, month: Int?, year: Int) : List<CountDTO>

    fun getJobsTime(year: Int) : List<TimeDTO>
}