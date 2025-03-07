import { Spinner } from 'react-bootstrap';

function Loading() {
    return (
        <div style={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 66px)' }}>
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
                <div style={{ marginTop: 20 }}>
                    <Spinner animation='border' variant='primary' />
                </div>
            </div>
        </div>
    );
}

export default Loading;
