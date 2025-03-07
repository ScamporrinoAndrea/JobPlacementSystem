import { useEffect, useState } from "react";
import { Container, Form, Button, Col, Row } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { JobOfferFormData, SpecificContactInterface, UserInterface } from "../interfaces/interfaces";
import Loading from "../components/Loading";
import { createJobOffer, fetchAllCustomers, fetchJobOffer, updateJobOffer } from "../API";
import { useNavigate, useParams } from "react-router-dom";



const CreateJobOffer = ({ user, userRole, handleError, handleSuccess }: { user: UserInterface | null, userRole: any, handleError: any, handleSuccess: any }) => {
    const [formData, setFormData] = useState<JobOfferFormData>({
        description: '',
        requiredSkills: '',
        duration: 1,
        profitMargin: 1,
    });
    const [validated, setValidated] = useState(false);
    const [customers, setCustomers] = useState<SpecificContactInterface[]>([]);
    const [customerId, setCustomerId] = useState<string>('');
    const [loading, setLoading] = useState(true);
    const { id } = useParams();
    const navigate = useNavigate();

    useEffect(() => {
        setLoading(true);
        fetchAllCustomers()
            .then(customers => {
                setCustomers(customers);
                if (id) {
                    fetchJobOffer(id)
                        .then(jobOffer => {
                            if (jobOffer) {
                                setFormData({
                                    description: jobOffer.description,
                                    requiredSkills: jobOffer.requiredSkills,
                                    duration: jobOffer.duration,
                                    profitMargin: jobOffer.profitMargin,
                                });
                                setCustomerId(jobOffer.customer?.id?.toString() || '');
                            }
                        })
                        .catch(() => handleError('Error fetching job offer'))
                }
            })
            .catch(() => {
                setCustomers([])
                handleError('Error fetching customers')
            })
            .finally(() => setLoading(false));
    }, []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: name === 'duration' || name === 'profitMargin' ? parseFloat(value) : value
        });
    };

    const handleChangeCustomer = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { value } = e.target;
        setCustomerId(value);
    };

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = e.currentTarget;

        if (form.checkValidity() === false) {
            e.stopPropagation();
        } else {
            if (id) {
                updateJobOffer(formData, id, user?.xsrfToken || '')
                    .then(() => {
                        handleSuccess('Job offer updated')
                        navigate('/ui/joboffers/' + id)
                    })
                    .catch(() => handleError('Error updating job offer'))
            }
            else {
                createJobOffer(formData, customerId, user?.xsrfToken || '')
                    .then((data) => {
                        handleSuccess('Job offer created')
                        navigate('/ui/joboffers/' + data.id)
                    })
                    .catch(() => handleError('Error creating job offer'))
            }
        }

        setValidated(true);
    };

    return (
        <>
            <MyNavbar user={user} userRole={userRole} />
            {loading ? <Loading /> :
                <Container style={{ marginTop: 25 }}>
                    <h1 style={{ textAlign: 'center' }}>{id ? "Edit Job offer" : "Create a new Job Offer"}</h1>
                    <Form noValidate validated={validated} onSubmit={handleSubmit}>
                        <Row>
                            <Col md={12}>
                                <Form.Group controlId="description">
                                    <Form.Label>Description</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        name="description"
                                        value={formData.description}
                                        onChange={handleChange}
                                        rows={3}
                                        placeholder="Enter job description"
                                        required
                                        isInvalid={validated && !formData.description}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide a job description.
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                        </Row>
                        <Row>
                            <Col md={6}>
                                <Form.Group controlId="duration" className="mt-3">
                                    <Form.Label>Duration (days)</Form.Label>
                                    <Form.Control
                                        type="number"
                                        name="duration"
                                        value={formData.duration || ''}
                                        onChange={handleChange}
                                        placeholder="Enter duration in days"
                                        required
                                        isInvalid={validated && formData.duration <= 0}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please enter a valid duration in days.
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group controlId="requiredSkills" className="mt-3">
                                    <Form.Label>Required Skills (separated by comma)</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="requiredSkills"
                                        value={formData.requiredSkills}
                                        onChange={handleChange}
                                        placeholder="Enter required skills"
                                        required
                                        isInvalid={validated && !formData.requiredSkills}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please provide the required skills.
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group controlId="profitMargin" className="mt-3">
                                    <Form.Label>Profit Margin (%)</Form.Label>
                                    <Form.Control
                                        type="number"
                                        name="profitMargin"
                                        value={formData.profitMargin || ''}
                                        onChange={handleChange}
                                        placeholder="Enter profit margin"
                                        required
                                        isInvalid={validated && formData.profitMargin < 0 || formData.profitMargin > 100}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        Please enter a valid profit margin percentage.
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                            {!id &&
                                <Col md={6}>
                                    <Form.Group controlId="customerId" className="mt-3">
                                        <Form.Label>Select Customer</Form.Label>
                                        <Form.Select
                                            name="customerId"
                                            value={customerId}
                                            onChange={handleChangeCustomer}
                                            required
                                            isInvalid={validated && !customerId}
                                        >
                                            <option value="">Select a customer</option>
                                            {customers.map(customer => (
                                                <option key={customer.id} value={customer.id?.toString()}>
                                                    {customer.name} {customer.surname}
                                                </option>
                                            ))}
                                        </Form.Select>
                                        <Form.Control.Feedback type="invalid">
                                            Please select a customer.
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            }
                        </Row>

                        <div className="d-flex justify-content-center mt-4">
                            <Button variant="primary" type="submit" style={{ width: 180, marginTop: 25 }}>
                                {id ? "Edit job offer" : "Create job offer"}
                            </Button>
                        </div>
                    </Form>
                </Container>
            }
        </>
    );
}

export default CreateJobOffer;
