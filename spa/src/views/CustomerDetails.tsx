import { useNavigate, useParams } from "react-router-dom"
import { JobOfferInterface, SpecificContactInterface, UserInterface } from "../interfaces/interfaces"
import { useEffect, useState } from "react"
import { deleteCustomer, fetchAllJobOffers, fetchCustomer, } from "../API"
import { handleError } from "../utils/ToastHandlers"
import MyNavbar from "../components/MyNavbar"
import Loading from "../components/Loading"
import { Container, Row, Col, Card, Button, Offcanvas, Table } from "react-bootstrap"
import { LeftBarContactDetails } from "../components/LeftBarContactDetails"
import { statusColors } from "../utils/StatusColor"

const CustomerDetails = ({ user, userRole, setShowModal, setMsgModal }: { user: UserInterface | null, userRole: any, setShowModal: any, setMsgModal: any }) => {
    const { id } = useParams<{ id: string }>()
    const navigate = useNavigate()
    const [customer, setCustomer] = useState<SpecificContactInterface | null>(null)
    const [loading, setLoading] = useState(true)
    const [jobOffers, setJobOffers] = useState<JobOfferInterface[]>([])
    const [showDetails, setShowDetails] = useState(false);
    const handleClose = () => setShowDetails(false);
    const handleShow = () => setShowDetails(true);

    useEffect(() => {
        fetchCustomer(id || '')
            .then((customer) => {
                setCustomer(customer)
                fetchAllJobOffers()
                    .then((jobOffers) => {
                        setJobOffers(jobOffers.filter((jobOffer) => jobOffer.customer?.id === customer.id))
                    }).catch(() => {
                        handleError("Error fetching job offers")
                    })
            })
            .catch(() => {
                handleError("Customer not found")
            })
            .finally(() => {
                setLoading(false)
            })
    }, [id])

    function deleteCustomerMethod() {
        setShowModal(false);
        deleteCustomer(id?.toString() || '', user?.xsrfToken || '')
            .then(() => {
                navigate('/ui/customers')
            })
            .catch((error) => {
                handleError(error.message)
            })
    }

    function deleteCustomerF() {
        setShowModal(true);
        setMsgModal({
            header: 'Delete Customer',
            body: `Are you sure you want to delete this customer?`,
            method: () => deleteCustomerMethod(),
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
                                    <LeftBarContactDetails contact={customer} />
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
                                            <div style={{ fontWeight: 'bold', fontSize: 30 }}>{customer?.name} {customer?.surname}</div>
                                        </Col>
                                        {userRole == 'manager' || userRole == 'operator' ?
                                            <Col xs={4} md={4} style={{ textAlign: 'center', float: 'right' }}>
                                                <Button variant='primary' onClick={() => navigate('/ui/customers/create/' + customer?.id)} style={{ marginRight: 10 }}>
                                                    <i className='bi bi-pencil'></i>
                                                </Button>
                                                <Button variant='danger' onClick={deleteCustomerF}>
                                                    <i className='bi bi-trash3'></i>
                                                </Button>
                                            </Col>
                                            : null
                                        }
                                    </Row>
                                    <Row style={{ marginTop: 32 }}>
                                        <Col>
                                            <div style={{ fontWeight: 'bold', fontSize: 20 }}> List of Job offer: </div>
                                        </Col>
                                    </Row>
                                    <Row style={{ marginTop: 18 }}>
                                        {jobOffers.length > 0 ? (
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
                                                    {jobOffers.map((jobOffer) => (
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
                                            <div>No Job offer for this customer</div>
                                        )}
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
                            <LeftBarContactDetails contact={customer} />
                        </Offcanvas.Body>
                    </Offcanvas>
                </>
            }
        </>
    )
}

export default CustomerDetails