const {
    canonicalState,
    stateLabel,
    displayFloor,
    formatElapsed,
    shiftIsoDate,
    groupSeatsByFloor,
    buildChartModel
} = globalThis.SmartSeatLogic;

const views = new Map(
    [...document.querySelectorAll(".view-button")].map(button => [button.dataset.view, {
        button,
        panel: document.getElementById(button.getAttribute("aria-controls"))
    }])
);

const floorMaps = document.getElementById("floor-maps");
const entries = document.getElementById("entries");
const seatDetails = document.getElementById("seat-details");
const occupiedInfo = document.getElementById("occupied-info");
const chartDateLabel = document.getElementById("chart-date-label");
const previousDateButton = document.getElementById("previous-date");
const nextDateButton = document.getElementById("next-date");
const chartMessage = document.getElementById("chart-message");
const chartContent = document.getElementById("chart-content");
const chartValuesAccessible = document.getElementById("chart-values-accessible");
const locationMessage = document.getElementById("location-message");

let seatsData = [];
let configuredFloors = [];
let selectedSeatId = null;
let activeView = "map";
let previousFreeCount = null;
let occupancyChart = null;
let loadedChartKey = null;
let catalogSignature = "";
let chartRequestCount = 0;
let selectedChartDate = localIsoDate();

function seatAccessibleText(seat) {
    const state = canonicalState(seat);
    const duration = state === "OCCUPIED" ? `, belegt seit ${formatElapsed(seat.occupiedSince)}` : "";
    return `${seat.name}, ${stateLabel(state)}${duration}`;
}

function showSeatDetails(seatId) {
    const seat = seatsData.find(item => Number(item.id) === Number(seatId));
    if (!seat) return;
    selectedSeatId = seat.id;
    const state = canonicalState(seat);
    seatDetails.replaceChildren();
    const heading = document.createElement("h2");
    heading.textContent = seat.name;
    const location = document.createElement("p");
    location.textContent = `${displayFloor(seat.floor)} · ${seat.wing}`;
    const status = document.createElement("p");
    status.className = `state-text state-${state.toLowerCase()}`;
    status.textContent = `Status: ${stateLabel(state)}`;
    seatDetails.append(heading, location, status);
    if (state === "OCCUPIED") {
        const duration = document.createElement("p");
        duration.dataset.role = "duration";
        duration.textContent = `Aktuelle Belegungsdauer: ${formatElapsed(seat.occupiedSince)}`;
        seatDetails.append(duration);
    }
}

function createMarker(seat) {
    const marker = document.createElement("button");
    const state = canonicalState(seat);
    marker.type = "button";
    marker.className = `seat-marker state-${state.toLowerCase()}`;
    marker.style.setProperty("--map-x", `${Number(seat.mapX) * 100}%`);
    marker.style.setProperty("--map-y", `${Number(seat.mapY) * 100}%`);
    marker.dataset.seatId = seat.id;
    marker.setAttribute("aria-label", seatAccessibleText(seat));
    marker.title = seatAccessibleText(seat);
    marker.addEventListener("focus", () => showSeatDetails(seat.id));
    marker.addEventListener("click", () => showSeatDetails(seat.id));
    marker.addEventListener("pointerenter", () => showSeatDetails(seat.id));
    return marker;
}

function renderFloorMaps() {
    floorMaps.replaceChildren();
    groupSeatsByFloor(seatsData, configuredFloors)
        .forEach(({floor, seats: floorSeats, placeable, unplaced}) => {
        const freeCount = floorSeats.filter(seat => canonicalState(seat) === "FREE").length;
        const panel = document.createElement("article");
        panel.className = "floor-panel";
        panel.dataset.floor = floor;
        const header = document.createElement("header");
        const title = document.createElement("h3");
        title.textContent = displayFloor(floor);
        const count = document.createElement("p");
        count.className = "floor-count";
        count.textContent = `${freeCount} ${freeCount === 1 ? "Sitzplatz" : "Sitzplätze"} frei`;
        header.append(title, count);
        const map = document.createElement("div");
        map.className = "floor-map";
        map.setAttribute("aria-label", `Karte ${displayFloor(floor)}`);
        placeable.forEach(seat => map.append(createMarker(seat)));
        if (floorSeats.length === 0) {
            const empty = document.createElement("p");
            empty.className = "map-empty";
            empty.textContent = "Keine Sitzplätze auf diesem Stockwerk.";
            map.append(empty);
        }
        panel.append(header, map);
        if (unplaced.length > 0) {
            const fallback = document.createElement("p");
            fallback.className = "unplaced-seats";
            fallback.textContent = `Ohne Kartenposition: ${unplaced.map(seat => seat.name).join(", ")}`;
            panel.append(fallback);
        }
        floorMaps.append(panel);
        });
    focusRequestedFloor();
}

function renderList() {
    entries.replaceChildren();
    [...seatsData].sort((a, b) => Number(a.id) - Number(b.id)).forEach(seat => {
        const state = canonicalState(seat);
        const entry = document.createElement("button");
        entry.type = "button";
        entry.className = "seat-entry";
        entry.addEventListener("click", () => showSeatDetails(seat.id));
        const text = document.createElement("span");
        const name = document.createElement("strong");
        name.textContent = seat.name;
        const location = document.createElement("small");
        location.textContent = `${displayFloor(seat.floor)} · ${seat.wing}`;
        text.append(name, location);
        const status = document.createElement("span");
        status.className = `state-badge state-${state.toLowerCase()}`;
        status.textContent = stateLabel(state);
        entry.append(text, status);
        entries.append(entry);
    });
}

function focusRequestedFloor() {
    const requested = new URLSearchParams(window.location.search).get("floor");
    if (!requested) return;
    const normalized = requested.replace(".", "").toUpperCase();
    const panel = [...document.querySelectorAll(".floor-panel")]
        .find(item => String(item.dataset.floor).replace(".", "").toUpperCase() === normalized);
    locationMessage.hidden = false;
    if (panel) {
        panel.classList.add("floor-panel-focused");
        locationMessage.textContent = `${displayFloor(panel.dataset.floor)} wurde hervorgehoben. Alle Stockwerke bleiben sichtbar.`;
    } else {
        locationMessage.textContent = "Das angeforderte Stockwerk wurde nicht gefunden. Alle Stockwerke werden angezeigt.";
    }
}

function announceFreeCount() {
    const count = seatsData.filter(seat => canonicalState(seat) === "FREE").length;
    if (previousFreeCount !== null && previousFreeCount !== count) {
        occupiedInfo.textContent = count === 0
            ? "Alle Sitzplätze sind belegt."
            : `${count} ${count === 1 ? "Sitzplatz ist" : "Sitzplätze sind"} frei.`;
        occupiedInfo.classList.add("visible");
        window.setTimeout(() => occupiedInfo.classList.remove("visible"), 1800);
    }
    previousFreeCount = count;
}

function renderSnapshot(seats) {
    const previousById = new Map(seatsData.map(seat => [Number(seat.id), seat]));
    seatsData = Array.isArray(seats) ? seats : [seats];
    renderFloorMaps();
    renderList();
    announceFreeCount();
    if (selectedSeatId !== null) showSeatDetails(selectedSeatId);
    const nextSignature = seatsData.map(seat => `${seat.id}:${seat.name}`).join("|");
    const analyticsChanged = seatsData.some(seat => {
        const previous = previousById.get(Number(seat.id));
        return previous && (previous.name !== seat.name ||
            (canonicalState(previous) === "OCCUPIED" && canonicalState(seat) === "FREE"));
    });
    if (analyticsChanged && activeView === "chart") loadChart(true);
    catalogSignature = nextSignature;
}

function activateView(viewName) {
    if (!views.has(viewName)) return;
    const changed = activeView !== viewName;
    activeView = viewName;
    views.forEach(({button, panel}, name) => {
        const active = name === viewName;
        button.classList.toggle("active", active);
        button.setAttribute("aria-pressed", String(active));
        panel.hidden = !active;
    });
    if (viewName === "chart") loadChart(changed);
}

views.forEach(({button}, name) => button.addEventListener("click", () => activateView(name)));

function localIsoDate(date = new Date()) {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
}

function renderAccessibleChartValues(hours, seatSeries) {
    chartValuesAccessible.replaceChildren();
    const heading = document.createElement("h3");
    heading.textContent = `Stündliche Auslastung am ${selectedChartDate} in Prozent`;
    const list = document.createElement("ul");
    hours.forEach((hour, index) => {
        const item = document.createElement("li");
        const values = seatSeries.map(series => `${series.name}: ${series.values[index].toFixed(2)} %`);
        item.textContent = `${hour}: ${values.join(", ")}`;
        list.append(item);
    });
    chartValuesAccessible.append(heading, list);
}

async function loadChart(force = false) {
    const date = selectedChartDate;
    const requestKey = `${date}:${catalogSignature}`;
    if (!force && loadedChartKey === requestKey) return;
    try {
        chartRequestCount += 1;
        document.getElementById("chart-view").dataset.requestCount = String(chartRequestCount);
        const response = await fetch(`/api/dashboard/history/occupancy/${date}`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const data = await response.json();
        loadedChartKey = requestKey;
        if (!Array.isArray(data) || data.length === 0 || !data.some(item => Number(item.occupancy) > 0)) {
            chartMessage.hidden = false;
            chartContent.hidden = true;
            if (occupancyChart) {
                occupancyChart.destroy();
                occupancyChart = null;
            }
            chartValuesAccessible.replaceChildren();
            return;
        }

        const {hours, seatSeries} = buildChartModel(data);

        renderAccessibleChartValues(hours, seatSeries);
        chartMessage.hidden = true;
        chartContent.hidden = false;
        if (typeof Chart === "undefined") return;
        if (occupancyChart) occupancyChart.destroy();
        const colors = ["#ff7675", "#74b9ff", "#55efc4", "#ffeaa7", "#a29bfe", "#fd79a8", "#81ecec"];
        occupancyChart = new Chart(document.getElementById("myChart"), {
            type: "line",
            data: {
                labels: hours,
                datasets: seatSeries.map((series, index) => ({
                    label: series.name,
                    data: series.values,
                    borderColor: colors[index % colors.length],
                    backgroundColor: colors[index % colors.length],
                    tension: 0.25,
                    pointRadius: 3,
                    pointHoverRadius: 6,
                    pointHitRadius: 12
                }))
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {mode: "index", intersect: false},
                plugins: {
                    title: {display: true, text: "Belegte Zeit je Sitzplatz-Stunde", color: "#ffffff"},
                    legend: {labels: {color: "#ffffff"}},
                    tooltip: {mode: "index", intersect: false}
                },
                scales: {
                    x: {
                        title: {display: true, text: "Uhrzeit", color: "#ffffff"},
                        ticks: {color: "#ffffff"},
                        grid: {color: "rgba(255,255,255,0.12)"}
                    },
                    y: {
                        beginAtZero: true,
                        min: 0,
                        max: 100,
                        title: {display: true, text: "Auslastung – belegte Zeit (%)", color: "#ffffff"},
                        ticks: {color: "#ffffff", callback: value => `${value} %`},
                        grid: {color: "rgba(255,255,255,0.12)"}
                    }
                }
            }
        });
    } catch (error) {
        console.error("Auslastung konnte nicht geladen werden", error);
        chartContent.hidden = true;
        chartMessage.hidden = false;
        chartMessage.textContent = "Die Auslastungsdaten konnten nicht geladen werden.";
    }
}

function renderSelectedChartDate() {
    const [year, month, day] = selectedChartDate.split("-").map(Number);
    const date = new Date(year, month - 1, day, 12);
    chartDateLabel.dateTime = selectedChartDate;
    chartDateLabel.textContent = new Intl.DateTimeFormat("de-AT", {
        weekday: "short",
        day: "2-digit",
        month: "2-digit",
        year: "numeric"
    }).format(date);
}

function changeChartDate(days) {
    selectedChartDate = shiftIsoDate(selectedChartDate, days);
    renderSelectedChartDate();
    loadedChartKey = null;
    if (activeView === "chart") loadChart(true);
}

renderSelectedChartDate();
previousDateButton.addEventListener("click", () => changeChartDate(-1));
nextDateButton.addEventListener("click", () => changeChartDate(1));

window.setInterval(() => {
    if (selectedSeatId !== null) showSeatDetails(selectedSeatId);
    document.querySelectorAll(".seat-marker").forEach(marker => {
        const seat = seatsData.find(item => Number(item.id) === Number(marker.dataset.seatId));
        if (seat) {
            marker.setAttribute("aria-label", seatAccessibleText(seat));
            marker.title = seatAccessibleText(seat);
        }
    });
}, 1000);

const protocol = window.location.protocol === "https:" ? "wss" : "ws";
const socket = new WebSocket(`${protocol}://${window.location.host}/ws/seats`);
socket.onmessage = event => renderSnapshot(JSON.parse(event.data));
socket.onerror = error => console.error("WebSocket-Fehler", error);

fetch("/api/seat/getFloors")
    .then(response => response.ok ? response.json() : [])
    .then(floors => {
        configuredFloors = Array.isArray(floors) ? floors : [];
        if (seatsData.length > 0) renderFloorMaps();
    })
    .catch(error => console.error("Stockwerke konnten nicht geladen werden", error));

activateView("map");
window.SmartSeat = {
    renderSnapshot,
    activateView,
    formatElapsed,
    loadChart,
    getChartRequestCount: () => chartRequestCount
};
