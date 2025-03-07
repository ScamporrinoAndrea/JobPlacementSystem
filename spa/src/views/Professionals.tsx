import { useState, useEffect, SetStateAction } from "react";
import { fetchAllProfessional } from "../API";
import MyNavbar from "../components/MyNavbar"
import { SpecificContactInterface, UserInterface } from "../interfaces/interfaces"
import { Button, Col, Container, Nav, Row } from "react-bootstrap";
import SearchBar from "../components/SearchBar";
import ProfessionalCard from "../components/ProfessionalCard";
import Loading from "../components/Loading";
import { useNavigate } from "react-router-dom";


const Professionals = ({ user, userRole, handleError, handleSuccess }: { user: UserInterface | null, userRole: any, handleError: any, handleSuccess: any }) => {
    const navigate = useNavigate();
    const [professionals, setProfessionals] = useState<SpecificContactInterface[]>([]);
    const [search, setSearch] = useState('');
    const [rapidFilter, setRapidFilter] = useState('all');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setLoading(true);
        fetchAllProfessional()
            .then(professionals => {
                setProfessionals(professionals)
            })
            .catch(() => { setProfessionals([]); handleError('Error fetching professionals') })
            .finally(() => setLoading(false));
    }, []);

    function handleRapidFilters(filter: SetStateAction<string>) {
        setRapidFilter(filter);
    }

    function handleSearch(e: { target: { value: SetStateAction<string>; }; }) {
        setSearch(e.target.value);
    }

    function filterProfessional(professionals: SpecificContactInterface[], search: string) {
        return professionals.filter((professionals: SpecificContactInterface) => {

            const matchesRapidFilter = rapidFilter === 'all' || rapidFilter === professionals.professionalInfo?.employmentState;

            const matchesSearch = professionals.name.toLowerCase().includes(search.toLowerCase()) ||
                professionals.surname.toLowerCase().includes(search.toLowerCase()) ||
                professionals.ssncode?.toLowerCase().includes(search.toLowerCase()) ||
                professionals.professionalInfo?.skills.toLowerCase().includes(search.toLowerCase()) ||
                professionals.addressList.join().toLowerCase().includes(search.toLowerCase()) ||
                professionals.mailList.join().toLowerCase().includes(search.toLowerCase()) ||
                professionals.telephoneList.join().toLowerCase().includes(search.toLowerCase()) ||
                professionals.professionalInfo?.location.toLowerCase().includes(search.toLowerCase())
                ;

            return matchesRapidFilter && matchesSearch;
        });
    }

    const filteredprofessional = filterProfessional(professionals, search);

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
                                    <Nav.Link eventKey='available' className='buttons-rapid-filter' onClick={() => handleRapidFilters('available')}>
                                        Available
                                    </Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey='not available' className='buttons-rapid-filter' onClick={() => handleRapidFilters('not available')}>
                                        Not available
                                    </Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey='employed' className='buttons-rapid-filter' onClick={() => handleRapidFilters('employed')}>
                                        Employed
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
                        {filteredprofessional.length > 0 ? (
                            filteredprofessional.map((professionals: SpecificContactInterface) => <ProfessionalCard key={professionals.id} {...professionals} />)
                        ) : (
                            <div className='d-flex justify-content-center align-items-center' style={{ height: '50vh' }}>
                                <div className='text-center'>
                                    <h1>No Professionals</h1>
                                </div>
                            </div>
                        )}
                    </Row>
                }
            </Container>
            <Button variant="primary" onClick={() => navigate("/ui/professionals/create")} size="lg" style={{ position: 'fixed', bottom: 20, right: 20, zIndex: 2, borderRadius: 100 }}>
                +
            </Button>
        </>
    )
}

export default Professionals