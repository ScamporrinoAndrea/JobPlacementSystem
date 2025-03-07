
import 'bootstrap/dist/css/bootstrap.min.css';
import { Row, Col, Card, Image, Button } from 'react-bootstrap';
import randomcolor from 'randomcolor';
import { Color } from '../constants/colors.js';
import Avatar from '../assets/avatar.svg';
import { useNavigate } from 'react-router-dom';
import { JobOfferInterface } from '../interfaces/interfaces.js';
import { statusColors } from '../utils/StatusColor.js';

const JobOfferCard = (jobOffer: JobOfferInterface) => {
    const navigate = useNavigate();

    const { backgroundColor, color, icon, text } = statusColors[jobOffer.status] || {};
    return (
        <Col lg={6} sm={12} style={{ marginTop: 25 }}>
            <Card style={{ padding: 20 }} className='custom-card'>
                <div
                    style={{
                        fontWeight: 'medium',
                        fontSize: 18,
                        height: 25,
                        display: '-webkit-box',
                        WebkitBoxOrient: 'vertical',
                        WebkitLineClamp: '2',
                        overflow: 'hidden',
                    }}
                >
                    Job offer n. {jobOffer.id}
                </div>
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
                    {jobOffer.requiredSkills.split(',').map((skill) => (
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
                <Row style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15, height: 30 }}>
                    <Col xs={7} style={{ display: 'flex', alignItems: 'center' }}>
                        <div className='clickable' onClick={() => navigate("/ui/customers/" + jobOffer.customer?.id)} style={{ display: 'flex', alignItems: 'center', flexDirection: 'row' }}>
                            <Image style={{ height: 35, width: 35, marginRight: 8 }} src={Avatar} roundedCircle />
                            <div>
                                <div>Customer</div>
                                <div style={{ color: 'rgba(0, 0, 0, 0.5)', fontSize: 13 }}>
                                    {jobOffer.customer?.name + ' ' + jobOffer.customer?.surname}
                                </div>
                            </div>
                        </div>
                    </Col>

                    <Col style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end' }}>
                        <span
                            className='badge'
                            style={{ backgroundColor: `${Color.primary}1a`, color: Color.primary, padding: '1em 1em', borderRadius: '0.25rem' }}
                        >
                            <i className="bi bi-calendar-range" style={{ fontSize: '16px' }}></i>
                        </span>
                        <span style={{ color: 'rgba(0, 0, 0, 0.5)', paddingLeft: 8 }}>
                            {jobOffer.duration + ' days'}
                        </span>
                    </Col>
                </Row>
                <div
                    style={{
                        fontWeight: 'regular',
                        fontSize: 16,
                        marginTop: 20,
                        minHeight: 72,
                        color: 'rgba(0, 0, 0, 0.8)',
                        display: '-webkit-box',
                        WebkitBoxOrient: 'vertical',
                        WebkitLineClamp: '3',
                        overflow: 'hidden',
                        whiteSpace: 'pre-line',
                    }}
                >
                    {jobOffer.description}
                </div>
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
                            <Button onClick={() => navigate('/ui/joboffers/create/' + jobOffer.id)} style={{ marginRight: 10, backgroundColor: Color.secondary, borderColor: Color.secondary }} >
                                <i className='bi bi-pencil'></i>
                            </Button>
                            <Button variant='primary' onClick={() => navigate('/ui/joboffers/' + jobOffer.id)}>
                                <i className='bi bi-arrow-right' style={{ fontSize: '16px' }}></i>
                            </Button>
                        </div>
                    </Col>
                </Row>
            </Card>
        </Col>
    );
}

export default JobOfferCard

