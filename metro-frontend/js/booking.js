// Load stops into dropdowns
async function loadStops() {
    try {
        const response = await fetch('http://localhost:8080/api/stops', {
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        });
        
        const stops = await response.json();
        console.log('Stops:', stops);

        const sourceSelect = document.getElementById('source-stop');
        const destSelect = document.getElementById('dest-stop');

        // Clear existing options except placeholder
        sourceSelect.innerHTML = '<option value="">Select source station...</option>';
        destSelect.innerHTML = '<option value="">Select destination station...</option>';

        stops.forEach(stop => {
            const label = `${stop.name} (${stop.code})${stop.isInterchange ? ' 🔄' : ''}`;
            
            const opt1 = document.createElement('option');
            opt1.value = stop.id;
            opt1.textContent = label;
            sourceSelect.appendChild(opt1);

            const opt2 = document.createElement('option');
            opt2.value = stop.id;
            opt2.textContent = label;
            destSelect.appendChild(opt2);
        });

        console.log('Dropdown populated with', stops.length, 'stops');

    } catch(err) {
        console.error('Failed to load stops:', err);
    }
}

// Swap source and destination
function swapStops() {
    const src = document.getElementById('source-stop');
    const dest = document.getElementById('dest-stop');
    const temp = src.value;
    src.value = dest.value;
    dest.value = temp;
}

// Book a ride
async function bookRide() {
    const sourceStopId = document.getElementById('source-stop').value;
    const destStopId = document.getElementById('dest-stop').value;

    // Validate
    if (!sourceStopId || !destStopId) {
        alert('Please select both source and destination!');
        return;
    }

    if (sourceStopId === destStopId) {
        alert('Source and destination cannot be the same!');
        return;
    }

    // Show loading
    showLoading(true);
    hideResult();
    hideError();

    try {
        const data = await apiCall('/api/bookings', 'POST', {
            sourceStopId: parseInt(sourceStopId),
            destinationStopId: parseInt(destStopId)
        });

        if (data.error) {
            showError(data.error);
        } else {
            showResult(data);
        }
    } catch (err) {
        showError('Something went wrong. Please try again!');
    } finally {
        showLoading(false);
    }
}

function showResult(data) {
    localStorage.setItem('lastBooking', JSON.stringify(data));
    window.location.href = 'result.html';
}
function buildTimeline(path) {
    const timeline = document.getElementById('route-timeline');
    timeline.innerHTML = '';

    const lineColors = {
        'YELLOW': '#FFD700',
        'BLUE': '#1E90FF',
        'PINK': '#FF69B4',
        'ORANGE': '#FF8C00'
    };

    path.forEach((segment, index) => {
        const isFirst = index === 0;
        const isLast = index === path.length - 1;
        const color = lineColors[segment.routeColor] || '#00d4ff';

        const item = document.createElement('div');
        item.className = 'timeline-item';

        item.innerHTML = `
            <div class="timeline-left">
                <div class="timeline-dot" style="border-color: ${isFirst ? '#00ff88' : isLast ? '#e94560' : color}"></div>
                ${!isLast ? `<div class="timeline-line" style="background: ${color}; opacity: 0.4;"></div>` : ''}
            </div>
            <div class="timeline-content">
                <div class="timeline-stop-name">${segment.stopName}</div>
                <div class="timeline-meta">
                    <span style="color: ${color}">● ${segment.routeName || ''}</span>
                    ${segment.interchange ? '<span class="interchange-tag">🔄 Interchange</span>' : ''}
                    ${isFirst ? '<span style="color:#00ff88">● Start</span>' : ''}
                    ${isLast ? '<span style="color:#e94560">● End</span>' : ''}
                </div>
            </div>
        `;

        timeline.appendChild(item);
    });
}

function showLoading(show) {
    document.getElementById('loading').classList.toggle('visible', show);
    document.getElementById('book-btn').disabled = show;
}

function hideResult() {
    document.getElementById('result-section').classList.remove('visible');
}

function hideError() {
    document.getElementById('error-card').classList.remove('visible');
}

function showError(msg) {
    const card = document.getElementById('error-card');
    card.classList.add('visible');
    document.getElementById('error-msg').textContent = msg;
}

// Init
checkAuth();
loadStops();