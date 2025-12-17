const floorCountDOM = {
    "1OG": document.getElementById("count1"),
    "2OG": document.getElementById("count2")
};

let isFirstFloor = true;
const selected1 = document.getElementById("selected1");
const selected2 = document.getElementById("selected2");

const seatDOM = {
    1: document.getElementById("k1"),
    2: document.getElementById("k2"),
    3: document.getElementById("k3"),
    4: document.getElementById("k4"),
    5: document.getElementById("k5")
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
    const isMobile = window.innerWidth <= 768;

    floor1.style.backgroundColor = floorNumber === 1 ? "#6a92f4" : "#a6bcefff";
    floor2.style.backgroundColor = floorNumber === 2 ? "#6a92f4" : "#a6bcefff";

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

function getUnoccupiedCount(floor) {
    fetch(`/api/seat/getUnoccupiedSeatsByFloor/${floor}`)
        .then(res => res.json())
        .then(data => {
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

let ws = new WebSocket(`ws://${window.location.host}/ws/seats`);
ws.onopen = () => console.log("Verbunden!");



ws.onmessage = (e) => {
    let seats = JSON.parse(e.data);


    if (!Array.isArray(seats)) seats = [seats];

    new_data = seats;
    updateSeatClasses(seats);

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
