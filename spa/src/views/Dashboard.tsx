import MyNavbar from "../components/MyNavbar"
import { UserInterface } from "../interfaces/interfaces"
import { Col, Container, Row } from "react-bootstrap";
import BarChartCard from "../components/analytics/BarChartCard";
import PieChartCard from "../components/analytics/PieChartCard";
import SpeedTestCard from "../components/analytics/SpeedTestCard";
import { useState, useEffect } from "react";
import { fetchAllCustomers, fetchAllJobOffers, fetchAllProfessional } from "../API";
import { statusColors, employmentStateColor } from "../utils/StatusColor";


const Dashboard = ({ user, userRole, handleError, handleSuccess }: { user: UserInterface | null, userRole: any, handleError: any, handleSuccess: any }) => {
    const [totalJobOffers, setTotalJobOffers] = useState(0);
    const [totalProfessionals, setTotalProfessionals] = useState(0);
    const [totalCustomers, setTotalCustomers] = useState(0);

    useEffect(() => {
        fetchAllJobOffers()
            .then(jobOffers => {
                setTotalJobOffers(jobOffers.length)
            })
            .catch(() => {
                handleError('Error fetching job offers')
            })
        fetchAllProfessional()
            .then(professional => {
                setTotalProfessionals(professional.length)
            })
            .catch(() => {
                handleError('Error fetching professionals')
            })
        fetchAllCustomers()
            .then(customers => {
                setTotalCustomers(customers.length)
            })
            .catch(() => {
                handleError('Error fetching customers')
            })

    }, []);
    return (
        <>
            <div style={{ position: 'fixed', top: 0, width: '100%', zIndex: 30 }}>
                <MyNavbar user={user} userRole={userRole} />
            </div>
            <Container style={{ marginBottom: 25 }}>
                <h1 style={{ marginTop: 105 }}>Dashboard Analytic</h1>
                <div style={{
                    fontSize: 20,
                    fontWeight: 'medium',
                }}>
                    Total job offers: {totalJobOffers}
                </div>
                <div style={{
                    fontSize: 20,
                    fontWeight: 'medium',
                }}>
                    Total customers: {totalCustomers}
                </div>
                <div style={{
                    fontSize: 20,
                    fontWeight: 'medium',
                }}>
                    Total professionals: {totalProfessionals}
                </div>
                <Row>
                    <PieChartCard analytic="Job offers status" />
                    <SpeedTestCard />
                    <PieChartCard analytic="Profesionals status" />
                    <BarChartCard analytic="Job Offers Created" desc="Number of job offers created filtered by year and month" typeChart="lineChart" />
                    <BarChartCard analytic="Job Offers Consolidated" desc="Number of job offers consolidated filtered by year and month" typeChart="barChart" />
                    {/* <BarChartCard analytic="Professional Assignation Failure" desc="Number of times which job offers have returned to selection phase status after filtered by year and month" typeChart="barChart"/> */}
                    <BarChartCard analytic="Job Offers Aborted" desc="Number of job offers with status aborted filtered by year and month" typeChart="barChart" />
                    <BarChartCard analytic="Time to Consolidation" desc="Average time in days per month from creation to consolidation of a job offer" typeChart="barChart" />
                    <BarChartCard analytic="Customers Created" desc="Number of customers created filtered by year and month" typeChart="lineChart" />
                    <BarChartCard analytic="Professionals Created" desc="Number of professionals created filtered by year and month" typeChart="lineChart" />
                </Row>
            </Container>

            {/* {JSON.stringify(analyticsJobsCreated, null, 4)} */}
        </>
    )
}

export default Dashboard