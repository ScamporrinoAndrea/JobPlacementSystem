import { useNavigate, useParams } from "react-router-dom"
import { SpecificContactInterface, UserInterface } from "../interfaces/interfaces"
import { useEffect, useState } from "react"
import { deleteDocument, deleteProfessional, fetchContactDocuments, fetchMetadataDocument, fetchProfessional, } from "../API"
import { handleError } from "../utils/ToastHandlers"
import MyNavbar from "../components/MyNavbar"
import Loading from "../components/Loading"
import { Container, Row, Col, Card, Button, Offcanvas, Table } from "react-bootstrap"
import { LeftBarContactDetails } from "../components/LeftBarContactDetails"
import { statusColors } from "../utils/StatusColor"
import { employmentStateColor } from "../utils/StatusColor"

const ProfessionalDetails = ({ user, userRole, setShowModal, setMsgModal }: { user: UserInterface | null, userRole: any, setShowModal: any, setMsgModal: any }) => {
    const { id } = useParams<{ id: string }>()
    const navigate = useNavigate()
    const [professional, setProfessional] = useState<SpecificContactInterface | null>(null)
    const [documentUrl, setDocumentUrl] = useState<string | null>(null)
    const [documentType, setDocumentType] = useState<string | null>(null)
    const [documentMetadata, setDocumentMetadata] = useState<string>('')
    const [loading, setLoading] = useState(true)
    const [showDetails, setShowDetails] = useState(false);
    const handleClose = () => setShowDetails(false);
    const handleShow = () => setShowDetails(true);
    const { backgroundColor = '', color = '', icon = '', text = '' } = professional?.professionalInfo ? employmentStateColor[professional.professionalInfo.employmentState] : {};


    useEffect(() => {
        fetchProfessional(id || '')
            .then((professional) => {
                setProfessional(professional)
                fetchContactDocuments(id?.toString() || "")
                    .then((documents) => {
                        setDocumentUrl(documents.url)
                        setDocumentType(documents.content)
                        fetchMetadataDocument(id?.toString() || "")
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
            })
            .catch(() => {
                handleError("professional not found")
            })

    }, [id, loading])

    function deleteProfessionalMethod() {
        setShowModal(false);
        deleteProfessional(id?.toString() || '', user?.xsrfToken || '')
            .then(() => {
                navigate('/ui/professionals')
            })
            .catch((error) => {
                handleError(error.message)
            })
    }

    function deleteProfessionalF() {
        setShowModal(true);
        setMsgModal({
            header: 'Delete Professional',
            body: `Are you sure you want to delete this professional?`,
            method: () => deleteProfessionalMethod(),
        });
    }

    function deleteDocumentMethod() {
        setShowModal(false);
        deleteDocument(documentMetadata || "", user?.xsrfToken || '')
            .catch(() => {
                handleError("documents not found")
            })
            .finally(() => setLoading(true))
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
            {loading ?
                <Loading /> :
                <>
                    <Container style={{ marginTop: 25, marginBottom: 25 }}>
                        <Row style={{ display: 'flex', alignItems: 'start' }}>
                            <Col md={4} style={{ position: 'sticky', top: 25 }}>
                                <Card style={{ padding: 20 }} className='custom-card d-none d-md-flex'>
                                    <LeftBarContactDetails contact={professional} />
                                </Card>
                            </Col>
                            <Col md={8}>
                                <Card style={{ padding: 20 }} className='custom-card'>
                                    <Row style={{ display: 'flex', alignItems: 'center' }}>
                                        <Col xs={2} className='d-md-none' style={{ textAlign: 'center', float: 'right' }}>
                                            <Button variant='primary' onClick={handleShow}>
                                                <i className="bi bi-list"></i>
                                            </Button>
                                        </Col>
                                        <Col xs={6} md={8}>
                                            <div style={{ fontWeight: 'bold', fontSize: 30 }}>{professional?.name} {professional?.surname}</div>
                                        </Col>
                                        <Col xs={4} md={4} style={{ textAlign: 'center', float: 'right' }}>
                                            <Button variant='primary' onClick={() => navigate('/ui/professionals/create/' + professional?.id)} style={{ marginRight: 10 }}>
                                                <i className='bi bi-pencil'></i>
                                            </Button>
                                            <Button variant='danger' onClick={deleteProfessionalF}>
                                                <i className='bi bi-trash3'></i>
                                            </Button>
                                        </Col>
                                    </Row>
                                    <Row style={{ marginTop: 32 }}>
                                        <Col>
                                            <div style={{ fontWeight: 'bold', fontSize: 20 }}> Status: </div>
                                        </Col>
                                    </Row>
                                    <Col>
                                        <div style={{ display: 'flex', alignItems: 'center', marginTop: 20 }}>
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
                                    <Row style={{ marginTop: 32 }}>
                                        <Col>
                                            <div style={{ fontWeight: 'bold', fontSize: 20 }}> History of job: </div>
                                        </Col>
                                    </Row>
                                    <Row style={{ marginTop: 18 }}>
                                        {professional?.professionalInfo?.linkedJobs?.length && professional?.professionalInfo?.linkedJobs?.length > 0 ? (
                                            <Table responsive hover>
                                                <thead>
                                                    <tr>
                                                        <th style={{ fontWeight: 600 }}>ID</th>
                                                        <th style={{ fontWeight: 600 }}>Description</th>
                                                        <th style={{ fontWeight: 600 }}>Duration</th>
                                                        <th style={{ fontWeight: 600 }}>Status</th>
                                                    </tr>
                                                </thead>
                                                <tbody style={{ cursor: "pointer" }}>
                                                    {professional?.professionalInfo?.linkedJobs.map((jobOffer) => (
                                                        <tr key={jobOffer.id} onClick={() => navigate('/ui/joboffers/' + jobOffer.id)}>
                                                            <td>{jobOffer.id}</td>
                                                            <td>{jobOffer.description}</td>
                                                            <td>{jobOffer.duration}</td>
                                                            <td>
                                                                <div className="badge" style={{ backgroundColor: statusColors[jobOffer.status].backgroundColor, color: statusColors[jobOffer.status].color, borderRadius: 20, }}>
                                                                    {statusColors[jobOffer.status].text}
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    ))}
                                                </tbody>
                                            </Table>
                                        ) : (
                                            <div>No Job offer for this professional</div>
                                        )}
                                        <Row style={{ marginTop: 32 }}>
                                            <Col>
                                                <div style={{ fontWeight: 'bold', fontSize: 20 }}> Curriculum Vitae: </div>
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
                                                <p>No cv uploaded</p>
                                            ) : (
                                                <embed src={documentUrl} type={documentType} width="100%" height="600px" />)}
                                        </Row>
                                    </Row>
                                </Card>
                            </Col>
                        </Row>
                    </Container>

                    <Offcanvas show={showDetails} onHide={handleClose}>
                        <Offcanvas.Header closeButton>
                            <Offcanvas.Title>Details</Offcanvas.Title>
                        </Offcanvas.Header>
                        <Offcanvas.Body>
                            <LeftBarContactDetails contact={professional} />
                        </Offcanvas.Body>
                    </Offcanvas>
                </>
            }
        </>
    )
}

export default ProfessionalDetails