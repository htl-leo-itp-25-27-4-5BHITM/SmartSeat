async function getAllSeats() {
    try {
        const res = await fetch(`/api/seat/getAllSeats`);
        return await res.json();
    } catch (err) {
        console.error(err);
        return [];
    }
}

let seatsData = [];

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

getAllSeats().then(seats => {
    seatsData = seats;
    renderSeats();
});

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
    const input = document.getElementById(`name${id}`);
    const newName = input?.value?.trim();

    if (!newName) {
        alert("Name darf nicht leer sein");
        return;
    }

    try {
        const res = await fetch("/api/dashboard/rename", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                id: id,
                name: newName
            })
        });

        if (!res.ok) {
            const text = await res.text();
            console.error("Serverfehler:", res.status, text);
            alert("Änderung nicht akzeptiert");
            return;
        }

        const updatedSeats = await res.json();

        if (!Array.isArray(updatedSeats)) {
            console.error("Unerwartete Antwort:", updatedSeats);
            alert("Ungültige Serverantwort");
            return;
        }

        if (updatedSeats.length === 0) {
            alert("Keine Daten zurückgegeben");
            return;
        }

        seatsData = updatedSeats;

        renderSeats();

    } catch (err) {
        console.error("Fetch Fehler:", err);
        alert("Netzwerkfehler");
    }
}