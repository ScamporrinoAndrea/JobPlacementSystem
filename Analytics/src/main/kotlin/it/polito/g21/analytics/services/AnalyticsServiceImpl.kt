package it.polito.g21.analytics.services

import it.polito.g21.analytics.exceptions.InvalidFormatException
import it.polito.g21.analytics.models.CountDTO
import it.polito.g21.analytics.models.TimeDTO
import it.polito.g21.analytics.repositories.ContactRepo
import it.polito.g21.analytics.repositories.JobRepo
import org.springframework.stereotype.Service

@Service
class AnalyticsServiceImpl(private val jobRepo: JobRepo, private val contactRepo: ContactRepo) : AnalyticsService {
    override fun getJobs(status: String, month: Int?, year: Int) : List<CountDTO> {
        if((month != null && (month > 12 || month < 0)) || year < 2020)
            throw InvalidFormatException("Request is in an invalid format!")

        return if(month == null){
            jobRepo.countJobsYearly(status, year)
        } else{
            jobRepo.countJobsMonthly(status, month, year)
        }
    }

    override fun getContacts(cat: String, month: Int?, year: Int): List<CountDTO> {
        if((month != null && (month > 12 || month < 0)) || year < 2020)
            throw InvalidFormatException("Request is in an invalid format!")

        return if(month == null){
            contactRepo.countContactsYearly(cat, year)
        } else{
            contactRepo.countContactsMonthly(cat, month, year)
        }
    }

    override fun getJobsTime(year: Int): List<TimeDTO> {
        if(year < 2020)
            throw InvalidFormatException("Request is in an invalid format!")
        return jobRepo.computeTimeYearly(year)
    }


}