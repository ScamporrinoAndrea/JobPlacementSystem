package it.polito.g21.analytics.controllers

import it.polito.g21.analytics.models.CountDTO
import it.polito.g21.analytics.models.TimeDTO
import it.polito.g21.analytics.services.AnalyticsService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/API")
@EnableWebSecurity
class AnalyticsController(val service: AnalyticsService) {

    @GetMapping("/jobs/created","/jobs/created/")
    @PreAuthorize("hasRole('manager')")
    @ResponseStatus(HttpStatus.OK)
    fun getCreatedJobs( @RequestParam month: Int?,
                 @RequestParam year: Int) : List<CountDTO>{
        return service.getJobs("created" , month, year)
    }

    @GetMapping("/jobs/consolidated","/jobs/consolidated/")
    @PreAuthorize("hasRole('manager')")
    @ResponseStatus(HttpStatus.OK)
    fun getConsolidatedJobs( @RequestParam month: Int?,
                        @RequestParam year: Int) : List<CountDTO>{
        return service.getJobs("consolidated" , month, year)
    }

    @GetMapping("/jobs/failed","/jobs/failed/")
    @PreAuthorize("hasRole('manager')")
    @ResponseStatus(HttpStatus.OK)
    fun getFailedJobs( @RequestParam month: Int?,
                        @RequestParam year: Int) : List<CountDTO>{
        return service.getJobs("failed" , month, year)
    }

    @GetMapping("/jobs/aborted","/jobs/aborted/")
    @PreAuthorize("hasRole('manager')")
    @ResponseStatus(HttpStatus.OK)
    fun getAbortedJobs( @RequestParam month: Int?,
                       @RequestParam year: Int) : List<CountDTO>{
        return service.getJobs("aborted" , month, year)
    }

    @GetMapping("/customers","/customers/")
    @PreAuthorize("hasRole('manager')")
    @ResponseStatus(HttpStatus.OK)
    fun getCustomer( @RequestParam month: Int?,
                             @RequestParam year: Int) : List<CountDTO>{
        return service.getContacts("customer" , month, year)
    }

    @GetMapping("/professionals","/professionals/")
    @PreAuthorize("hasRole('manager')")
    @ResponseStatus(HttpStatus.OK)
    fun getProfessionals( @RequestParam month: Int?,
                       @RequestParam year: Int) : List<CountDTO>{
        return service.getContacts("professional" , month, year)
    }

    @GetMapping("/jobs/time","/jobs/time/")
    @PreAuthorize("hasRole('manager')")
    @ResponseStatus(HttpStatus.OK)
    fun getJobsTime(@RequestParam year: Int) : List<TimeDTO>{
        return service.getJobsTime(year)
    }
}