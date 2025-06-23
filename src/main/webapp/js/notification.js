// notification.js
window.showNotification = (message, type = 'info') => {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type} fade-in`;

    // Get appropriate icon
    const icons = {
        success: 'bi-check-circle-fill',
        error: 'bi-exclamation-triangle-fill',
        info: 'bi-info-circle-fill',
        warning: 'bi-exclamation-octagon-fill'
    };

    // Notification HTML structure
    notification.innerHTML = `
        <div class="notification-icon">
            <i class="bi ${icons[type] || 'bi-bell-fill'}"></i>
        </div>
        <div class="notification-content">${message}</div>
        <button class="notification-close" aria-label="Close notification">
            <i class="bi bi-x"></i>
        </button>
    `;

    // Add to DOM
    document.body.appendChild(notification);

    // Auto-remove after delay
    const timer = setTimeout(() => {
        closeNotification(notification);
    }, 5000);

    // Manual close handler
    notification.querySelector('.notification-close').addEventListener('click', () => {
        clearTimeout(timer);
        closeNotification(notification);
    });
};

/**
 * Closes a notification with animation
 * @param {HTMLElement} notification - The notification element to close
 */
function closeNotification(notification) {
    notification.classList.add('fade-out');
    notification.addEventListener('animationend', () => {
        notification.remove();
    }, { once: true });
}

// Initialize notification system
function initNotificationSystem() {
    // Check if styles are already added
    if (!document.getElementById('notification-styles')) {
        const link = document.createElement('link');
        link.id = 'notification-styles';
        link.rel = 'stylesheet';
        link.href = 'css/notification.css';
        document.head.appendChild(link);
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', initNotificationSystem);