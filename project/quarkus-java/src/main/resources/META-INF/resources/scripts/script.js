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

function setSessionStorage(floorCode) {
    // currentFloor updaten
    currentFloor = floorCountDOM[floorCode];

    // SessionStorage setzen
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

    document.getElementById("floor_1OG").style.backgroundColor =
        floorNumber === 1 ? "#6a92f4" : "#b7cbfa";

    document.getElementById("floor_2OG").style.backgroundColor =
        floorNumber === 2 ? "#6a92f4" : "#b7cbfa";

        
    document.getElementById("floor_1OG").style.marginLeft =
        floorNumber === 1 ? "15%" : "0";
        
    document.getElementById("floor_2OG").style.marginLeft =
        floorNumber === 2 ? "15%" : "0";
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

let currentFloor = sessionStorage.getItem("currentFloor");
if (currentFloor) {
    loadFloor(currentFloor === "1OG" ? 1 : 2);
} else {
    loadFloor(1);
}

getUnoccupiedCount("1OG");
getUnoccupiedCount("2OG");
