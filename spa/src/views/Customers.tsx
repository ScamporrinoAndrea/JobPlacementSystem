import { useState, useEffect, SetStateAction } from "react";
import { fetchAllCustomers } from "../API";
import MyNavbar from "../components/MyNavbar"
import { SpecificContactInterface, UserInterface } from "../interfaces/interfaces"
import { Button, Container, Row } from "react-bootstrap";
import SearchBar from "../components/SearchBar";
import CustomerCard from "../components/CustomerCard";
import Loading from "../components/Loading";
import { useNavigate } from "react-router-dom";


const Customers = ({ user, userRole, handleError, handleSuccess }: { user: UserInterface | null, userRole: any, handleError: any, handleSuccess: any }) => {
    const navigate = useNavigate();
    const [customers, setCustomer] = useState<SpecificContactInterface[]>([]);
    const [search, setSearch] = useState('');
    const [loading, setLoading] = useState(true);


    useEffect(() => {
        setLoading(true);
        fetchAllCustomers()
            .then(customers => {
                setCustomer(customers)
            })
            .catch(() => { setCustomer([]), handleError('Error fetching customers') })
            .finally(() => setLoading(false));

    }, []);

    function handleSearch(e: { target: { value: SetStateAction<string>; }; }) {
        setSearch(e.target.value);
    }

    function filterCustomers(customers: SpecificContactInterface[], search: string) {
        return customers.filter((customers: SpecificContactInterface) => {

            return customers.name.toLowerCase().includes(search.toLowerCase()) ||
                customers.surname.toLowerCase().includes(search.toLowerCase()) ||
                customers.ssncode?.toLowerCase().includes(search.toLowerCase()) ||
                customers.addressList.join().toLowerCase().includes(search.toLowerCase()) ||
                customers.mailList.join().toLowerCase().includes(search.toLowerCase()) ||
                customers.telephoneList.join().toLowerCase().includes(search.toLowerCase());
        });
    }

    const filteredCustomers = filterCustomers(customers, search);

    return (
        <>
            <MyNavbar user={user} userRole={userRole} />
            <div style={{ position: 'sticky', top: 0, zIndex: 2, backgroundColor: 'white', boxShadow: '0 4px 2px -2px rgba(0, 0, 0, 0.2)' }}>
                <Container>
                    <SearchBar search={search} handleSearch={handleSearch} />
                    <div style={{ paddingBottom: 20 }} />
                </Container>
            </div>
            <Container>
                {loading ? <Loading /> :
                    <Row style={{ marginBottom: 25 }}>
                        {filteredCustomers.length > 0 ? (
                            filteredCustomers.map((customer: SpecificContactInterface) => <CustomerCard key={customer.id} {...customer} />)
                        ) : (
                            <div className='d-flex justify-content-center align-items-center' style={{ height: '50vh' }}>
                                <div className='text-center'>
                                    <h1>No Customers</h1>
                                </div>
                            </div>
                        )}
                    </Row>
                }
            </Container>
            {userRole == 'manager' || userRole == 'operator' ?
                <Button variant="primary" onClick={() => navigate("/ui/customers/create")} size="lg" style={{ position: 'fixed', bottom: 20, right: 20, zIndex: 2, borderRadius: 100 }}>
                    +
                </Button>
                : null
            }
        </>
    )
}

export default Customers