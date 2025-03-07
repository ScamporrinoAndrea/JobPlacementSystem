import logo from '../assets/logoJobFinder.png';
import Button from 'react-bootstrap/Button';
import { UserInterface } from '../interfaces/interfaces';

function Login({ user }: { user: UserInterface | null }) {
    return (
        <>
            <div style={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100vh',
                backgroundColor: '#f8f9fa',
            }}>
                <img
                    src={logo}
                    alt="logo"
                    style={{ marginBottom: '20px', height: '300px' }}
                />
                <Button variant="primary" style={{ width: '300px' }} href={user?.loginUrl}>
                    Login
                </Button>
            </div>
        </>
    );
}

export default Login;
