import { Col, Card, Row, Button, Image } from "react-bootstrap"
import { Color } from "../constants/colors"
import { SpecificContactInterface } from "../interfaces/interfaces"
import Avatar from '../assets/avatar.svg';
import { useNavigate } from 'react-router-dom';



const CustomerCard = (customer: SpecificContactInterface) => {
    const navigate = useNavigate();

    return (
        <Col lg={4} md={6} xs={12} style={{ marginTop: 25 }}>
            <Card style={{ padding: 20 }} className='custom-card'>
                <Row style={{ fontWeight: 'medium', fontSize: 15 }}>
                    <Col xs={10} style={{ display: 'flex', alignItems: 'center' }}>
                        <div style={{ display: 'flex', flexDirection: 'row' }}>
                            <Image style={{ height: 50, width: 50, marginRight: 8 }} src={Avatar} roundedCircle />
                            <div>
                                <div>{customer.name + ' ' + customer.surname}</div>
                                <div style={{ color: 'rgba(0, 0, 0, 0.5)', fontSize: 13 }}>
                                    {customer.ssncode ? customer.ssncode : 'No SSN'}
                                </div>
                            </div>
                        </div>
                    </Col>
                    <Col xs={2} style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end' }}>
                        <div style={{ textAlign: 'right' }}>
                            <Button variant='primary' onClick={() => navigate('/ui/customers/' + customer.id)}>
                                <i className='bi bi-arrow-right' style={{ fontSize: '16px' }}></i>
                            </Button>
                        </div>
                    </Col>
                </Row>
                <Row style={{ marginTop: 15 }}>
                    <Col xs={6} style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-start' }}>
                        <span
                            className='badge'
                            style={{ backgroundColor: `${Color.primary}1a`, color: Color.primary, padding: '1em 1em', borderRadius: '0.25rem' }}
                        >
                            <i className="bi bi-envelope" style={{ fontSize: '16px' }}></i>
                        </span>
                        <span className=' oneLineText' style={{ color: 'rgba(0, 0, 0, 0.5)', paddingLeft: 8 }}>
                            <span className='oneLineText'>
                                {customer.mailList.length > 0 ? customer.mailList[0].mail : 'No email'}
                            </span>
                        </span>
                    </Col>
                    <Col xs={6} style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end' }}>
                        <span
                            className='badge'
                            style={{ backgroundColor: `${Color.primary}1a`, color: Color.primary, padding: '1em 1em', borderRadius: '0.25rem' }}
                        >
                            <i className="bi bi-telephone" style={{ fontSize: '16px' }}></i>
                        </span>
                        <span className='oneLineText' style={{ color: 'rgba(0, 0, 0, 0.5)', paddingLeft: 8 }}>
                            <span className='oneLineText'>
                                {customer.telephoneList.length > 0 ? customer.telephoneList[0].number : 'No telephone'}
                            </span>
                        </span>
                    </Col>
                </Row>
            </Card>
        </Col>
    )
}

export default CustomerCard