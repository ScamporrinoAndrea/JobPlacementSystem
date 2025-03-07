import { Navbar, Nav, Image, Dropdown, Container } from 'react-bootstrap';
import { Color } from '../constants/colors.js';
import { useNavigate, useLocation } from 'react-router-dom';
import { UserInterface } from '../interfaces/interfaces.js';
import logo from '../assets/logoWhiteJobOffer.png';
import userProfilePicture from '../assets/userProfilePicture.png';

function MyNavbar({ user, userRole }: { user: UserInterface | null, userRole: any }) {
    const navigate = useNavigate();
    let location = useLocation();

    function navElement() {
        return (
            <Nav activeKey={location.pathname}>
                {userRole == "manager" ?
                    <Nav.Link eventKey='/ui' onClick={() => navigate('/ui')}>
                        Dashboard
                    </Nav.Link> : null
                }
                {userRole == "guest" || userRole == "operator" || userRole == "manager" ?
                    <>
                        <Nav.Link eventKey={userRole == "manager" ? '/ui/joboffers' : '/ui'} onClick={() => navigate(userRole == "manager" ? '/ui/joboffers' : '/ui')}>
                            Job offers
                        </Nav.Link>
                        <Nav.Link eventKey='/ui/customers' onClick={() => navigate('/ui/customers')}>
                            Customers
                        </Nav.Link>
                        <Nav.Link eventKey='/ui/professionals' onClick={() => navigate('/ui/professionals')}>
                            Professionals
                        </Nav.Link>
                        <Nav.Link className='d-md-none' eventKey='/logout' href={user?.logoutUrl}>
                            Logout
                        </Nav.Link>
                    </> : null
                }
            </Nav>
        );
    }

    return (
        <>
            <Navbar collapseOnSelect expand='md' style={{ backgroundColor: Color.primary }} variant='dark'>
                <Container fluid>
                    <Navbar.Brand style={{ paddingLeft: '20px', paddingRight: 30 }} onClick={() => navigate('/ui')}>
                        <img src={logo} alt='Logo' style={{ height: 30 }} />
                        {'  '}
                        Job Finder
                    </Navbar.Brand>

                    <Navbar.Collapse className='d-none d-md-flex'>{navElement()}</Navbar.Collapse>
                    <Nav className='d-flex flex-row'>
                        {userRole ?
                            <Dropdown align='end' className='d-none d-md-flex'>
                                <Dropdown.Toggle variant='primary' id='dropdown-custom'>
                                    <Container className='d-flex justify-content-between align-items-center'>
                                        <div style={{ marginRight: 15 }}>
                                            <div style={{ fontSize: 15 }}>{user?.name}</div>
                                            <div style={{ color: 'rgba(255,255,255,0.5)', float: 'right', fontSize: 12 }}>{userRole == "guest" ? "recruiter" : userRole}</div>
                                        </div>
                                        <div className='text-center'>
                                            <Image style={{ height: 33, width: 33 }} src={userProfilePicture} roundedCircle />
                                        </div>
                                    </Container>
                                </Dropdown.Toggle>
                                <Dropdown.Menu>
                                    <Dropdown.Item>
                                        <i className='bi bi-gear'></i>
                                        <span style={{ marginLeft: 15 }}>Settings</span>
                                    </Dropdown.Item>
                                    <Dropdown.Divider />
                                    <Dropdown.Item href={user?.logoutUrl} style={{ color: 'red' }}>
                                        <i className='bi bi-box-arrow-right'></i>
                                        <span style={{ marginLeft: 15 }}>Logout</span>
                                    </Dropdown.Item>
                                </Dropdown.Menu>
                            </Dropdown>
                            : null
                        }
                        <Navbar.Toggle aria-controls='responsive-navbar-nav' />
                    </Nav>
                </Container>
                <Container className='d-md-none' style={{ display: 'content', textAlign: 'center' }}>
                    <Navbar.Collapse id='responsive-navbar-nav' className='d-md-none'>
                        {navElement()}
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </>
    );
}

export default MyNavbar;
