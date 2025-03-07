import { useEffect, useState } from "react";
import { Container, Form, Button, Col, Row } from "react-bootstrap";
import MyNavbar from "../components/MyNavbar";
import { EmploymentState, ProfessionalFormData, UserInterface, EmailInterface, TelephoneInterface, AddressInterface, FileData } from "../interfaces/interfaces";
import { createProfessional, addEmailToContact, addPhoneToContact, addAddressToContact, fetchProfessional, deleteAddress, deleteEmail, deletePhone, updateProfessional, insertNewDocument, fetchMetadataDocument } from "../API";
import { EmailInput, PhoneInput, AddressInput, Option } from "../components/MultiSelect";
import { useNavigate, useParams } from "react-router-dom";

const CreateProfessional = ({ user, userRole, handleError, handleSuccess }: { user: UserInterface | null, userRole: any, handleError: any, handleSuccess: any }) => {
    const [formData, setFormData] = useState<ProfessionalFormData>({
        name: '',
        surname: '',
        category: 'professional',
        ssncode: '',
        professionalInfo: {
            skills: '',
            employmentState: EmploymentState.AVAILABLE,
            location: '',
            dailyRate: 0
        }
    });
    const navigate = useNavigate();
    const [validated, setValidated] = useState(false);
    const [emailOptions, setEmailOptions] = useState<Option[]>([]);
    const [phoneOptions, setPhoneOptions] = useState<Option[]>([]);
    const [addressOptions, setAddressOptions] = useState<Option[]>([]);
    const [oldEmails, setOldEmails] = useState<EmailInterface[]>([]);
    const [oldPhones, setOldPhones] = useState<TelephoneInterface[]>([]);
    const [oldAddresses, setOldAddresses] = useState<AddressInterface[]>([]);
    const [documentMetadata, setDocumentMetadata] = useState<string>('');
    const [fileData, setFileData] = useState<FileData>({
        name: '',
        type: '',
        size: 0,
        content: null,
    });
    const { id } = useParams();

    useEffect(() => {
        if (id) {
            fetchProfessional(id)
                .then((professional) => {
                    fetchMetadataDocument(id?.toString() || "")
                        .then((id) => {
                            setDocumentMetadata(id)
                        })
                        .catch(() => {
                            handleError("documents not found")
                        })
                    setFormData({
                        name: professional.name,
                        surname: professional.surname,
                        category: professional.category,
                        ssncode: professional.ssncode || '',
                        professionalInfo: {
                            skills: professional.professionalInfo?.skills || '',
                            employmentState: professional.professionalInfo?.employmentState || EmploymentState.AVAILABLE,
                            location: professional.professionalInfo?.location || '',
                            dailyRate: professional.professionalInfo?.dailyRate || 0
                        }
                    });
                    setEmailOptions(professional.mailList.map((email) => ({ value: email.mail, label: email.mail })));
                    setPhoneOptions(professional.telephoneList.map((phone) => ({ value: phone.number, label: phone.number })));
                    setAddressOptions(professional.addressList.map((address) => ({ value: address.address, label: address.address })));
                    setOldEmails(professional.mailList);
                    setOldPhones(professional.telephoneList);
                    setOldAddresses(professional.addressList);
                })
                .catch(() => {
                    handleError('Error fetching professional');
                });
        }
    }, [id]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value } = e.target;

        if (name in formData.professionalInfo) {
            setFormData({
                ...formData,
                professionalInfo: {
                    ...formData.professionalInfo,
                    [name]: name === 'dailyRate' ? parseFloat(value) : value
                }
            });
        } else {
            setFormData({
                ...formData,
                [name]: value
            });
        }
    };

    function handleChangeDocument(e: React.ChangeEvent<HTMLInputElement>) {
        const file = e.target.files?.[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setFileData({
                    name: file.name,
                    type: file.type,
                    size: file.size,
                    content: reader.result as ArrayBuffer,
                });
            };
            reader.readAsArrayBuffer(file);
        }
    }

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
                updateProfessional(formData, id, user?.xsrfToken || '')
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
                        if (fileData.name) {
                            insertNewDocument(fileData, null, id, user?.xsrfToken || '')
                                .then(() => navigate('/ui/professionals/' + id))
                                .catch(() => handleError('Error inserting document'));
                        }
                        else {
                            navigate('/ui/professionals/' + id)
                        }
                        handleSuccess('Professional updated successfully');
                    })
                    .catch(() => handleError('Error updating professional'));
            } else {
                createProfessional(formData, user?.xsrfToken || '')
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
                        if (fileData.name) {
                            insertNewDocument(fileData, null, data.id, user?.xsrfToken || '')
                                .then(() => navigate('/ui/professionals/' + data.id))
                                .catch(() => handleError('Error inserting document'));
                        }
                        else {
                            navigate('/ui/professionals/' + data.id)
                        }
                        handleSuccess('Professional created successfully');
                    })
                    .catch(() => handleError('Error creating professional'));
            }
        }

        setValidated(true);
    };

    return (
        <>
            <MyNavbar user={user} userRole={userRole} />

            <Container style={{ marginTop: 25 }}>
                <h1 style={{ textAlign: 'center' }}>{id ? "Edit the professional" : "Create a new professional"}</h1>
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
                                    placeholder="Enter SSN code"
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please provide the SSN code.
                                </Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group controlId="skills" className="mt-3">
                                <Form.Label>Skills</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="skills"
                                    value={formData.professionalInfo.skills}
                                    onChange={handleChange}
                                    placeholder="Enter skills"
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please provide skills.
                                </Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group controlId="employmentState" className="mt-3">
                                <Form.Label>Employment State</Form.Label>
                                <Form.Select
                                    name="employmentState"
                                    value={formData.professionalInfo.employmentState}
                                    onChange={handleChange}
                                    required
                                    isInvalid={validated && !formData.professionalInfo.employmentState}
                                >
                                    <option value={EmploymentState.AVAILABLE}>Available</option>
                                    <option value={EmploymentState.NOT_AVAILABLE}>Not available</option>
                                    <option value={EmploymentState.EMPLOYED}>Employed</option>
                                </Form.Select>
                                <Form.Control.Feedback type="invalid">
                                    Please select an employment state.
                                </Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group controlId="location" className="mt-3">
                                <Form.Label>Location</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="location"
                                    value={formData.professionalInfo.location}
                                    onChange={handleChange}
                                    placeholder="Enter location"
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please provide a location.
                                </Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group controlId="dailyRate" className="mt-3">
                                <Form.Label>Daily Rate</Form.Label>
                                <Form.Control
                                    type="number"
                                    name="dailyRate"
                                    value={formData.professionalInfo.dailyRate || ''}
                                    onChange={handleChange}
                                    placeholder="Enter daily rate"
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please provide a valid daily rate.
                                </Form.Control.Feedback>
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
                        {documentMetadata ? null :
                            <Col md={6}>
                                <Form.Group controlId='formFile' className='mt-3'>
                                    <Form.Label>Upload a document</Form.Label>
                                    <Form.Control type='file' onChange={handleChangeDocument} />
                                </Form.Group>
                            </Col>
                        }
                    </Row>

                    <div className="d-flex justify-content-center mt-4">
                        <Button variant="primary" type="submit" style={{ width: 180, marginTop: 25 }}>
                            {id ? "Edit professional" : "Create professional"}
                        </Button>
                    </div>
                </Form>
            </Container>
        </>
    );
};

export default CreateProfessional;
