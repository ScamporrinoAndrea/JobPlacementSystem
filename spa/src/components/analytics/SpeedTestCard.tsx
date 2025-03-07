import { useState, useEffect, useRef } from "react";
import { fetchAllJobOffers } from "../../API";
import { JobOfferInterface } from "../../interfaces/interfaces";
import { handleError } from "../../utils/ToastHandlers";
import { Col, Card, Spinner } from "react-bootstrap";
import { Gauge, gaugeClasses } from '@mui/x-charts/Gauge';
import { statusColors } from "../../utils/StatusColor";
import { Color } from "../../constants/colors";



const SpeedTestCard = () => {
    const [loading, setLoading] = useState(true);
    const [jobOffers, setJobOffers] = useState<JobOfferInterface[]>([]);
    const [chartDimensions, setChartDimensions] = useState({ width: 200, height: 200 });
    const chartContainerRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
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

    const data = [
        { label: 'Created', value: jobOffers.filter(jobOffer => jobOffer.status === 'created').length, color: statusColors['created'].color },
        { label: 'Selection Phase', value: jobOffers.filter(jobOffer => jobOffer.status === 'selection_phase').length, color: statusColors['selection_phase'].color },
        { label: 'Candidate Proposal', value: jobOffers.filter(jobOffer => jobOffer.status === 'candidate_proposal').length, color: statusColors['candidate_proposal'].color },
        { label: 'Consolidated', value: jobOffers.filter(jobOffer => jobOffer.status === 'consolidated').length, color: statusColors['consolidated'].color },
        { label: 'Done', value: jobOffers.filter(jobOffer => jobOffer.status === 'done').length, color: statusColors['done'].color },
        { label: 'Aborted', value: jobOffers.filter(jobOffer => jobOffer.status === 'aborted').length, color: statusColors['aborted'].color },
    ];

    useEffect(() => {
        const resizeObserver = new ResizeObserver((entries) => {
            for (let entry of entries) {
                const { width, height } = entry.contentRect;
                setChartDimensions({
                    width: width,
                    height: height,
                });
            }
        });

        if (chartContainerRef.current) {
            resizeObserver.observe(chartContainerRef.current);
        }

        return () => {
            if (chartContainerRef.current) {
                resizeObserver.unobserve(chartContainerRef.current);
            }
        };
    }, []);

    return (
        <Col md={4} style={{ marginTop: 25 }}>
            <Card ref={chartContainerRef} style={{ padding: 20 }} className='custom-card'>
                <div
                    style={{
                        fontWeight: 'bold',
                        fontSize: 18,
                        height: 25,
                    }}
                >
                    Job Offers Done
                </div>
                {loading ?
                    <div style={{ display: 'flex', flexDirection: 'column', width: chartDimensions.width, height: 200 }}>
                        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
                            <Spinner animation='border' variant='primary' />
                        </div>
                    </div>
                    :
                    <Gauge
                        value={jobOffers.filter(jobOffer => jobOffer.status === 'done').length}
                        valueMax={jobOffers.length}
                        startAngle={-110}
                        endAngle={110}
                        sx={{
                            [`& .${gaugeClasses.valueText}`]: {
                                fontSize: 40,
                                transform: 'translate(0px, 0px)',
                            },
                            [`& .${gaugeClasses.valueArc}`]: {
                                fill: Color.primary,
                            },
                        }}
                        text={
                            ({ value, valueMax }) => `${value} / ${valueMax}`
                        }
                        width={chartDimensions.width}
                        height={200}
                    />
                }
            </Card>
        </Col>
    )
}

export default SpeedTestCard