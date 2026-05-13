async function getDuration() {
    try {
        const res = await fetch(`/api/dashboard/duration`);
        return await res.json();
    } catch (err) {
        console.error(err);
        return [];
    }
}

let seatsData = [];
let duration;

getDuration().then(d => {
    duration = Number(d);
    document.getElementById('duration').value = duration;
});

function renderSeats() {
    let html = '';

    for (let seat of seatsData.slice().reverse()) {
        html += `
            <div class="seat" id="box${seat.id}">
                <h3>${seat.name}</h3>
                <button onclick="editName(${seat.id})">Bearbeiten</button>
            </div>
        `;
    }

    document.getElementById("section1").innerHTML = html;
}


function editName(id) {
    const seat = seatsData.find(s => s.id === id);

    document.getElementById(`box${id}`).innerHTML = `
        <input type="text" id="name${id}" value="${seat.name}">
    <div class="edit-buttons">
        <button onclick="renameSeat(${id})">Umbenennen</button>
        <button onclick="stopEdit()">Abbrechen</button>
    </div>
    `;
}

function stopEdit() {
    renderSeats();
}


async function renameSeat(id) {
    const name = document.getElementById(`name${id}`)?.value.trim();

    if (!name) return alert("Name darf nicht leer sein");

    try {
        const res = await fetch("/api/dashboard/rename", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ id, name })
        });

        if (!res.ok) return alert("Fehler beim Umbenennen");

        seatsData = await res.json();
        renderSeats();

    } catch (err) {
        console.error(err);
        alert("Netzwerkfehler");
    }
}

function activateButton() {
    let d = document.getElementById('duration').value;
    let button = document.getElementById('duration-button');

    if (Number(d) === Number(duration)) {
        button.disabled = true;
    } else {
        button.disabled = false;
    }
}

async function updateDuration() {
    let d = document.getElementById('duration').value;

    if (!d) return;

    try {
        const res = await fetch(`/api/dashboard/duration/${d}`, {
            method: "PUT"
        });

        if (res.ok) {
            duration = Number(d);
            activateButton();

        } else if (res.status === 400) {
            alert("Ungültiger Wert (muss > 10 sein)");
        } else if (res.status === 404) {
            alert("Wert zu klein");
        } else {
            alert("Fehler beim Speichern");
        }

    } catch (err) {
        console.error(err);
        alert("Netzwerkfehler");
    }
}

async function getAverageWaitingTimesBySeat() {
    try {
        const res = await fetch('/api/dashboard/histories');

        if (!res.ok) {
            throw new Error(`HTTP ${res.status}`);
        }

        return await res.json();

    } catch (err) {
        console.error('Failed to load histories:', err);
        return [];
    }
}



const protocol = window.location.protocol === "https:" ? "wss" : "ws";
const ws = new WebSocket(`${protocol}://${window.location.host}/ws/seats`);
ws.onopen = () => console.log("Verbunden!");


ws.onmessage = (e) => {
    let seats = JSON.parse(e.data);

    if (!Array.isArray(seats)) seats = [seats];
    seatsData = seats;

    renderSeats()

    getAverageWaitingTimesBySeat().then(data => {
        data.sort((a, b) => b.average - a.average);

        const leaderboard = document.getElementById("section3");

        leaderboard.innerHTML = `
        <ol>
            ${data.map(item => `
                <li>
                    ${item.name} – ${item.average} Sekunden
                </li>
            `).join("")}
        </ol>
    `;
    });

};

ws.onerror = (err) => console.error("Fehler:", err);
ws.onclose = () => console.log("Verbindung geschlossen");