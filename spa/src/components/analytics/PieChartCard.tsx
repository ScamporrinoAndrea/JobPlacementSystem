import { useState, useEffect, useRef } from "react";
import { fetchAllJobOffers, fetchAllProfessional, fetchProfessional } from "../../API";
import { JobOfferInterface, SpecificContactInterface } from "../../interfaces/interfaces";
import { handleError } from "../../utils/ToastHandlers";
import { Col, Card, Spinner } from "react-bootstrap";
import { PieChart } from '@mui/x-charts/PieChart';
import { employmentStateColor, statusColors } from "../../utils/StatusColor";



const PieChartCard = ({ analytic }: { analytic: string }) => {
    const [loading, setLoading] = useState(true);
    const [chartDimensions, setChartDimensions] = useState({ width: 200, height: 200 });
    const chartContainerRef = useRef<HTMLDivElement>(null);
    const [data, setData] = useState<any[]>([]);

    useEffect(() => {
        if (analytic === 'Job offers status') {
            fetchAllJobOffers()
                .then(jobOffers => {
                    const JobOffersStatus = [
                        { label: 'Created', value: jobOffers.filter(jobOffer => jobOffer.status === 'created').length, color: statusColors['created'].color },
                        { label: 'Selection Phase', value: jobOffers.filter(jobOffer => jobOffer.status === 'selection_phase').length, color: statusColors['selection_phase'].color },
                        { label: 'Candidate Proposal', value: jobOffers.filter(jobOffer => jobOffer.status === 'candidate_proposal').length, color: statusColors['candidate_proposal'].color },
                        { label: 'Consolidated', value: jobOffers.filter(jobOffer => jobOffer.status === 'consolidated').length, color: statusColors['consolidated'].color },
                        { label: 'Done', value: jobOffers.filter(jobOffer => jobOffer.status === 'done').length, color: statusColors['done'].color },
                        { label: 'Aborted', value: jobOffers.filter(jobOffer => jobOffer.status === 'aborted').length, color: statusColors['aborted'].color },
                    ];
                    setData(JobOffersStatus);
                })
                .catch(() => {
                    handleError('Error fetching job offers')
                })
                .finally(() => setLoading(false));

        }
        else if (analytic === 'Profesionals status') {
            fetchAllProfessional()
                .then(professional => {
                    const ProfessionalsStatus = [
                        { label: 'Employed', value: professional.filter(professional => professional.professionalInfo?.employmentState === 'employed').length, color: employmentStateColor['employed'].color },
                        { label: 'Available', value: professional.filter(professional => professional.professionalInfo?.employmentState === 'available').length, color: employmentStateColor['available'].color },
                        { label: 'Not Available', value: professional.filter(professional => professional.professionalInfo?.employmentState === 'not available').length, color: employmentStateColor['not available'].color },
                    ];
                    setData(ProfessionalsStatus);
                })
                .catch(() => {
                    handleError('Error fetching professionals')
                })
                .finally(() => setLoading(false));
        }
    }, []);



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
                    {analytic}
                </div>
                {loading ?
                    <div style={{ display: 'flex', flexDirection: 'column', width: chartDimensions.width, height: 200 }}>
                        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
                            <Spinner animation='border' variant='primary' />
                        </div>
                    </div>
                    :
                    <PieChart
                        series={[
                            {
                                paddingAngle: 5,
                                innerRadius: 60,
                                outerRadius: 80,
                                data,
                            },
                        ]}
                        margin={{ right: 5 }}
                        width={chartDimensions.width}
                        height={200}
                        legend={{ hidden: true }}
                    />
                }
            </Card>
        </Col>
    )
}

export default PieChartCard