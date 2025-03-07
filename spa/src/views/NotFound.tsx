import logo from '../assets/logoJobFinder.png';
import Button from 'react-bootstrap/Button';
import MyNavbar from '../components/MyNavbar'
import { UserInterface } from '../interfaces/interfaces'
import { Color } from '../constants/colors';
import { useNavigate } from 'react-router-dom';

const NotFound = ({ user, userRole }: { user: UserInterface | null, userRole: any }) => {
    const navigate = useNavigate();
    return (
        <>
            <MyNavbar user={user} userRole={userRole} />
            <div style={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                height: '90vh',
                backgroundColor: '#f8f9fa',
            }}>
                <img
                    src={logo}
                    alt="logo"
                    style={{ marginBottom: '20px', height: '300px' }}
                />
                <h1 style={{ color: Color.primary }}>404 Not Found</h1>
                <p style={{ color: Color.primary }}>The page you are looking for does not exist</p>
                <br />
                <Button variant="primary" style={{ width: '300px' }} onClick={() => navigate("/ui")}>
                    Back to Home
                </Button>
            </div>
        </>
    )
}

export default NotFound