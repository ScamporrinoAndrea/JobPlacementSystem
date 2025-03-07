import { Row, Col, Button, Image } from "react-bootstrap"
import { Color } from "../constants/colors"
import Avatar from '../assets/avatar.svg';
import { JobOfferInterface, SpecificContactInterface } from "../interfaces/interfaces";
import randomcolor from 'randomcolor';
import { useState, useEffect } from "react";
import { fetchProfessional } from "../API";
import { useNavigate } from "react-router-dom";

const LeftBarJobOfferDetails = ({ jobOffer, handleError }: { jobOffer: JobOfferInterface, handleError: any }) => {
    const [professional, setProfessional] = useState<SpecificContactInterface | null>(null);
    const navigate = useNavigate();
    useEffect(() => {
        if (jobOffer.professionalId) {
            fetchProfessional(jobOffer.professionalId?.toString() || '')
                .then((professional) => {
                    setProfessional(professional);
                })
                .catch(() => {
                    handleError("Professional not found");
                });
        }
    }, [jobOffer.professionalId]);

    return (
        <Row>
            <Col md={12}>
                <div style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15 }}> Customer </div>
                <div style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15 }} className='clickable' onClick={() => navigate("/ui/customers/" + jobOffer.customer?.id)}>
                    <Image style={{ height: 38, width: 38 }} src={Avatar} roundedCircle />
                    <span style={{ marginLeft: 15, color: 'rgba(0, 0, 0, 0.8)' }}>{jobOffer.customer?.name + ' ' + jobOffer.customer?.surname}</span>
                </div>
            </Col>
            {jobOffer.professionalId &&
                <Col md={12}>
                    <div style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15 }}> Professional </div>
                    <div style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15 }} className='clickable' onClick={() => navigate("/ui/professionals/" + professional?.id)}>
                        <Image style={{ height: 38, width: 38 }} src={Avatar} roundedCircle />
                        <span style={{ marginLeft: 15, color: 'rgba(0, 0, 0, 0.8)' }}>{professional?.name + ' ' + professional?.surname}</span>
                    </div>
                </Col>
            }
            {jobOffer.requiredSkills.split(',').length > 0 ? (
                <Col md={12}>
                    <div style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15 }}> Required Skills </div>
                    <div style={{ fontWeight: 'semi-bold', fontSize: 14, marginTop: 15 }}>
                        {jobOffer.requiredSkills.split(',').map((keyword) => (
                            <span
                                key={keyword}
                                className='badge'
                                style={{
                                    backgroundColor: randomcolor({ seed: keyword.replace(/\s+/g, '').toLowerCase(), luminosity: 'bright', format: 'rgba', alpha: 1 }).replace(/1(?=\))/, '0.1'),
                                    color: randomcolor({ seed: keyword.replace(/\s+/g, '').toLowerCase(), luminosity: 'bright', format: 'rgba', alpha: 1 }),
                                    padding: '0.5em 1.2em',
                                    borderRadius: '0.25rem',
                                    marginRight: 10,
                                    marginBottom: 10,
                                }}
                            >
                                {keyword}
                            </span>
                        ))}
                    </div>
                </Col>
            ) : null}
            <Col md={12}>
                <div style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15 }}> Duration </div>
                <div style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15, display: 'flex', alignItems: 'center', justifyContent: 'flex-start' }}>
                    <span
                        className='badge'
                        style={{ backgroundColor: `${Color.primary}1a`, color: Color.primary, padding: '1em 1em', borderRadius: '0.25rem' }}
                    >
                        <i className="bi bi-calendar-range" style={{ fontSize: '16px' }}></i>
                    </span>
                    <span style={{ color: 'rgba(0, 0, 0, 0.5)', paddingLeft: 8 }}>
                        {jobOffer.duration + ' days'}
                    </span>
                </div>
            </Col>
            <Col md={12}>
                <div style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15 }}> Profit margin </div>
                <div style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15, display: 'flex', alignItems: 'center', justifyContent: 'flex-start' }}>
                    <span
                        className='badge'
                        style={{ backgroundColor: `${Color.primary}1a`, color: Color.primary, padding: '1em 1em', borderRadius: '0.25rem' }}
                    >
                        <i className="bi-graph-up-arrow" style={{ fontSize: '16px' }}></i>
                    </span>
                    <span style={{ color: 'rgba(0, 0, 0, 0.5)', paddingLeft: 8 }}>
                        {jobOffer.profitMargin + ' %'}
                    </span>
                </div>
            </Col>
            {jobOffer.professionalId != null && professional?.professionalInfo?.dailyRate != null ?
                <Col md={12}>
                    <div style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15 }}> Value </div>
                    <div style={{ fontWeight: 'medium', fontSize: 15, marginTop: 15, display: 'flex', alignItems: 'center', justifyContent: 'flex-start' }}>
                        <span
                            className='badge'
                            style={{ backgroundColor: `${Color.primary}1a`, color: Color.primary, padding: '1em 1em', borderRadius: '0.25rem' }}
                        >
                            <i className="bi bi-currency-euro" style={{ fontSize: '16px' }}></i>
                        </span>
                        <span style={{ color: 'rgba(0, 0, 0, 0.5)', paddingLeft: 8 }}>
                            {(parseFloat(((jobOffer.profitMargin / 100) * jobOffer.duration * professional?.professionalInfo?.dailyRate).toString()).toFixed(2)) + ' €'}

                        </span>
                    </div>
                </Col> : null
            }
        </Row>
    )
}

export default LeftBarJobOfferDetails