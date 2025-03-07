import { useEffect, useState } from "react";
import { Container, Form, Row, Col, Button } from "react-bootstrap";
import { addEmailToContact, createCustomer, addPhoneToContact, addAddressToContact, fetchCustomer, updateCustomer, deleteEmail, deletePhone, deleteAddress } from "../API";
import MyNavbar from "../components/MyNavbar";
import { UserInterface, CustomerFormData, EmailInterface, TelephoneInterface, AddressInterface } from "../interfaces/interfaces";
import { EmailInput, PhoneInput, AddressInput, Option } from "../components/MultiSelect";
import { useNavigate, useParams } from "react-router-dom";

const CreateCustomer = ({ user, userRole, handleError, handleSuccess }: { user: UserInterface | null, userRole: any, handleError: any, handleSuccess: any }) => {
    const [formData, setFormData] = useState<CustomerFormData>({
        name: '',
        surname: '',
        category: 'customer',
        ssncode: '',
    });
    const navigate = useNavigate();
    const [validated, setValidated] = useState(false);
    const [emailOptions, setEmailOptions] = useState<Option[]>([]);
    const [phoneOptions, setPhoneOptions] = useState<Option[]>([]);
    const [addressOptions, setAddressOptions] = useState<Option[]>([]);
    const [oldEmails, setOldEmails] = useState<EmailInterface[]>([]);
    const [oldPhones, setOldPhones] = useState<TelephoneInterface[]>([]);
    const [oldAddresses, setOldAddresses] = useState<AddressInterface[]>([]);
    const { id } = useParams();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    useEffect(() => {
        if (id) {
            fetchCustomer(id)
                .then((customer) => {
                    setFormData({
                        name: customer.name,
                        surname: customer.surname,
                        category: customer.category,
                        ssncode: customer.ssncode || '',
                    });
                    setEmailOptions(customer.mailList.map((email) => ({ value: email.mail, label: email.mail })));
                    setPhoneOptions(customer.telephoneList.map((phone) => ({ value: phone.number, label: phone.number })));
                    setAddressOptions(customer.addressList.map((address) => ({ value: address.address, label: address.address })));
                    setOldEmails(customer.mailList);
                    setOldPhones(customer.telephoneList);
                    setOldAddresses(customer.addressList);
                })
                .catch(() => {
                    handleError('Error fetching customer');
                });
        }
    }, [id]);

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = e.currentTarget;

        if (form.checkValidity() === false) {
            e.stopPropagation();
        } else {
            const emails: EmailInterface[] = emailOptions.map(opt => ({ mail: opt.value }));
            const phones: TelephoneInterface[] = phoneOptions.map(opt => ({ number: opt.value }));
            const addresses: AddressInterface[] = addressOptions.map(opt => ({ address: opt.value }));

            if (id) {
                oldEmails.forEach((email) => {
                    deleteEmail(id, email.id?.toString() || '', user?.xsrfToken || '')
                        .catch(() => handleError('Error deleting emails'));
                })
                oldPhones.forEach((phone) => {
                    deletePhone(id, phone.id?.toString() || '', user?.xsrfToken || '')
                        .catch(() => handleError('Error deleting phones'));
                })
                oldAddresses.forEach((address) => {
                    deleteAddress(id, address.id?.toString() || '', user?.xsrfToken || '')
                        .catch(() => handleError('Error deleting addresses'));
                })
                updateCustomer(formData, id, user?.xsrfToken || '')
                    .then(() => {
                        if (emails.length > 0) {
                            emails.forEach((email) => {
                                addEmailToContact(id, email, user?.xsrfToken || '')
                                    .catch(() => handleError('Error adding emails'));
                            });
                        }
                        if (phones.length > 0) {
                            phones.forEach((phone) => {
                                addPhoneToContact(id, phone, user?.xsrfToken || '')
                                    .catch(() => handleError('Error adding phones'));
                            });
                        }
                        if (addresses.length > 0) {
                            addresses.forEach((address) => {
                                addAddressToContact(id, address, user?.xsrfToken || '')
                                    .catch(() => handleError('Error adding addresses'));
                            });
                        }
                        handleSuccess('Customer updated successfully');
                        navigate('/ui/customers/' + id);
                    })
                    .catch(() => handleError('Error updating customer'));
            } else {
                createCustomer(formData, user?.xsrfToken || '')
                    .then((data) => {
                        if (emails.length > 0) {
                            emails.forEach((email) => {
                                addEmailToContact(data.id, email, user?.xsrfToken || '')
                                    .catch(() => handleError('Error adding emails'));
                            });
                        }
                        if (phones.length > 0) {
                            phones.forEach((phone) => {
                                addPhoneToContact(data.id, phone, user?.xsrfToken || '')
                                    .catch(() => handleError('Error adding phones'));
                            });
                        }
                        if (addresses.length > 0) {
                            addresses.forEach((address) => {
                                addAddressToContact(data.id, address, user?.xsrfToken || '')
                                    .catch(() => handleError('Error adding addresses'));
                            });
                        }
                        handleSuccess('Customer created successfully');
                        navigate('/ui/customers/' + data.id);
                    })
                    .catch(() => handleError('Error creating customer'));
            }
        }

        setValidated(true);
    };

    return (
        <>
            <MyNavbar user={user} userRole={userRole} />

            <Container style={{ marginTop: 25 }}>
                <h1 style={{ textAlign: 'center' }}>{id ? "Edit the customer" : "Create a new customer"}</h1>
                <Form noValidate validated={validated} onSubmit={handleSubmit}>
                    <Row>
                        <Col md={6}>
                            <Form.Group controlId="name" className="mt-3">
                                <Form.Label>Name</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleChange}
                                    placeholder="Enter a name"
                                    required
                                    isInvalid={validated && !formData.name}
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please provide the name.
                                </Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group controlId="surname" className="mt-3">
                                <Form.Label>Surname</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="surname"
                                    value={formData.surname}
                                    onChange={handleChange}
                                    placeholder="Enter a surname"
                                    required
                                    isInvalid={validated && !formData.surname}
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please provide the surname.
                                </Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group controlId="ssncode" className="mt-3">
                                <Form.Label>SSN code</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="ssncode"
                                    value={formData.ssncode}
                                    onChange={handleChange}
                                    placeholder="Enter a SSN code"
                                />
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group className="mt-3">
                                <Form.Label>Emails (Press enter after each insertion)</Form.Label>
                                <EmailInput value={emailOptions} setValue={setEmailOptions} />
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group className="mt-3">
                                <Form.Label>Phone Numbers (Press enter after each insertion)</Form.Label>
                                <PhoneInput value={phoneOptions} setValue={setPhoneOptions} />
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group className="mt-3">
                                <Form.Label>Addresses (Press enter after each insertion)</Form.Label>
                                <AddressInput value={addressOptions} setValue={setAddressOptions} />
                            </Form.Group>
                        </Col>
                    </Row>

                    <div className="d-flex justify-content-center mt-4">
                        <Button variant="primary" type="submit" style={{ width: 180, marginTop: 25 }}>
                            {id ? "Edit customer" : "Create customer"}
                        </Button>
                    </div>
                </Form>
            </Container>
        </>
    );
};

export default CreateCustomer;
