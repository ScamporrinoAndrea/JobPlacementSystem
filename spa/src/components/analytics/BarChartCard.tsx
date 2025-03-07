import { BarChart } from '@mui/x-charts'
import { Color } from '../../constants/colors'
import { CountInterface } from '../../interfaces/interfaces'
import { useEffect, useState, useRef } from "react"
import { analyticsAbortedJobs, analyticsConsolidatedJobs, analyticsCreatedJobs, analyticsFailedJobs, analyticsProfessionalCreated, analyticsCustomerCreated, analyticsTimeJobs } from "../../API"
import { Card, Col, FloatingLabel, Form, Row, Spinner } from "react-bootstrap";
import { handleError } from '../../utils/ToastHandlers'
import { LineChart } from '@mui/x-charts/LineChart';


const BarChartCard = ({ analytic, desc, typeChart }: { analytic: string, desc: string, typeChart: string }) => {
    const [analyticsJobsCreated, setAnalyticsJobsCreated] = useState<CountInterface[]>([])
    const [loading, setLoading] = useState(true);
    const [year, setYear] = useState('2024');
    const [month, setMonth] = useState<string | null>(null);
    const [chartDimensions, setChartDimensions] = useState({ width: 500, height: 300 });
    const chartContainerRef = useRef<HTMLDivElement>(null);
    const [chartType, setChartType] = useState(typeChart);

    useEffect(() => {
        const getDaysInMonth = (year: string, month: string | null) => {
            return new Date(Number(year), month ? Number(month) : 0, 0).getDate();
        };

        const arrayLength = month ? getDaysInMonth(year, month) : 12;

        let monthArray = Array.from({ length: arrayLength }, (v, i) => ({
            period: i + 1,
            count: 0
        }));

        if (analytic === 'Job Offers Created') {
            analyticsCreatedJobs(year, month)
                .then((jobsCreated) => {
                    jobsCreated.forEach(item => {
                        monthArray[item.period - 1].count = item.count;
                    });
                    setAnalyticsJobsCreated(monthArray);
                })
                .catch((error) => {
                    handleError(error);
                })
                .finally(() => setLoading(false));
        }
        else if (analytic === 'Job Offers Consolidated') {
            analyticsConsolidatedJobs(year, month)
                .then((jobsCreated) => {
                    jobsCreated.forEach(item => {
                        monthArray[item.period - 1].count = item.count;
                    });
                    setAnalyticsJobsCreated(monthArray);
                })
                .catch((error) => {
                    handleError(error);
                })
                .finally(() => setLoading(false));
        }
        else if (analytic === "Professional Assignation Failure") {
            analyticsFailedJobs(year, month)
                .then((jobsCreated) => {
                    jobsCreated.forEach(item => {
                        monthArray[item.period - 1].count = item.count;
                    });
                    setAnalyticsJobsCreated(monthArray);
                })
                .catch((error) => {
                    handleError(error);
                })
                .finally(() => setLoading(false));
        }
        else if (analytic === "Job Offers Aborted") {
            analyticsAbortedJobs(year, month)
                .then((jobsCreated) => {
                    jobsCreated.forEach(item => {
                        monthArray[item.period - 1].count = item.count;
                    });
                    setAnalyticsJobsCreated(monthArray);
                })
                .catch((error) => {
                    handleError(error);
                })
                .finally(() => setLoading(false));
        }
        else if (analytic === "Customers Created") {
            analyticsCustomerCreated(year, month)
                .then((jobsCreated) => {
                    jobsCreated.forEach(item => {
                        monthArray[item.period - 1].count = item.count;
                    });
                    setAnalyticsJobsCreated(monthArray);
                })
                .catch((error) => {
                    handleError(error);
                })
                .finally(() => setLoading(false));
        }
        else if (analytic === "Professionals Created") {
            analyticsProfessionalCreated(year, month)
                .then((jobsCreated) => {
                    jobsCreated.forEach(item => {
                        monthArray[item.period - 1].count = item.count;
                    });
                    setAnalyticsJobsCreated(monthArray);
                })
                .catch((error) => {
                    handleError(error);
                })
                .finally(() => setLoading(false));
        }
        else if (analytic === "Time to Consolidation") {
            analyticsTimeJobs(year)
                .then((jobsCreated) => {
                    jobsCreated.forEach(item => {
                        monthArray[item.period - 1].count = item.count;
                    });
                    setAnalyticsJobsCreated(monthArray);
                })
                .catch((error) => {
                    handleError(error);
                })
                .finally(() => setLoading(false));
        }

    }, [year, month]);

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
        <Col md={6} style={{ marginTop: 25 }}>
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
                <div
                    style={{
                        fontWeight: 'lighter',
                        fontSize: 15,
                        height: 35,
                    }}
                >
                    {desc}
                </div>

                <Row style={{ marginTop: 25 }}>
                    <Col>
                        <FloatingLabel controlId="floatingSelect" label="Chart type">
                            <Form.Select value={chartType} aria-label="Floating label select example" onChange={(e) => { setChartType(e.target.value) }}>
                                <option value="barChart">Bar Chart</option>
                                <option value="lineChart">Line Chart</option>
                            </Form.Select>
                        </FloatingLabel>
                    </Col>
                    <Col>
                        <FloatingLabel controlId="floatingSelect" label="Year">
                            <Form.Select aria-label="Floating label select example" onChange={(e) => { setLoading(true); setYear(e.target.value) }}>
                                <option value="2024">2024</option>
                                <option value="2023">2023</option>
                                <option value="2022">2022</option>
                                <option value="2021">2021</option>
                            </Form.Select>
                        </FloatingLabel>
                    </Col>
                    {analytic === 'Time to Consolidation' ? null :
                        <Col>
                            <FloatingLabel controlId="floatingSelect2" label="Month">
                                <Form.Select aria-label="Floating label select example" onChange={(e) => { setLoading(true); setMonth(e.target.value == "0" ? null : e.target.value) }}>
                                    <option value="0">All</option>
                                    <option value="1">January</option>
                                    <option value="2">February</option>
                                    <option value="3">March</option>
                                    <option value="4">April</option>
                                    <option value="5">May</option>
                                    <option value="6">June</option>
                                    <option value="7">July</option>
                                    <option value="8">August</option>
                                    <option value="9">September</option>
                                    <option value="10">October</option>
                                    <option value="11">November</option>
                                    <option value="12">December</option>
                                </Form.Select>
                            </FloatingLabel>
                        </Col>
                    }
                </Row>
                {loading ?
                    <div style={{ display: 'flex', flexDirection: 'column', width: chartDimensions.width, height: 300 }}>
                        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
                            <Spinner animation='border' variant='primary' />
                        </div>
                    </div>
                    :
                    chartType === 'barChart' ?
                        <BarChart
                            yAxis={[{
                                colorMap: {
                                    type: 'piecewise',
                                    thresholds: [0],
                                    colors: [Color.primary],
                                }
                            }]}
                            borderRadius={5}
                            xAxis={[
                                {
                                    id: 'barCategories',
                                    data:
                                        month ? Array.from({ length: analyticsJobsCreated.length }, (v, i) => i + 1) :
                                            ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
                                    scaleType: 'band',
                                },
                            ]}
                            series={[
                                {
                                    data: analyticsJobsCreated.map((item) => item.count),
                                },
                            ]}
                            width={chartDimensions.width}
                            height={300}

                        /> :
                        <LineChart
                            width={chartDimensions.width}
                            height={300}
                            yAxis={[{
                                colorMap: {
                                    type: 'piecewise',
                                    thresholds: [0],
                                    colors: [Color.primary],
                                }
                            }]}
                            series={[
                                {
                                    data: analyticsJobsCreated.map((item) => item.count),
                                },
                            ]}
                            xAxis={[
                                {
                                    id: 'barCategories',
                                    data:
                                        month ? Array.from({ length: analyticsJobsCreated.length }, (v, i) => i + 1) :
                                            ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
                                    scaleType: 'band',
                                },
                            ]}
                        />
                }
            </Card>
        </Col>
    )
}

export default BarChartCard;

