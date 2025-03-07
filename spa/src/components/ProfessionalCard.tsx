import { Col, Card, Row, Button, Image } from "react-bootstrap"
import { Color } from "../constants/colors"
import { SpecificContactInterface } from "../interfaces/interfaces"
import Avatar from '../assets/avatar.svg';
import { useNavigate } from 'react-router-dom';
import randomcolor from 'randomcolor';
import { employmentStateColor } from "../utils/StatusColor"



const ProfessionalCard = (professional: SpecificContactInterface) => {
    const navigate = useNavigate();


    const { backgroundColor = '', color = '', icon = '', text = '' } = professional.professionalInfo ? employmentStateColor[professional.professionalInfo.employmentState] : {};
    return (
        <Col lg={4} md={6} xs={12} style={{ marginTop: 25 }}>
            <Card style={{ padding: 20 }} className='custom-card'>
                <Row style={{ fontWeight: 'medium', fontSize: 15 }}>
                    <Col style={{ display: 'flex', alignItems: 'center' }}>
                        <div style={{ display: 'flex', flexDirection: 'row' }}>
                            <Image style={{ height: 50, width: 50, marginRight: 8 }} src={Avatar} roundedCircle />
                            <div>
                                <div>{professional.name + ' ' + professional.surname}</div>
                                <div style={{ color: 'rgba(0, 0, 0, 0.5)', fontSize: 13 }}>
                                    {professional.ssncode ? professional.ssncode : 'No SSN'}
                                </div>
                            </div>
                        </div>
                    </Col>
                </Row>
                <div
                    className='hide-scrollbar'
                    style={{
                        fontWeight: 'semi-bold',
                        fontSize: 14,
                        marginTop: 5,
                        overflowX: 'auto',
                        whiteSpace: 'nowrap',
                        scrollbarWidth: 'none',
                        msOverflowStyle: 'none',
                        height: 25
                    }}
                >
                    {professional.professionalInfo?.skills.split(',').map((skill) => (
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
                <Row style={{ marginTop: 15 }}>
                    <Col xs={6} style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-start' }}>
                        <span
                            className='badge'
                            style={{ backgroundColor: `${Color.primary}1a`, color: Color.primary, padding: '1em 1em', borderRadius: '0.25rem' }}
                        >
                            <i className="bi bi-envelope" style={{ fontSize: '16px' }}></i>
                        </span>
                        <span className='oneLineText' style={{ color: 'rgba(0, 0, 0, 0.5)', paddingLeft: 8 }}>
                            <span className='oneLineText'>
                                {professional.mailList.length > 0 ? professional.mailList[0].mail : 'No email'}
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
                                {professional.telephoneList.length > 0 ? professional.telephoneList[0].number : 'No telephone'}
                            </span>
                        </span>
                    </Col>
                </Row>
                <Row>
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
                    <Col>
                        <div style={{ marginTop: 20, marginRight: 10, textAlign: 'right' }}>
                            <Button variant='primary' onClick={() => navigate('/ui/professionals/' + professional.id)}>
                                <i className='bi bi-arrow-right' style={{ fontSize: '16px' }}></i>
                            </Button>
                        </div>
                    </Col>
                </Row>
            </Card>
        </Col>
    )
}

export default ProfessionalCard