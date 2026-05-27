window.onload = () => {

    const loggedIn = sessionStorage.getItem("loggedIn");

    if (loggedIn === "true") {

        document.getElementById("login-screen").style.display = "none";
        document.getElementById("app").style.display = "block";

    } else {
        document.getElementById("login-screen").style.display = "flex";
        document.getElementById("app").style.display = "none";
    }
};

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
let changedInfoBox = document.getElementById('changed-info');

getDuration().then(d => {
    duration = Number(d);
    document.getElementById('duration').value = duration;
});

function renderSeats() {
    let html = '';

    for (let seat of seatsData.slice().reverse()) {
        html += `
            <div class="seat" id="box${seat.id}" onclick="editName(${seat.id})">
                <h3>${seat.name}</h3>
                <p>Klicken zum Bearbeiten</p>
            </div>
        `;
    }

    document.getElementById("section1").innerHTML = html;
}


function editName(id) {
    const seat = seatsData.find(s => s.id === id);

    document.getElementById(`box${id}`).innerHTML = `
 <input 
    type="text" 
    id="name${id}" 
    value="${seat.name}"
    onkeydown="handleRename(event, ${id})"
    onclick="event.stopPropagation()"
    autofocus
>

        <div class="edit-buttons">
       <button onclick="event.stopPropagation(); renameSeat(${id})">
    Speichern
</button>

<button onclick="event.stopPropagation(); stopEdit()">
    Abbrechen
</button>
        </div>
    `;
}

function stopEdit() {
    renderSeats();
}

function handleRename(event, id) {
    if (event.key === "Enter") {
        renameSeat(id);
    }

    if (event.key === "Escape") {
        stopEdit();
    }
}

async function renameSeat(id) {
    const name = document.getElementById(`name${id}`)?.value.trim();

    if (!name) return alert("Name darf nicht leer sein");

    try {
        const res = await fetch("/api/dashboard/rename", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({id, name})
        });

        if (!res.ok) {
            changedInfoBox.innerHTML = `<h2> Fehler beim Umbenennen! </h2>`;
            changedInfoBox.style.display = 'flex';
            setTimeout(() => {
                changedInfoBox.style.display = 'none';
            }, 2000);
            return;
        }

        changedInfoBox.innerHTML = `<h2> Erfolgreich umbenannt! </h2>`;
        changedInfoBox.style.display = 'flex';
        setTimeout(() => {
            changedInfoBox.style.display = 'none';
        }, 2000);

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

    button.disabled = Number(d) === Number(duration);
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
            changedInfoBox.innerHTML = `<h2> Duration bearbeitet! </h2>`;

        } else if (res.status === 400) {
            changedInfoBox.innerHTML = `<h2> Ungültiger Wert (muss > 10 sein) </h2>`;
        } else if (res.status === 404) {
            changedInfoBox.innerHTML = `<h2>Wert zu klein</h2>`;
        } else {
            changedInfoBox.innerHTML = `<h2> Fehler beim Speichern </h2>`;
        }

        changedInfoBox.style.display = 'flex';
        setTimeout(() => {
            changedInfoBox.style.display = 'none';
        }, 2000);

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
        data.sort((a, b) => a.average - b.average);

        const leaderboard = document.getElementById("section3");

        leaderboard.innerHTML = `
        <h2 class="panel-title">Leaderboard</h2>
        <ol>
            ${data.map(item => {
            const seconds = Math.round(item.average);

            let formattedTime;

            if (seconds >= 60) {
                const minutes = Math.floor(seconds / 60);
                const remainingSeconds = seconds % 60;

                formattedTime = `${minutes} Min ${remainingSeconds} Sek`;
            } else {
                formattedTime = `${seconds} Sekunden`;
            }

            return `
                    <li>
                        ${item.name} – ${formattedTime}
                    </li>
                `;
        }).join("")}
        </ol>
    `;
    });

};

ws.onerror = (err) => console.error("Fehler:", err);
ws.onclose = () => console.log("Verbindung geschlossen");

async function login() {
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;

    const res = await fetch("/api/user/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username,
            password
        })
    });

    if (!res.ok) {
        document.getElementById("login-error").innerText =
            "Falscher Benutzername oder Passwort";
        return;
    }

    sessionStorage.setItem("loggedIn", "true");
    document.getElementById("login-screen").style.display = "none";
    document.getElementById("app").style.display = "block";
}