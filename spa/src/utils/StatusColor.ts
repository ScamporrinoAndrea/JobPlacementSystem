
export const statusColors = {
    'done': {
        backgroundColor: 'rgba(0, 255, 127, 0.2)',
        color: 'rgba(0, 255, 127, 1)',
        icon: 'bi bi-check-circle',
        text: 'Done',
        next: 'aborted',
    },
    'aborted': {
        backgroundColor: 'rgba(255, 69, 58, 0.2)',
        color: 'rgba(255, 69, 58, 1)',
        icon: 'bi bi-x-circle',
        text: 'Aborted',
        next: 'aborted',
    },
    'selection_phase': {
        backgroundColor: 'rgba(255, 204, 0, 0.2)',
        color: 'rgba(255, 204, 0, 1)',
        icon: 'bi bi-hourglass-split',
        text: 'Selection phase',
        next: 'candidate_proposal',
    },
    'created': {
        backgroundColor: 'rgba(0, 204, 255, 0.2)',
        color: 'rgba(0, 204, 255, 1)',
        icon: 'bi bi-file-earmark-plus',
        text: 'Created',
        next: 'selection_phase',
    },
    'candidate_proposal': {
        backgroundColor: 'rgba(255, 165, 0, 0.2)',
        color: 'rgba(255, 165, 0, 1)',
        icon: 'bi bi-person-check',
        text: 'Candidate proposal',
        next: 'consolidated',
    },
    'consolidated': {
        backgroundColor: 'rgba(100, 149, 237, 0.2)',
        color: 'rgba(100, 149, 237, 1)',
        icon: 'bi bi-calendar-check',
        text: 'Consolidated',
        next: 'done',
    }
};

export const employmentStateColor = {
    'employed': {
        backgroundColor: 'rgba(0, 128, 0, 0.2)',
        color: 'rgba(0, 128, 0, 1)',
        icon: 'bi bi-briefcase-fill',
        text: 'Employed',
    },
    'available': {
        backgroundColor: 'rgba(30, 144, 255, 0.2)',
        color: 'rgba(30, 144, 255, 1)',
        icon: 'bi bi-person-lines-fill',
        text: 'Available',
    },
    'not available': {
        backgroundColor: 'rgba(255, 69, 0, 0.2)',
        color: 'rgba(255, 69, 0, 1)',
        icon: 'bi bi-slash-circle',
        text: 'Not Available',
    }
};