 /* const floorCountDOM = {
    "1OG": document.getElementById("count1"),
    "2OG": document.getElementById("count2")
};

const seatDOM = {
    1: document.getElementById("k1"),
    2: document.getElementById("k2"),
    3: document.getElementById("k3"),
    4: document.getElementById("k4"),
    5: document.getElementById("k5")
};

const floor1 = document.getElementById("floor_1OG");
const floor2 = document.getElementById("floor_2OG");

function setSessionStorage(floorCode) {
    currentFloor = floorCountDOM[floorCode];
    sessionStorage.setItem("currentFloor", floorCode);
}

async function loadFloor(floorNumber) {
    let floorCode = floorNumber === 1 ? "1OG" : "2OG";
    let seats = await getSeatsByFloor(floorCode);

    setSessionStorage(floorCode);

    Object.keys(seatDOM).forEach(num => {
        let isFloor1Seat = ["1", "2", "3"].includes(num);
        seatDOM[num].style.display =
            (floorNumber === 1 && isFloor1Seat) ||
                (floorNumber === 2 && !isFloor1Seat)
                ? "block"
                : "none";
    });

    updateSeatClasses(seats);


    const isMobile = window.innerWidth <= 768;


    document.getElementById("floor_1OG").style.backgroundColor =
        floorNumber === 1 ? "#6a92f4" : "#b7cbfa";

    document.getElementById("floor_2OG").style.backgroundColor =
        floorNumber === 2 ? "#6a92f4" : "#b7cbfa";

    floor1.style.marginTop = "0";
    floor1.style.marginLeft = "0";
    floor2.style.marginTop = "0";
    floor2.style.marginLeft = "0";

    if (floorNumber === 1) {
        if (isMobile) {
            floor1.style.marginTop = "5%";
        } else {
            floor1.style.marginLeft = "10%";
        }
    }

    if (floorNumber === 2) {
        if (isMobile) {
            floor2.style.marginTop = "5%";
        } else {
            floor2.style.marginLeft = "10%";
        }
    }

}

async function getSeatsByFloor(floor) {
    try {
        const res = await fetch(`/api/seat/getSeatsByFloor/${floor}`);
        return await res.json();
    } catch (err) {
        console.error(err);
        return [];
    }
}


function getUnoccupiedCount(floor) {
    fetch(`/api/seat/getUnoccupiedSeatsByFloor/${floor}`)
        .then(res => res.json())
        .then(data => {
            console.log(data);
            const label = data === 1 ? "Koje" : "Kojen";
            const color = data === 0 ? "red" : "greenyellow";
            floorCountDOM[floor].innerText = `${data} ${label} verfügbar`;
            floorCountDOM[floor].style.color = color;
        })
        .catch(err => console.error(err));
}


function updateSeatClasses(seatData) {
    seatData.forEach(seat => {
        const match = seat.name.match(/Koje\s*(\d+)/i);
        if (!match) return;

        const num = match[1];
        const el = seatDOM[num];
        if (!el) return;

        el.classList.remove("occupied", "unoccupied");
        el.classList.add(seat.status ? "unoccupied" : "occupied");
    });
}

let currentFloor = sessionStorage.getItem("currentFloor");
if (currentFloor) {
    loadFloor(currentFloor === "1OG" ? 1 : 2);
} else {
    loadFloor(1);
}
getUnoccupiedCount("1OG");
getUnoccupiedCount("2OG");
*/


const floorCountDOM = {
    "1OG": document.getElementById("count1"),
    "2OG": document.getElementById("count2")
};

const seatDOM = {
    1: document.getElementById("k1"),
    2: document.getElementById("k2"),
    3: document.getElementById("k3"),
    4: document.getElementById("k4"),
    5: document.getElementById("k5")
};

const floor1 = document.getElementById("floor_1OG");
const floor2 = document.getElementById("floor_2OG");

let currentFloor = sessionStorage.getItem("currentFloor") || "1OG";

const protocol = window.location.protocol === "https:" ? "wss" : "ws";
const ws = new WebSocket(`${protocol}://${window.location.host}/ws/seats`);

ws.onopen = () => {
    console.log("WebSocket verbunden");
    ws.send(JSON.stringify({ type: "subscribe", floor: currentFloor }));
};

ws.onmessage = (event) => {
    const message = JSON.parse(event.data);

    if (message.type === "seatsUpdate") {
        // Sitzbelegung aktualisieren
        updateSeatClasses(message.data);
        updateUnoccupiedCount(message.data);
    }

    if (message.type === "floorChange") {
        loadFloor(message.floor === "1OG" ? 1 : 2);
    }
};

ws.onerror = (err) => console.error("WebSocket Fehler:", err);

ws.onclose = () => console.log("WebSocket Verbindung geschlossen");

function setSessionStorage(floorCode) {
    currentFloor = floorCode;
    sessionStorage.setItem("currentFloor", floorCode);
    ws.send(JSON.stringify({ type: "subscribe", floor: floorCode }));
}

function loadFloor(floorNumber) {
    const floorCode = floorNumber === 1 ? "1OG" : "2OG";
    setSessionStorage(floorCode);

    Object.keys(seatDOM).forEach(num => {
        const isFloor1Seat = ["1", "2", "3"].includes(num);
        seatDOM[num].style.display =
            (floorNumber === 1 && isFloor1Seat) || (floorNumber === 2 && !isFloor1Seat)
                ? "block"
                : "none";
    });

    const isMobile = window.innerWidth <= 768;
    floor1.style.marginTop = floor1.style.marginLeft = "0";
    floor2.style.marginTop = floor2.style.marginLeft = "0";

    if (floorNumber === 1) isMobile ? floor1.style.marginTop = "5%" : floor1.style.marginLeft = "10%";
    if (floorNumber === 2) isMobile ? floor2.style.marginTop = "5%" : floor2.style.marginLeft = "10%";

    floor1.style.backgroundColor = floorNumber === 1 ? "#6a92f4" : "#b7cbfa";
    floor2.style.backgroundColor = floorNumber === 2 ? "#6a92f4" : "#b7cbfa";
}

function updateSeatClasses(seatData) {
    seatData.forEach(seat => {
        const match = seat.name.match(/Koje\s*(\d+)/i);
        if (!match) return;

        const num = match[1];
        const el = seatDOM[num];
        if (!el) return;

        el.classList.remove("occupied", "unoccupied");
        el.classList.add(seat.status ? "unoccupied" : "occupied");
    });
}

function updateUnoccupiedCount(seatData) {
    const counts = { "1OG": 0, "2OG": 0 };

    seatData.forEach(seat => {
        if (seat.status) {
            counts[seat.floor] = (counts[seat.floor] || 0) + 1;
        }
    });

    Object.keys(counts).forEach(floor => {
        const data = counts[floor];
        const label = data === 1 ? "Koje" : "Kojen";
        const color = data === 0 ? "red" : "greenyellow";
        floorCountDOM[floor].innerText = `${data} ${label} verfügbar`;
        floorCountDOM[floor].style.color = color;
    });
}

// Initiale Floor-Ladung
loadFloor(currentFloor === "1OG" ? 1 : 2);
