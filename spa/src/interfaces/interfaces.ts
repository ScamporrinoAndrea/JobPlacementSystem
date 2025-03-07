// Enum for job status
export enum JobStatus {
    CREATED = "created",
    SELECTION_PHASE = "selection_phase",
    CANDIDATE_PROPOSAL = "candidate_proposal",
    CONSOLIDATED = "consolidated",
    DONE = "done",
    ABORTED = "aborted"
}
export enum EmploymentState {
    EMPLOYED = "employed",
    AVAILABLE = "available",
    NOT_AVAILABLE = "not available"
}

export enum MachineStateType {
    RECEIVED = "received",
    READ = "read",
    DISCARDED = "discarded",
    PROCESSING = "processing",
    DONE = "done",
    FAILED = "failed"
}

// Interface for user
export interface UserInterface {
    name: string,
    email: string,
    loginUrl: string,
    logoutUrl: string,
    principal: any | null,
    xsrfToken: string
}

export interface ActionOnJobInterface {
    state: JobStatus;
    date: string;
    note?: string | null;
    jobOfferId?: number | null;
    professionalId?: number | null;
}

export interface ActionOnMessageInterface {
    state: MachineStateType;
    date?: string | null;
    comment?: string | null;
}

export interface AddressInterface {
    id?: number | null;
    address: string;
}

export interface EmailInterface {
    id?: number | null;
    mail: string;
}

export interface GeneralContactInterface {
    id?: number | null;
    name: string;
    surname: string;
    category: string;
    ssncode?: string | null;
    professionalInfo?: ProfessionalInterface | null;
}

export interface JobOfferInterface {
    id?: number | null;
    description: string;
    requiredSkills: string;
    duration: number;
    status: JobStatus;
    profitMargin: number;
    customer?: SpecificContactInterface | null;
    professionalId?: number | null;
}

export interface JobStatusInterface {
    status: string;
    note?: string | null;
    professionalId?: number | null;
}

export interface MessageInterface {
    id?: number | null;
    sender: string;
    date?: string | null;
    subject?: string | null;
    body?: string | null;
    channel: string;
    state?: string | null;
    priority?: number | null;
    relatedContacts?: GeneralContactInterface[];
}

export interface NewEmailInterface {
    recipient: string;
    subject: string;
    body: string;
}

export interface NoteInterface {
    note: string;
    date?: string | null;
}

export interface ProfessionalInterface {
    skills: string;
    employmentState: EmploymentState;
    location: string;
    dailyRate: number;
    linkedJobs?: JobOfferInterface[] | null;
}

export interface SpecificContactInterface {
    id: number | null;
    name: string;
    surname: string;
    category: string;
    ssncode?: string | null;
    mailList: EmailInterface[];
    addressList: AddressInterface[];
    telephoneList: TelephoneInterface[];
    professionalInfo?: ProfessionalInterface | null;
}

export interface StateWithMessageInterface {
    state: string;
    comment: string;
}

export interface TelephoneInterface {
    id?: number | null;
    number: string;
}

export interface ModalInterface {
    header: string;
    body: string;
    method: any;
}

export interface CustomerFormData {
    name: string;
    surname: string;
    category: string;
    ssncode: string;
}

export interface ProfessionalFormData {
    name: string;
    surname: string;
    category: string;
    ssncode: string;
    professionalInfo: {
        skills: string;
        employmentState: EmploymentState;
        location: string;
        dailyRate: number;
    };
}

export interface JobOfferFormData {
    description: string;
    requiredSkills: string;
    duration: number;
    profitMargin: number;
}

export interface JobStatusFormData {
    status: string;
    note: string;
    professionalId: string;
}

export interface CountInterface {
    period: number,
    count: number
}

export interface FileData {
    name: string;
    type: string;
    size: number;
    content: ArrayBuffer | null;
}