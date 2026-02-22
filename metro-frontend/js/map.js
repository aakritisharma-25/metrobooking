// Metro line colors
const LINE_COLORS = {
    'YELLOW': '#FFD700',
    'BLUE': '#1E90FF',
    'PINK': '#FF69B4',
    'ORANGE': '#FF8C00'
};

// All stops with coordinates
const STOPS_DATA = [
    {id: 1, name: "Rajiv Chowk", code: "RJC", lat: 28.6328, lng: 77.2197, interchange: true},
    {id: 2, name: "Kashmere Gate", code: "KSG", lat: 28.6678, lng: 77.2285, interchange: true},
    {id: 3, name: "Central Secretariat", code: "CSS", lat: 28.6149, lng: 77.2090, interchange: true},
    {id: 4, name: "Dwarka Sector 21", code: "DWK", lat: 28.5521, lng: 77.0588, interchange: false},
    {id: 5, name: "Vaishali", code: "VSH", lat: 28.6453, lng: 77.3411, interchange: false},
    {id: 6, name: "Huda City Centre", code: "HCC", lat: 28.4595, lng: 77.0266, interchange: false},
    {id: 7, name: "Samaypur Badli", code: "SPB", lat: 28.7452, lng: 77.1429, interchange: false},
    {id: 8, name: "New Delhi", code: "NDL", lat: 28.6419, lng: 77.2194, interchange: true},
    {id: 9, name: "Chandni Chowk", code: "CHC", lat: 28.6506, lng: 77.2303, interchange: false},
    {id: 10, name: "Welcome", code: "WLC", lat: 28.6726, lng: 77.2942, interchange: true},
    {id: 11, name: "Noida Sector 18", code: "NS18", lat: 28.57, lng: 77.321, interchange: false},
    {id: 12, name: "Botanical Garden", code: "BTG", lat: 28.5644, lng: 77.335, interchange: true},
    {id: 13, name: "Janakpuri West", code: "JNW", lat: 28.6219, lng: 77.0831, interchange: false},
    {id: 14, name: "Lajpat Nagar", code: "LJN", lat: 28.57, lng: 77.2432, interchange: true},
    {id: 15, name: "IGI Airport", code: "IGI", lat: 28.5562, lng: 77.1, interchange: false}
];

// Metro lines with stop IDs in order
const LINES_DATA = [
    {name: "Yellow Line", color: "YELLOW", stops: [7, 2, 1, 3, 6]},
    {name: "Blue Line", color: "BLUE", stops: [4, 1, 2, 5]},
    {name: "Pink Line", color: "PINK", stops: [8, 10, 13, 1, 14]},
    {name: "Orange Line", color: "ORANGE", stops: [4, 15, 9]}
];

let dashboardMap = null;
let bookingMap = null;

function initDashboardMap() {
    // Create map centered on Delhi
    dashboardMap = L.map('map').setView([28.6139, 77.2090], 11);

    // Add OpenStreetMap tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(dashboardMap);

    // Draw all metro lines
    LINES_DATA.forEach(line => {
        const coords = line.stops.map(stopId => {
            const stop = STOPS_DATA.find(s => s.id === stopId);
            return [stop.lat, stop.lng];
        });

        L.polyline(coords, {
            color: LINE_COLORS[line.color],
            weight: 5,
            opacity: 0.8
        }).addTo(dashboardMap).bindPopup(line.name);
    });

    // Add stop markers
    STOPS_DATA.forEach(stop => {
        const markerColor = stop.interchange ? '#e94560' : '#ffffff';

        const icon = L.divIcon({
            className: '',
            html: `<div style="
                width: 14px;
                height: 14px;
                background: ${markerColor};
                border: 3px solid white;
                border-radius: 50%;
                box-shadow: 0 0 6px rgba(0,0,0,0.5);
            "></div>`,
            iconSize: [14, 14],
            iconAnchor: [7, 7]
        });

        L.marker([stop.lat, stop.lng], {icon})
            .addTo(dashboardMap)
            .bindPopup(`
                <b>${stop.name}</b><br>
                Code: ${stop.code}<br>
                ${stop.interchange ? '🔄 Interchange Station' : ''}
            `);
    });
}

function initBookingMap(sourceName, destName, pathSegments) {
    if (bookingMap) {
        bookingMap.remove();
    }

    bookingMap = L.map('booking-map').setView([28.6139, 77.2090], 11);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(bookingMap);

    if (!pathSegments || pathSegments.length === 0) return;

    // Draw the selected route
    let currentLineCoords = [];
    let currentColor = '#e94560';

    pathSegments.forEach((segment, index) => {
        const stop = STOPS_DATA.find(s => s.name === segment.stopName);
        if (!stop) return;

        const color = LINE_COLORS[segment.routeColor] || '#e94560';

        if (index === 0) {
            currentColor = color;
        }

        if (color !== currentColor) {
            // Draw previous segment
            if (currentLineCoords.length > 1) {
                L.polyline(currentLineCoords, {
                    color: currentColor,
                    weight: 6,
                    opacity: 0.9
                }).addTo(bookingMap);
            }
            currentLineCoords = [[stop.lat, stop.lng]];
            currentColor = color;
        } else {
            currentLineCoords.push([stop.lat, stop.lng]);
        }

        // Add marker
        const isSource = segment.stopName === sourceName;
        const isDest = segment.stopName === destName;

        const icon = L.divIcon({
            className: '',
            html: `<div style="
                width: ${isSource || isDest ? '18px' : '12px'};
                height: ${isSource || isDest ? '18px' : '12px'};
                background: ${isSource ? '#00ff88' : isDest ? '#e94560' : 'white'};
                border: 3px solid white;
                border-radius: 50%;
                box-shadow: 0 0 8px rgba(0,0,0,0.5);
            "></div>`,
            iconSize: [18, 18],
            iconAnchor: [9, 9]
        });

        L.marker([stop.lat, stop.lng], {icon})
            .addTo(bookingMap)
            .bindPopup(`
                <b>${segment.stopName}</b><br>
                ${segment.routeName}<br>
                ${segment.interchange ? '🔄 Interchange' : ''}
                ${isSource ? '🟢 Start' : isDest ? '🔴 End' : ''}
            `);
    });

    // Draw last segment
    if (currentLineCoords.length > 1) {
        L.polyline(currentLineCoords, {
            color: currentColor,
            weight: 6,
            opacity: 0.9
        }).addTo(bookingMap);
    }

    // Fit map to route
    const allCoords = pathSegments
        .map(s => STOPS_DATA.find(stop => stop.name === s.stopName))
        .filter(Boolean)
        .map(s => [s.lat, s.lng]);

    if (allCoords.length > 0) {
        bookingMap.fitBounds(allCoords, {padding: [30, 30]});
    }
}