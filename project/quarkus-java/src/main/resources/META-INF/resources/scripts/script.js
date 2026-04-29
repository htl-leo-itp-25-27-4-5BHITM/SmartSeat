const floorCountDOM = {
    "1OG": document.getElementById("count1"),
    "2OG": document.getElementById("count2")
};

const main1 = document.getElementById('main1');
const main2 = document.getElementById('main2');

let isFirstFloor = true;
const selected1 = document.getElementById("selected1");
const selected2 = document.getElementById("selected2");
let occupiedInfo = document.getElementById('occupied-info');
let occupiedCount = -1;

const seatDOM = {
    1: document.getElementById("k1"),
    2: document.getElementById("k2"),
    3: document.getElementById("k3"),
    4: document.getElementById("k4"),
    5: document.getElementById("k5")
};

const seatDOM2 = {
    1: document.getElementById("e1"),
    2: document.getElementById("e2"),
    3: document.getElementById("e3"),
    4: document.getElementById("e4"),
    5: document.getElementById("e5")
};


const floor1 = document.getElementById("floor_1OG");
const floor2 = document.getElementById("floor_2OG");


async function loadFloor(floorNumber) {
    let floorCode = floorNumber === 1 ? "1OG" : "2OG";
    let seats = await getSeatsByFloor(floorCode);

    Object.keys(seatDOM).forEach(num => {
        let isFloor1Seat = ["1", "2", "3"].includes(num);
        seatDOM[num].style.display =
            (floorNumber === 1 && isFloor1Seat) ||
            (floorNumber === 2 && !isFloor1Seat)
                ? "block"
                : "none";
    });

    updateSeatClasses(seats);

    if (floorNumber === 1) {
        selected1.style.display = "block";
        selected2.style.display = "none";
    }
    if (floorNumber === 2) {
        selected1.style.display = "none";
        selected2.style.display = "block";
    }

    getUnoccupiedCount(floorCode);
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

async function getAllUnoccupiedCount() {
    try {
        const res = await fetch(`/api/seat/getUnoccupiedCount`);
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
            const label = data === 1 ? "Koje" : "Kojen";
            const color = data === 0 ? "red" : "rgb(29, 195, 98)";
            floorCountDOM[floor].innerText = `${data} ${label} verfügbar`;
            floorCountDOM[floor].style.color = color;
        })
        .catch(err => console.error(err));
}

function updateSeatClasses(seatData) {
    seatData.forEach(seat => {
        const num = seat.id; // <-- clean mapping
        const el = seatDOM[num];
        if (!el) return;

        el.classList.remove("occupied", "unoccupied");
        el.classList.add(seat.status ? "unoccupied" : "occupied");
    });
}

function updateEntryClasses(seatData) {
    seatData.forEach(seat => {
        const num = seat.id;
        const el = seatDOM2[num];
        if (!el) return;

        el.classList.remove("occupied", "unoccupied");
        el.classList.add(seat.status ? "unoccupied" : "occupied");
    });
}

function setMessage(count) {
    let message = "";
    occupiedInfo.style.display = 'flex';

    switch (count) {
        case 5:
            message = "Alle Kojen frei!";
            break;
        case 4:
            message = "4 Kojen frei!";
            break;
        case 3:
            message = "3 Kojen frei!";
            break;
        case 2:
            message = "2 Kojen frei!";
            break;
        case 1:
            message = "1 Koje frei!";
            break;
        case 0:
            message = "Alle Kojen belegt!";
            break;
        default:
            message = "Ungültiger Wert!";
    }
    occupiedInfo.innerHTML = `<h2>${message}</h2>`;

    setTimeout(() => {
        occupiedInfo.style.display = 'none';
    }, 1500);
}

const protocol = window.location.protocol === "https:" ? "wss" : "ws";
const ws = new WebSocket(`${protocol}://${window.location.host}/ws/seats`);
ws.onopen = () => console.log("Verbunden!");


ws.onmessage = (e) => {
    let seats = JSON.parse(e.data);

    if (!Array.isArray(seats)) seats = [seats];

    new_data = seats;
    updateSeatClasses(seats);
    updateEntryClasses(seats);
    getAllUnoccupiedCount().then(count => {
        if (occupiedCount === -1 || occupiedCount !== count) {
            setMessage(count);
            occupiedCount = count;
        }
    });

    const floors = [...new Set(seats.map(s => s.floor))];
    floors.forEach(floor => getUnoccupiedCount(floor));

    if (isFirstFloor) {
        loadFloor(1);
    } else {
        loadFloor(2);
    }

};

ws.onerror = (err) => console.error("Fehler:", err);
ws.onclose = () => console.log("Verbindung geschlossen");

function loadView(view) {
    switch (view) {
        case 1:
            main1.style.display = 'flex';
            main2.style.display = 'none';
            break;
        case 2:
            main1.style.display = 'none';
            main2.style.display = 'flex';
            break;
        default:
            main1.style.display = 'flex';
            main2.style.display = 'none';
    }
}