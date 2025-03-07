// API.tsx

import { ActionOnJobInterface, AddressInterface, CountInterface, CustomerFormData, EmailInterface, FileData, JobOfferFormData, JobOfferInterface, JobStatusFormData, ProfessionalFormData, SpecificContactInterface, TelephoneInterface } from "./interfaces/interfaces";

const url = "http://localhost:8080/crm/API/";

export const fetchUser = () => {
    return fetch("/me")
        .then(res => res.json())
        .catch(error => {
            console.error("Error fetching /me:", error);
            return null;
        });
};

export const fetchUserRole = (email: string, xsrfToken: string) => {
    return fetch(url + "user/data", {
        method: "POST",
        headers: {
            "Content-type": "application/json",
            "X-XSRF-TOKEN": xsrfToken
        },
        body: JSON.stringify({ email: email.toLowerCase() })
    })
        .then(res => res.json())
        .catch(error => {
            console.error("Error fetching user data:", error);
            return {};
        });
};

export const fetchAllJobOffers = (): Promise<JobOfferInterface[]> => {
    return fetch(url + "joboffers/")
        .then(res => res.json())
        .then(data => {
            return data.jobOffers || [];
        })
        .catch(error => {
            console.error("Error fetching job offers:", error);
            return [];
        });
};

export const fetchAllCustomers = (): Promise<SpecificContactInterface[]> => {
    return fetch(url + "customers/")
        .then(res => res.json())
        .then(data => {
            return data.customers || [];
        })
        .catch(error => {
            console.error("Error fetching customers:", error);
            return [];
        });
};

export const fetchAllProfessional = (): Promise<SpecificContactInterface[]> => {
    return fetch(url + "professionals/")
        .then(res => res.json())
        .then(data => {
            return data.professionals || [];
        })
        .catch(error => {
            console.error("Error fetching professionals:", error);
            return [];
        });
};

export const createJobOffer = (jobOffer: JobOfferFormData, customerId: string, xsrfToken: string) => {
    return fetch(url + "joboffers/?customer=" + customerId, {
        method: "POST",
        headers: {
            "Content-type": "application/json",
            "X-XSRF-TOKEN": xsrfToken
        },
        body: JSON.stringify(jobOffer)
    })
        .then(res => res.json())
        .then(data => {
            return data || {};
        })
        .catch(error => {
            console.error("Error creating job offer:", error);
            return {};
        });
};

export const createCustomer = (customer: CustomerFormData, xsrfToken: string) => {
    return fetch(url + "customers/", {
        method: "POST",
        headers: {
            "Content-type": "application/json",
            "X-XSRF-TOKEN": xsrfToken
        },
        body: JSON.stringify(customer)
    })
        .then(res => res.json())
        .then(data => {
            return data || {};
        })
        .catch(error => {
            console.error("Error creating customer:", error);
            return {};
        });
};

export const createProfessional = (professional: ProfessionalFormData, xsrfToken: string) => {
    return fetch(url + "professionals/", {
        method: "POST",
        headers: {
            "Content-type": "application/json",
            "X-XSRF-TOKEN": xsrfToken
        },
        body: JSON.stringify(professional)
    })
        .then(res => res.json())
        .then(data => {
            return data || {};
        })
        .catch(error => {
            console.error("Error creating professional:", error);
            return {};
        });
};

export const fetchJobOffer = (id: string): Promise<JobOfferInterface> => {
    return fetch(url + "joboffers/" + id)
        .then(res => res.json())
        .then(data => {
            return data || {};
        })
        .catch(error => {
            console.error("Error fetching job offer:", error);
            return {};
        });
};

export const updateJobOffer = (jobOffer: JobOfferFormData, id: string, xsrfToken: string) => {
    return fetch(url + "joboffers/" + id, {
        method: "PUT",
        headers: {
            "Content-type": "application/json",
            "X-XSRF-TOKEN": xsrfToken
        },
        body: JSON.stringify(jobOffer)
    })
        .catch(error => {
            console.error("Error updating job offer:", error);
            return {};
        });
}

export const fetchJobOfferHistory = (id: string): Promise<ActionOnJobInterface[]> => {
    return fetch(url + "joboffers/history/joboffer/" + id)
        .then(res => res.json())
        .then(data => {
            return data["job offer history"] || [];
        })
        .catch(error => {
            console.error("Error fetching job offer history:", error);
            return [];
        });
};


export const updateJobOfferStatus = async (id: string, status: JobStatusFormData, xsrfToken: string): Promise<void> => {
    try {
        const response = await fetch(url + "joboffers/" + id, {
            method: "POST",
            headers: {
                "Content-type": "application/json",
                "X-XSRF-TOKEN": xsrfToken
            },
            body: JSON.stringify(status)
        });
        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}: ${response.statusText}`);
        }
    } catch (error) {
        console.error("Error updating job offer status:", error);
        throw error;
    }
};

export const fetchProfessional = (id: string): Promise<SpecificContactInterface> => {
    return fetch(url + "professionals/" + id)
        .then(res => res.json())
        .then(data => {
            return data || {};
        })
        .catch(error => {
            console.error("Error fetching professional:", error);
            return {};
        });
};

export const fetchCustomer = (id: string): Promise<SpecificContactInterface> => {
    return fetch(url + "customers/" + id)
        .then(res => res.json())
        .then(data => {
            return data || {};
        })
        .catch(error => {
            console.error("Error fetching customer:", error);
            return {};
        });
};

export const addEmailToContact = (id: string, email: EmailInterface, xsrfToken: string) => {
    return fetch(url + "contacts/" + id + "/email/", {
        method: "POST",
        headers: {
            "Content-type": "application/json",
            "X-XSRF-TOKEN": xsrfToken
        },
        body: JSON.stringify(email)
    })
        .catch(error => {
            console.error("Error adding email to contact:", error);
            return {};
        });
}

export const addAddressToContact = (id: string, address: AddressInterface, xsrfToken: string) => {
    return fetch(url + "contacts/" + id + "/address/", {
        method: "POST",
        headers: {
            "Content-type": "application/json",
            "X-XSRF-TOKEN": xsrfToken
        },
        body: JSON.stringify(address)
    })
        .catch(error => {
            console.error("Error adding address to contact:", error);
            return {};
        });
}

export const addPhoneToContact = (id: string, telephone: TelephoneInterface, xsrfToken: string) => {
    return fetch(url + "contacts/" + id + "/telephone/", {
        method: "POST",
        headers: {
            "Content-type": "application/json",
            "X-XSRF-TOKEN": xsrfToken
        },
        body: JSON.stringify(telephone)
    })
        .catch(error => {
            console.error("Error adding telephone to contact:", error);
            return {};
        });
}

export const deleteEmail = (id: string, emailid: string, xsrfToken: string) => {
    return fetch(url + "contacts/" + id + "/email/" + emailid, {
        method: "DELETE",
        headers: {
            "X-XSRF-TOKEN": xsrfToken
        }
    })
        .catch(error => {
            console.error("Error deleting email:", error);
            return {};
        });
};

export const deleteAddress = (id: string, addressid: string, xsrfToken: string) => {
    return fetch(url + "contacts/" + id + "/address/" + addressid, {
        method: "DELETE",
        headers: {
            "X-XSRF-TOKEN": xsrfToken
        }
    })
        .catch(error => {
            console.error("Error deleting address:", error);
            return {};
        });
};

export const deletePhone = (id: string, phoneid: string, xsrfToken: string) => {
    return fetch(url + "contacts/" + id + "/telephone/" + phoneid, {
        method: "DELETE",
        headers: {
            "X-XSRF-TOKEN": xsrfToken
        }
    })
        .catch(error => {
            console.error("Error deleting phone:", error);
            return {};
        });
};

export const deleteCustomer = async (id: string, xsrfToken: string) => {
    try {
        const response = await fetch(url + "customers/" + id, {
            method: "DELETE",
            headers: {
                "X-XSRF-TOKEN": xsrfToken
            }
        });

        if (!response.ok) {
            const errorMessage = `The customers cannot be deleted since he has some jobOffer related to him.`;
            throw new Error(errorMessage);
        }
    } catch (error) {
        console.error(error);
        throw error;
    }
};

export const deleteProfessional = async (id: string, xsrfToken: string) => {
    try {
        const response = await fetch(url + "professionals/" + id, {
            method: "DELETE",
            headers: {
                "X-XSRF-TOKEN": xsrfToken
            }
        });

        if (!response.ok) {
            const errorMessage = `The professional cannot be deleted since he has some jobOffer related to him.`;
            throw new Error(errorMessage);
        }
    } catch (error) {
        console.error(error);
        throw error;
    }
};


export const updateCustomer = (customer: CustomerFormData, id: string, xsrfToken: string) => {
    return fetch(url + "customers/" + id, {
        method: "PUT",
        headers: {
            "Content-type": "application/json",
            "X-XSRF-TOKEN": xsrfToken
        },
        body: JSON.stringify(customer)
    })
        .catch(error => {
            console.error("Error updating customer:", error);
            return {};
        });
}

export const updateProfessional = (professional: ProfessionalFormData, id: string, xsrfToken: string) => {
    return fetch(url + "professionals/" + id, {
        method: "PUT",
        headers: {
            "Content-type": "application/json",
            "X-XSRF-TOKEN": xsrfToken
        },
        body: JSON.stringify(professional)
    })
        .catch(error => {
            console.error("Error updating professional:", error);
            return {};
        });
}

export const analyticsCreatedJobs = (year: string, month: string | null): Promise<CountInterface[]> => {
    return fetch("http://localhost:8080/analytics/API/" + "jobs/created/" + "?year=" + year + (month ? ("&month=" + month) : ""))
        .then(res => res.json())
        .then(data => {
            return data || [];
        })
        .catch(error => {
            console.error("Error fetching analytics created jobs:", error);
            return [];
        });
};

export const analyticsConsolidatedJobs = (year: string, month: string | null): Promise<CountInterface[]> => {
    return fetch("http://localhost:8080/analytics/API/" + "jobs/consolidated/" + "?year=" + year + (month ? ("&month=" + month) : ""))
        .then(res => res.json())
        .then(data => {
            return data || [];
        })
        .catch(error => {
            console.error("Error fetching analytics consolidated jobs:", error);
            return [];
        });
};

export const analyticsFailedJobs = (year: string, month: string | null): Promise<CountInterface[]> => {
    return fetch("http://localhost:8080/analytics/API/" + "jobs/failed/" + "?year=" + year + (month ? ("&month=" + month) : ""))
        .then(res => res.json())
        .then(data => {
            return data || [];
        })
        .catch(error => {
            console.error("Error fetching analytics failed jobs:", error);
            return [];
        });
};

export const analyticsAbortedJobs = (year: string, month: string | null): Promise<CountInterface[]> => {
    return fetch("http://localhost:8080/analytics/API/" + "jobs/aborted/" + "?year=" + year + (month ? ("&month=" + month) : ""))
        .then(res => res.json())
        .then(data => {
            return data || [];
        })
        .catch(error => {
            console.error("Error fetching analytics aborted jobs:", error);
            return [];
        });
};

export const analyticsTimeJobs = (year: string): Promise<CountInterface[]> => {
    return fetch("http://localhost:8080/analytics/API/" + "jobs/time/" + "?year=" + year)
        .then(res => res.json())
        .then(data => {
            return data || [];
        })
        .catch(error => {
            console.error("Error fetching analytics aborted jobs:", error);
            return [];
        });
};

export const analyticsCustomerCreated = (year: string, month: string | null): Promise<CountInterface[]> => {
    return fetch("http://localhost:8080/analytics/API/" + "customers/" + "?year=" + year + (month ? ("&month=" + month) : ""))
        .then(res => res.json())
        .then(data => {
            return data || [];
        })
        .catch(error => {
            console.error("Error fetching analytics customers:", error);
            return [];
        });
};

export const analyticsProfessionalCreated = (year: string, month: string | null): Promise<CountInterface[]> => {
    return fetch("http://localhost:8080/analytics/API/" + "professionals/" + "?year=" + year + (month ? ("&month=" + month) : ""))
        .then(res => res.json())
        .then(data => {
            return data || [];
        })
        .catch(error => {
            console.error("Error fetching analytics professionals:", error);
            return [];
        });
};

export const insertNewDocument = (document: FileData, jobId: string | null, contactId: string | null, xsrfToken: string) => {
    return fetch("http://localhost:8080/docStore/API/" + "documents/" + (jobId ? "?jobId=" + jobId : "?contactId=" + contactId), {
        method: "POST",
        headers: {
            "Content-type": "application/json",
            "X-XSRF-TOKEN": xsrfToken
        },
        body: JSON.stringify(
            {
                "name": document.name + new Date().getTime(),
                "contentType": document.type,
                "size": document.size,
                "bytes": Array.from(new Uint8Array(document.content ? document.content : new ArrayBuffer(0)))
            }
        )
    })
        .catch(error => {
            console.error("Error inserting new document:", error);
            return {};
        });
}

export const fetchContactDocuments = (contactId: string) => {
    return fetch("http://localhost:8080/docStore/API/" + "documents/contact/" + contactId + "/data")
        .then(async res => {
            const contentType = res.headers.get('Content-Type');
            const blob = await res.blob();
            const fileUrl = URL.createObjectURL(blob);
            if (!res.ok) {
                throw new Error(`Request failed with status ${res.status}: ${res.statusText}`);
            }
            return { "content": contentType, "url": fileUrl };
        }
        )
        .catch(error => {
            console.error("Error fetching contact documents:", error);
            return { "content": null, "url": null };
        });
}

export const fetchMetadataDocument = (contactId: string) => {
    return fetch("http://localhost:8080/docStore/API/" + "documents/contact/" + contactId)
        .then(res => res.json())
        .then(data => {
            return data.id || '';
        })
        .catch(error => {
            console.error("Error fetching contact documents:", error);
            return '';
        });
}

export const deleteDocument = (documentId: string, xsrfToken: string) => {
    return fetch("http://localhost:8080/docStore/API/documents/" + documentId, {
        method: "DELETE",
        headers: {
            "X-XSRF-TOKEN": xsrfToken
        }
    })
        .catch(error => {
            console.error("Error deleting document:", error);
            return {};
        });
}

export const fetchJobDocuments = (jobId: string) => {
    return fetch("http://localhost:8080/docStore/API/" + "documents/job/" + jobId + "/data")
        .then(async res => {
            const contentType = res.headers.get('Content-Type');
            const blob = await res.blob();
            const fileUrl = URL.createObjectURL(blob);
            if (!res.ok) {
                throw new Error(`Request failed with status ${res.status}: ${res.statusText}`);
            }
            return { "content": contentType, "url": fileUrl };
        }
        )
        .catch(error => {
            console.error("Error fetching job documents:", error);
            return { "content": null, "url": null };
        });
}

export const fetchMetadataDocumentJob = (jobId: string) => {
    return fetch("http://localhost:8080/docStore/API/" + "documents/job/" + jobId)
        .then(res => res.json())
        .then(data => {
            return data.id || '';
        })
        .catch(error => {
            console.error("Error fetching job documents:", error);
            return '';
        });
}