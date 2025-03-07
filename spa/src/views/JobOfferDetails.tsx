import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { deleteDocument, fetchAllProfessional, fetchJobDocuments, fetchJobOffer, fetchJobOfferHistory, fetchMetadataDocumentJob, insertNewDocument, updateJobOfferStatus } from "../API";
import MyNavbar from "../components/MyNavbar"
import { ActionOnJobInterface, FileData, JobOfferInterface, JobStatusFormData, SpecificContactInterface, UserInterface } from "../interfaces/interfaces"
import Loading from "../components/Loading";
import { Container, Row, Col, Card, Button, Table, Offcanvas, Modal, Form } from "react-bootstrap";
import { employmentStateColor, statusColors } from "../utils/StatusColor";
import LeftBarJobOfferDetails from "../components/LeftBarJobOfferDetails";
import { Color } from "../constants/colors";
import randomcolor from 'randomcolor';

const JobOfferDetails = ({ user, userRole, handleError, handleSuccess, setShowModal, setMsgModal }: { user: UserInterface | null, userRole: any, handleError: any, handleSuccess: any, setShowModal: any, setMsgModal: any }) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [jobOffer, setJobOffer] = useState<JobOfferInterface | null>(null);
    const [jobOfferHistory, setJobOfferHistory] = useState<ActionOnJobInterface[]>([]);
    const [loading, setLoading] = useState(true);
    const { backgroundColor = '', color = '', icon = '', text = '' } = jobOffer ? statusColors[jobOffer.status] || {} : {};
    const [showDetails, setShowDetails] = useState(false);
    const [updateStatusModal, setUpdateStatusModal] = useState(false);
    const [nextStatus, setNextStatus] = useState('');
    const [note, setNote] = useState('');
    const [dirty, setDirty] = useState(true);
    const [professionalId, setProfessionalId] = useState('');
    const [professionals, setProfessionals] = useState<SpecificContactInterface[]>([]);
    const [validated, setValidated] = useState(false);
    const [bestCandidates, setBestCandidates] = useState<MatchingContact[] | null>(null);
    const [fileData, setFileData] = useState<FileData>({
        name: '',
        type: '',
        size: 0,
        content: null,
    });
    const [documentUrl, setDocumentUrl] = useState<string | null>(null)
    const [documentType, setDocumentType] = useState<string | null>(null)
    const [documentMetadata, setDocumentMetadata] = useState<string>('')


    const handleClose = () => setShowDetails(false);
    const handleShow = () => setShowDetails(true);


    useEffect(() => {
        if (dirty) {
            fetchJobOffer(id || '')
                .then(jobOffer => {
                    setJobOffer(jobOffer);
                    fetchJobOfferHistory(jobOffer.id?.toString() || '')
                        .then(history => {
                            setJobOfferHistory(history)
                            fetchAllProfessional().
                                then(professionals => {
                                    setProfessionals(professionals)
                                    setBestCandidates(findMatchingContacts(jobOffer, professionals) || null)
                                    fetchJobDocuments(id?.toString() || "")
                                        .then((documents) => {
                                            setDocumentUrl(documents.url)
                                            setDocumentType(documents.content)
                                            fetchMetadataDocumentJob(id?.toString() || "")
                                                .then((id) => {
                                                    setDocumentMetadata(id)
                                                    setLoading(false)
                                                })
                                                .catch(() => {
                                                    handleError("documents not found")
                                                })
                                        })
                                        .catch(() => {
                                            handleError("documents not found")
                                        })
                                }
                                ).catch(() => {
                                    setProfessionals([]);
                                    handleError('Error fetching professionals')
                                })
                        })
                        .catch(() => {
                            setJobOfferHistory([]);
                            handleError('Error fetching job offer history')
                        })
                })
                .catch(() => {
                    setJobOffer(null);
                    handleError('Error fetching job offer')
                })
                .finally(() => {
                    setLoading(false)
                    setDirty(false)
                })
        }
    }, [id, dirty]);

    interface MatchingContact {
        contact: SpecificContactInterface;
        matchCount: number;
    }

    function findMatchingContacts(job: JobOfferInterface, contacts: SpecificContactInterface[]): MatchingContact[] {
        const requiredSkills = job.requiredSkills.replace(/\s+/g, '').split(',').map(skill => skill.trim().toLowerCase());

        const contactsWithMatchCount = contacts.map(contact => {
            const contactSkills = contact.professionalInfo?.skills.replace(/\s+/g, '').split(',').map(skill => skill.trim().toLowerCase()) || [];

            const matchCount = contactSkills.filter(skill => requiredSkills.includes(skill)).length;

            return {
                contact,
                matchCount
            };
        });

        const matchingContacts = contactsWithMatchCount.filter(item => item.matchCount > 0);

        matchingContacts.sort((a, b) => b.matchCount - a.matchCount);

        return matchingContacts;
    }

    function handleChangeDocument(e: React.ChangeEvent<HTMLInputElement>) {
        const file = e.target.files?.[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setFileData({
                    name: file.name,
                    type: file.type,
                    size: file.size,
                    content: reader.result as ArrayBuffer,
                });
            };
            reader.readAsArrayBuffer(file);
        }
    }



    function updateStatus(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        const form = e.currentTarget;

        if (form.checkValidity() === false) {
            e.stopPropagation();
        } else {
            const statusForm: JobStatusFormData = {
                status: nextStatus,
                note: note,
                professionalId: professionalId
            };
            updateJobOfferStatus(jobOffer?.id?.toString() || '', statusForm, user?.xsrfToken || '')
                .then(() => {
                    if (documentUrl && fileData.name) {
                        deleteDocument(documentMetadata || "", user?.xsrfToken || '')
                            .then(() => {
                                if (fileData.name) {
                                    insertNewDocument(fileData, jobOffer?.id?.toString() || '', null, user?.xsrfToken || '')
                                        .then(() => setDirty(true))
                                        .catch(() => handleError('Error inserting document'));
                                }
                            })
                            .catch(() => {
                                handleError("documents not found")
                            })
                            .finally(() => setLoading(true))
                    } else if (fileData.name) {
                        insertNewDocument(fileData, jobOffer?.id?.toString() || '', null, user?.xsrfToken || '')
                            .then(() => setDirty(true))
                            .catch(() => handleError('Error inserting document'));
                    }
                    setDirty(true);
                    setUpdateStatusModal(false);
                    handleSuccess('Job offer status updated');
                    setNote('');
                    setProfessionalId('');
                    setFileData({
                        name: '',
                        type: '',
                        size: 0,
                        content: null,
                    });
                    setValidated(false);
                }).catch(() => {
                    handleError('Error updating job offer status');
                });
        }
        setValidated(true);
    }

    const handleChangeProfessional = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { value } = e.target;
        setProfessionalId(value);
    };

    function deleteDocumentMethod() {
        setShowModal(false);
        deleteDocument(documentMetadata || "", user?.xsrfToken || '')
            .then(() => setDirty(true))
            .catch(() => {
                handleError("documents not found")
            })
    }

    function deleteDocumentF() {
        setShowModal(true);
        setMsgModal({
            header: 'Delete Document',
            body: `Are you sure you want to delete this document?`,
            method: () => deleteDocumentMethod(),
        });
    }

    return (
        <>
            <MyNavbar user={user} userRole={userRole} />
            {loading || !jobOffer ? <Loading /> :
                <>
                    <Container style={{ marginTop: 25, marginBottom: 25 }}>
                        <Row style={{ display: 'flex', alignItems: 'start' }}>
                            <Col md={4} style={{ position: 'sticky', top: 25 }}>
                                <Card style={{ padding: 20 }} className='custom-card d-none d-md-flex'>
                                    <LeftBarJobOfferDetails jobOffer={jobOffer} handleError={handleError} />
                                </Card>
                            </Col>
                            <Col md={8} sm={12}>
                                <Card style={{ padding: 20 }} className='custom-card'>
                                    <Row style={{ display: 'flex', alignItems: 'center' }}>
                                        <Col xs={2} className='d-md-none' style={{ textAlign: 'center', float: 'right' }}>
                                            <Button variant='primary' onClick={handleShow}>
                                                <i className="bi bi-list"></i>
                                            </Button>
                                        </Col>
                                        <Col xs={6} md={7}>
                                            <div style={{ fontWeight: 'bold', fontSize: 30 }}>Job offer n. {jobOffer.id}</div>
                                        </Col>
                                        <Col xs={4} md={5} style={{ textAlign: 'center', float: 'right' }}>
                                            <Button onClick={() => navigate('/ui/joboffers/create/' + jobOffer.id)} style={{ backgroundColor: Color.secondary, borderColor: Color.secondary, float: "right" }} >
                                                <i className='bi bi-pencil'></i> Edit job offer
                                            </Button>
                                        </Col>
                                    </Row>
                                    <Row style={{ marginTop: 45 }}>
                                        <Col lg={2} md={3}>
                                            <div style={{ fontWeight: 'bold', fontSize: 20 }}> Status: </div>
                                        </Col>
                                        <Col lg={3} md={4}>
                                            <div style={{ display: 'flex', alignItems: 'center' }}>
                                                <div style={{ display: 'flex', alignItems: 'center', marginRight: 5 }}>
                                                    <span
                                                        className='badge'
                                                        style={{
                                                            backgroundColor,
                                                            color,
                                                            padding: '1em 1em',
                                                            borderRadius: '0.25rem',
                                                        }}
                                                    >
                                                        <i className={icon} style={{ fontSize: '16px' }}></i>
                                                    </span>
                                                </div>
                                                <div style={{ display: 'flex', alignItems: 'center', marginLeft: 5, whiteSpace: 'nowrap' }}>
                                                    <span style={{ color: 'rgba(0, 0, 0, 0.5)' }}>{text}</span>
                                                </div>
                                            </div>
                                        </Col>
                                    </Row>
                                    <Row style={{ marginTop: 32 }}>
                                        <Col>
                                            <div style={{ fontWeight: 'bold', fontSize: 20 }}> Description: </div>
                                        </Col>
                                    </Row>
                                    <Row style={{ marginTop: 18 }}>
                                        <Col>
                                            {jobOffer.description}
                                        </Col>
                                    </Row>
                                    {jobOffer.status == 'selection_phase' || jobOffer.status == 'candidate_proposal' ?
                                        <>
                                            <Row style={{ marginTop: 32 }}>
                                                <Col>
                                                    <div style={{ fontWeight: 'bold', fontSize: 20 }}> Best candidate: </div>
                                                </Col>
                                            </Row>
                                            <Row style={{ marginTop: 18 }}>
                                                <Col>
                                                    {bestCandidates && bestCandidates.length > 0 ?
                                                        <Table responsive hover>
                                                            <thead>
                                                                <tr>
                                                                    <th style={{ fontWeight: 600 }}>Candidate name</th>
                                                                    <th style={{ fontWeight: 600 }}>Skills</th>
                                                                    <th style={{ fontWeight: 600 }}>N. of skills matched</th>
                                                                    <th style={{ fontWeight: 600 }}>Status of candidate</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody style={{ cursor: "pointer" }}>
                                                                {bestCandidates.map((candidate, index) => (
                                                                    <tr key={index} onClick={() => navigate('/ui/professionals/' + candidate.contact.id)}>
                                                                        <td>{candidate.contact.name} {candidate.contact.surname}</td>
                                                                        <td>
                                                                            <div
                                                                                className='hide-scrollbar'
                                                                                style={{
                                                                                    fontWeight: 'semi-bold',
                                                                                    fontSize: 14,
                                                                                    marginTop: 5,
                                                                                    overflowX: 'auto',
                                                                                    whiteSpace: 'nowrap',
                                                                                    scrollbarWidth: 'none',
                                                                                    msOverflowStyle: 'none'
                                                                                }}
                                                                            >
                                                                                {candidate.contact.professionalInfo?.skills.split(',').map((skill) => (
                                                                                    <span
                                                                                        key={skill}
                                                                                        className='badge'
                                                                                        style={{
                                                                                            backgroundColor: randomcolor({ seed: skill.replace(/\s+/g, '').toLowerCase(), luminosity: 'bright', format: 'rgba', alpha: 1 }).replace(/1(?=\))/, '0.1'),
                                                                                            color: randomcolor({ seed: skill.replace(/\s+/g, '').toLowerCase(), luminosity: 'bright', format: 'rgba', alpha: 1 }),
                                                                                            padding: '0.5em 1.2em',
                                                                                            borderRadius: '0.25rem',
                                                                                            marginRight: 10,
                                                                                        }}
                                                                                    >
                                                                                        {skill}
                                                                                    </span>
                                                                                ))}
                                                                            </div>
                                                                        </td>
                                                                        <td>{candidate.matchCount}</td>
                                                                        <td>
                                                                            {candidate.contact.professionalInfo?.employmentState &&
                                                                                <div className="badge" style={{ backgroundColor: employmentStateColor[candidate.contact.professionalInfo?.employmentState].backgroundColor, color: employmentStateColor[candidate.contact.professionalInfo?.employmentState].color, borderRadius: 20, }}>
                                                                                    {employmentStateColor[candidate.contact.professionalInfo?.employmentState].text}
                                                                                </div>
                                                                            }
                                                                        </td>
                                                                    </tr>
                                                                ))}
                                                            </tbody>
                                                        </Table>
                                                        :
                                                        <div>
                                                            No best candidate found
                                                        </div>
                                                    }
                                                </Col>
                                            </Row>
                                        </>
                                        : null
                                    }
                                    <Row style={{ marginTop: 32 }}>
                                        <Col>
                                            <div style={{ fontWeight: 'bold', fontSize: 20 }}> Contract: </div>
                                        </Col>
                                        {documentUrl == null || documentType == null ? null : (
                                            <Col >
                                                <Button variant='danger' onClick={deleteDocumentF} style={{ float: "right" }}>
                                                    <i className='bi bi-trash3'></i>
                                                </Button>
                                            </Col>
                                        )}
                                    </Row>
                                    <Row style={{ marginTop: 18 }}>
                                        {documentUrl == null || documentType == null ? (
                                            <p>No contract uploaded yet</p>
                                        ) : (
                                            <embed src={documentUrl} type={documentType} width="100%" height="600px" />)}
                                    </Row>
                                    <Row style={{ marginTop: 32 }}>
                                        <Col>
                                            <div style={{ fontWeight: 'bold', fontSize: 20 }}> History: </div>
                                        </Col>
                                    </Row>
                                    <Row style={{ marginTop: 18 }}>
                                        <Col>
                                            <Container>
                                                <Row className="align-items-start mb-3" style={{ position: 'relative' }}>
                                                    <Col xs={1} className="d-flex flex-column align-items-center position-relative">
                                                        <div className="timeline-circle"></div>
                                                        {jobOfferHistory.length != 0 && <div className="timeline-line very-short"></div>}
                                                    </Col>
                                                    <Col xs={11}>
                                                        <div><strong>State:</strong> CREATED</div>
                                                    </Col>
                                                </Row>
                                                {jobOfferHistory.map((event, index) => (
                                                    <Row key={index} className="align-items-start mb-3" style={{ position: 'relative' }}>
                                                        <Col xs={1} className="d-flex flex-column align-items-center position-relative">
                                                            <div className="timeline-circle"></div>
                                                            {index < jobOfferHistory.length - 1 && !event.note && !event.professionalId && <div className="timeline-line short"></div>}
                                                            {index < jobOfferHistory.length - 1 && event.note && !event.professionalId && <div className="timeline-line medium"></div>}
                                                            {index < jobOfferHistory.length - 1 && !event.note && event.professionalId && <div className="timeline-line medium"></div>}
                                                            {index < jobOfferHistory.length - 1 && event.note && event.professionalId && <div className="timeline-line long"></div>}
                                                        </Col>
                                                        <Col xs={11}>
                                                            <div><strong>State:</strong> {event.state.replace(/_/g, ' ').toUpperCase()}</div>
                                                            <div><strong>Date:</strong> {new Date(event.date).toLocaleString()}</div>
                                                            {event.note && <div className="oneLineText"><strong>Note:</strong> {event.note}</div>}
                                                            {event.professionalId && <div><strong>Professional: </strong>
                                                                {professionals.find((professional) =>
                                                                    professional.professionalInfo?.linkedJobs?.find((job) => job.id == jobOffer.id))?.name}
                                                                {' '}
                                                                {professionals.find((professional) =>
                                                                    professional.professionalInfo?.linkedJobs?.find((job) => job.id == jobOffer.id))?.surname}
                                                            </div>}
                                                        </Col>
                                                    </Row>
                                                ))}
                                            </Container>
                                        </Col>
                                    </Row>
                                    {userRole == 'manager' || userRole == "guest" ?
                                        <Row>
                                            {jobOffer.status != 'aborted' && jobOffer.status != 'done' &&
                                                <Col style={{ textAlign: 'center', marginTop: 25 }}>
                                                    <Button variant='outline-danger' onClick={() => {
                                                        setUpdateStatusModal(true);
                                                        setNextStatus('aborted');
                                                    }}>
                                                        <i className={statusColors["aborted"].icon}></i> Set aborted
                                                    </Button>
                                                </Col>
                                            }
                                            {jobOffer.status != 'aborted' && jobOffer.status != 'selection_phase' && jobOffer.status != 'done' &&
                                                <Col style={{ textAlign: 'center', marginTop: 25 }}>
                                                    <Button variant='outline-primary' onClick={() => {
                                                        setUpdateStatusModal(true);
                                                        setNextStatus('selection_phase');
                                                    }}>
                                                        <i className={statusColors["selection_phase"].icon}></i> Set selection phase
                                                    </Button>
                                                </Col>
                                            }
                                            {jobOffer.status == 'selection_phase' &&
                                                <Col style={{ textAlign: 'center', marginTop: 25 }}>
                                                    <Button variant='outline-primary' onClick={() => {
                                                        setUpdateStatusModal(true);
                                                        setNextStatus('candidate_proposal');
                                                    }}>
                                                        <i className={statusColors["candidate_proposal"].icon}></i> Set candidate proposal
                                                    </Button>
                                                </Col>
                                            }
                                            {jobOffer.status == 'candidate_proposal' &&
                                                <Col style={{ textAlign: 'center', marginTop: 25 }}>
                                                    <Button variant='outline-primary' onClick={() => {
                                                        setUpdateStatusModal(true);
                                                        setNextStatus('consolidated');
                                                    }}>
                                                        <i className={statusColors["consolidated"].icon}></i> Set consolidated
                                                    </Button>
                                                </Col>
                                            }
                                            {jobOffer.status == 'consolidated' &&
                                                <Col style={{ textAlign: 'center', marginTop: 25 }}>
                                                    <Button variant='outline-primary' onClick={() => {
                                                        setUpdateStatusModal(true);
                                                        setNextStatus('done');
                                                    }}>
                                                        <i className={statusColors["done"].icon}></i> Set done
                                                    </Button>
                                                </Col>
                                            }
                                        </Row>
                                        : null
                                    }
                                </Card>
                            </Col>
                        </Row>
                    </Container>
                    <Offcanvas show={showDetails} onHide={handleClose}>
                        <Offcanvas.Header closeButton>
                            <Offcanvas.Title>Details</Offcanvas.Title>
                        </Offcanvas.Header>
                        <Offcanvas.Body>
                            <LeftBarJobOfferDetails jobOffer={jobOffer} handleError={handleError} />
                        </Offcanvas.Body>
                    </Offcanvas>
                    <Modal show={updateStatusModal} onHide={() => setUpdateStatusModal(false)} centered>
                        <Modal.Header closeButton>
                            <Modal.Title>Set {nextStatus.replace(/_/g, ' ')}</Modal.Title>
                        </Modal.Header>
                        <Form noValidate validated={validated} onSubmit={updateStatus}>
                            <Modal.Body>
                                Are you sure you want to set the job offer {nextStatus.replace(/_/g, ' ')}?
                                <br />
                                <br />

                                <Form.Group controlId="note">
                                    <Form.Label>Add some Notes (optional)</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        name="description"
                                        value={note}
                                        onChange={(e) => setNote(e.target.value)}
                                        rows={3}
                                        placeholder="Enter some notes"
                                    />
                                </Form.Group>
                                {nextStatus == 'consolidated' &&
                                    <>
                                        <Form.Group controlId="professionalId" className="mt-3">
                                            <Form.Label>Select a professional</Form.Label>
                                            <Form.Select
                                                name="professionalId"
                                                value={professionalId}
                                                onChange={handleChangeProfessional}
                                                required
                                                isInvalid={validated && !professionalId}
                                            >
                                                <option value="">Select a Professional</option>
                                                {professionals.map(professional => (
                                                    <option key={professional.id} value={professional.id?.toString()}>
                                                        {professional.name} {professional.surname} - {professional.professionalInfo?.employmentState.replace(/_/g, ' ')}
                                                    </option>
                                                ))}
                                            </Form.Select>
                                            <Form.Control.Feedback type="invalid">
                                                Please select a professional.
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                        <Form.Group controlId='formFile' className='mt-3'>
                                            <Form.Label>Upload a document</Form.Label>
                                            <Form.Control type='file' onChange={handleChangeDocument} />
                                        </Form.Group>
                                    </>
                                }

                            </Modal.Body>
                            <Modal.Footer>
                                <Button
                                    variant='outline-secondary'
                                    onClick={() => {
                                        setUpdateStatusModal(false);
                                    }}
                                >
                                    No
                                </Button>
                                <Button variant='outline-primary' type="submit">
                                    Yes
                                </Button>
                            </Modal.Footer>
                        </Form>
                    </Modal>
                </>
            }
        </>
    )
}

export default JobOfferDetails

