import toast from 'react-hot-toast';

export function handleError(err: string) {
    toast.error(err, {
        position: 'bottom-center',
        duration: 5000,
        style: {
            borderRadius: '10px',
            background: 'rgba(255, 0, 0, 0.9)',
            color: '#fff',
        },
    });
}

export function handleSuccess(msg: string) {

    toast.success(msg, {
        position: 'bottom-center',
        style: {
            borderRadius: '10px',
            background: 'rgba(40, 199, 111, 0.9)',
            color: '#fff',
        },
    });
}
