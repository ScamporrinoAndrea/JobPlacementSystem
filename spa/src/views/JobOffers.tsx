import { Container, Row, Col, Nav, Button } from "react-bootstrap"
import { fetchAllJobOffers } from "../API"
import { JobOfferInterface, UserInterface } from "../interfaces/interfaces"
import MyNavbar from "../components/MyNavbar"
import { useState, useEffect, SetStateAction } from 'react'
import SearchBar from "../components/SearchBar"
import JobOfferCard from "../components/JobOfferCard"
import Loading from "../components/Loading"
import { useNavigate } from "react-router-dom"


const JobOffers = ({ user, userRole, handleError, handleSuccess }: { user: UserInterface | null, userRole: any, handleError: any, handleSuccess: any }) => {
    const navigate = useNavigate();
    const [jobOffers, setJobOffers] = useState<JobOfferInterface[]>([]);
    const [search, setSearch] = useState('');
    const [rapidFilter, setRapidFilter] = useState('all');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setLoading(true);
        fetchAllJobOffers()
            .then(jobOffers => {
                setJobOffers(jobOffers)
            })
            .catch(() => {
                setJobOffers([]),
                    handleError('Error fetching job offers')
            })
            .finally(() => setLoading(false));
    }, []);

    function handleSearch(e: { target: { value: SetStateAction<string> } }) {
        setSearch(e.target.value);
    }

    function handleRapidFilters(filter: SetStateAction<string>) {
        setRapidFilter(filter);
    }

    function filterJobOffers(jobOffers: JobOfferInterface[], search: string, rapidFilter: string) {
        return jobOffers.filter((jobOffer: JobOfferInterface) => {
            // Filtro basato sul filtro rapido
            const matchesRapidFilter = rapidFilter === 'all' || rapidFilter === jobOffer.status;

            // Filtro basato sulla ricerca
            const matchesSearch = jobOffer.description.toLowerCase().includes(search.toLowerCase()) ||
                jobOffer.requiredSkills.toLowerCase().includes(search.toLowerCase()) ||
                jobOffer.customer?.name.toLowerCase().includes(search.toLowerCase()) ||
                jobOffer.id?.toString().includes(search) ||
                jobOffer.customer?.surname.toLowerCase().includes(search.toLowerCase());

            return matchesRapidFilter && matchesSearch;
        });
    }

    const filteredJobOffers = filterJobOffers(jobOffers, search, rapidFilter);

    return (
        <>
            <MyNavbar user={user} userRole={userRole} />

            <div style={{ position: 'sticky', top: 0, zIndex: 2, backgroundColor: 'white', boxShadow: '0 4px 2px -2px rgba(0, 0, 0, 0.2)' }}>
                <Container>
                    <SearchBar search={search} handleSearch={handleSearch} />
                    <Row style={{ marginTop: 25, paddingBottom: 10 }}>
                        <Col md='auto' style={{ paddingBottom: 10, overflowX: 'auto' }}>
                            <Nav variant='pills' activeKey={rapidFilter} style={{ display: 'flex', flexWrap: 'nowrap' }}>
                                <Nav.Item>
                                    <Nav.Link eventKey='all' className='buttons-rapid-filter' onClick={() => handleRapidFilters('all')}>
                                        All
                                    </Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey='created' className='buttons-rapid-filter' onClick={() => handleRapidFilters('created')}>
                                        Created
                                    </Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey='selection_phase' className='buttons-rapid-filter' style={{ minWidth: 150 }} onClick={() => handleRapidFilters('selection_phase')}>
                                        Selection phase
                                    </Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey='candidate_proposal' className='buttons-rapid-filter' style={{ minWidth: 176 }} onClick={() => handleRapidFilters('candidate_proposal')}>
                                        Candidate proposal
                                    </Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey='consolidated' className='buttons-rapid-filter' onClick={() => handleRapidFilters('consolidated')}>
                                        Consolidated
                                    </Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey='done' className='buttons-rapid-filter' onClick={() => handleRapidFilters('done')}>
                                        Done
                                    </Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey='aborted' className='buttons-rapid-filter' onClick={() => handleRapidFilters('aborted')}>
                                        Aborted
                                    </Nav.Link>
                                </Nav.Item>
                            </Nav>
                        </Col>
                    </Row>
                </Container>
            </div>
            <Container>
                {loading ? <Loading /> :
                    <Row style={{ marginBottom: 25 }}>
                        {filteredJobOffers.length > 0 ? (
                            filteredJobOffers.sort((a, b) => (b.id ?? 0) - (a.id ?? 0)).map((jobOffer: JobOfferInterface) => <JobOfferCard key={jobOffer.id} {...jobOffer} />)
                        ) : (
                            <div className='d-flex justify-content-center align-items-center' style={{ height: '50vh' }}>
                                <div className='text-center'>
                                    <h1>No Job offer has been made</h1>
                                </div>
                            </div>
                        )}
                    </Row>
                }
            </Container>
            {userRole == 'manager' || userRole == 'operator' ?
                <Button variant="primary" onClick={() => navigate("/ui/joboffers/create")} size="lg" style={{ position: 'fixed', bottom: 20, right: 20, zIndex: 2, borderRadius: 100 }}>
                    +
                </Button>
                : null
            }
        </>
    )
}

export default JobOffers