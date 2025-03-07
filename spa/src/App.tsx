import 'bootstrap/dist/css/bootstrap.min.css'
import { useEffect, useState } from 'react'
import { fetchUser, fetchUserRole } from './API';
import './Style.css'
import JobOffers from './views/JobOffers';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Customers from './views/Customers';
import Professionals from './views/Professionals';
import '../src/constants/custom-styles.scss'
import 'bootstrap-icons/font/bootstrap-icons.css';
import { ModalInterface, UserInterface } from './interfaces/interfaces';
import CreateProfessional from './views/CreateProfessional';
import CreateJobOffer from './views/CreateJobOffer';
import CreateCustomer from './views/CreateCustomer';
import { handleError, handleSuccess } from './utils/ToastHandlers.ts';
import { Toaster } from 'react-hot-toast';
import JobOfferDetails from './views/JobOfferDetails.tsx';
import GenericModal from './components/GenericModal.tsx';
import CustomerDetails from './views/CustomerDetails.tsx';
import ProfessionalDetails from './views/ProfessionalDetails.tsx';
import Dashboard from './views/Dashboard.tsx';
import NotFound from './views/NotFound.tsx';
import Login from './views/Login.tsx';

function App() {
    const [user, setUser] = useState<UserInterface | null>(null)
    const [userRole, setUserRole] = useState<any>('')

    //GenericModal
    const [showModal, setShowModal] = useState(false);
    const [msgModal, setMsgModal] = useState<ModalInterface | null>(null);

    useEffect(() => {
        fetchUser()
            .then(user => {
                setUser(user)
                fetchUserRole(user?.email || '', user?.xsrfToken || '')
                    .then(userRole => {
                        setUserRole(userRole["user's roles"].replace(/[\[\]]/g, ''));
                    })
                    .catch(() => {
                        setUserRole('');
                    });
            })
            .catch(() => setUser(null));

    }, []);


    return (
        <BrowserRouter>
            <GenericModal showModal={showModal} setShowModal={setShowModal} msgModal={msgModal || { header: '', body: '', method: null }} />
            <Toaster />
            <Routes>
                {user && user.principal == null && user.loginUrl &&
                    <Route path='/ui' element={<Login user={user} />} />
                }
                {userRole == "manager" ?
                    <Route path='/ui' element={<Dashboard user={user} userRole={userRole} handleError={handleError} handleSuccess={handleSuccess} />} /> : null
                }
                <>
                    <Route path={userRole == "manager" ? '/ui/joboffers' : '/ui'} element={<JobOffers user={user} userRole={userRole} handleError={handleError} handleSuccess={handleSuccess} />} />
                    <Route path='/ui/joboffers/:id' element={<JobOfferDetails user={user} userRole={userRole} handleError={handleError} handleSuccess={handleSuccess} setMsgModal={setMsgModal} setShowModal={setShowModal} />} />
                    <Route path='/ui/joboffers/create' element={<CreateJobOffer user={user} userRole={userRole} handleError={handleError} handleSuccess={handleSuccess} />} />
                    <Route path='/ui/joboffers/create/:id' element={<CreateJobOffer user={user} userRole={userRole} handleError={handleError} handleSuccess={handleSuccess} />} />

                    <Route path='/ui/customers' element={<Customers user={user} userRole={userRole} handleError={handleError} handleSuccess={handleSuccess} />} />
                    <Route path='/ui/customers/create' element={<CreateCustomer user={user} userRole={userRole} handleError={handleError} handleSuccess={handleSuccess} />} />
                    <Route path='/ui/customers/:id' element={<CustomerDetails user={user} userRole={userRole} setMsgModal={setMsgModal} setShowModal={setShowModal} />} />
                    <Route path='/ui/customers/create/:id' element={<CreateCustomer user={user} userRole={userRole} handleError={handleError} handleSuccess={handleSuccess} />} />


                    <Route path='/ui/professionals' element={<Professionals user={user} userRole={userRole} handleError={handleError} handleSuccess={handleSuccess} />} />
                    <Route path='/ui/professionals/create' element={<CreateProfessional user={user} userRole={userRole} handleError={handleError} handleSuccess={handleSuccess} />} />
                    <Route path='/ui/professionals/:id' element={<ProfessionalDetails user={user} userRole={userRole} setMsgModal={setMsgModal} setShowModal={setShowModal} />} />
                    <Route path='/ui/professionals/create/:id' element={<CreateProfessional user={user} userRole={userRole} handleError={handleError} handleSuccess={handleSuccess} />} />
                </>
                <Route path='*' element={<NotFound user={user} userRole={userRole} />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App
