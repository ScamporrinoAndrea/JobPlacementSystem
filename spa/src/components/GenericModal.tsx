import { Button, Modal } from 'react-bootstrap';

function GenericModal({ showModal, setShowModal, msgModal }: { showModal: boolean, setShowModal: any, msgModal: { header: string, body: string, method: any } }) {
    return (
        <Modal show={showModal} onHide={() => setShowModal(false)} centered>
            <Modal.Header closeButton>
                <Modal.Title>{msgModal.header}</Modal.Title>
            </Modal.Header>
            <Modal.Body>{msgModal.body}</Modal.Body>
            <Modal.Footer>
                <Button
                    variant='outline-secondary'
                    onClick={() => {
                        setShowModal(false);
                    }}
                >
                    No
                </Button>
                <Button variant='outline-primary' onClick={msgModal.method}>
                    Yes
                </Button>
            </Modal.Footer>
        </Modal>
    );
}


export default GenericModal;
