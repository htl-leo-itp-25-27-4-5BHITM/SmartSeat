const floorCountDOM = {
    "1OG": document.getElementById("count1"),
    "2OG": document.getElementById("count2")
};

const main1 = document.getElementById('main1');
const main2 = document.getElementById('main2');
const main3 = document.getElementById('main3');

const selected1 = document.getElementById("selected1");
const selected2 = document.getElementById("selected2");
let occupiedInfo = document.getElementById('occupied-info');
let occupiedCount = -1;
let seatsData = [];
let averageWaitingTimes = {};
let currentFloor = 1;

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

const nameDom = {
    1: document.getElementById("name1"),
    2: document.getElementById("name2"),
    3: document.getElementById("name3"),
    4: document.getElementById("name4"),
    5: document.getElementById("name5")
};


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

    currentFloor = floorNumber;
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

const tooltip = document.getElementById("seat-tooltip");

let activeSeatId = null;
let tooltipInterval = null;

function updateSeatClasses(seatData) {

    seatData.forEach(seat => {

        const num = seat.id;
        const el = seatDOM[num];

        if (!el) return;

        el.classList.remove("occupied", "unoccupied");
        el.classList.add(seat.status ? "unoccupied" : "occupied");

        el.onmouseover = null;
        el.onmouseleave = null;

        el.onmouseover = () => {

            activeSeatId = seat.id;

            clearInterval(tooltipInterval);

            const updateTooltip = () => {

                if (activeSeatId !== seat.id) return;

                const currentSeat =
                    seatsData.find(s => s.id === seat.id) || seat;

                let text = currentSeat.name;

                if (currentSeat.status === false) {

                    if (currentSeat.occupiedSince) {

                        const rawDate = currentSeat.occupiedSince;

                        const normalizedDate =
                            rawDate.includes("T") &&
                            !/Z$|[+-]\d{2}:\d{2}$/.test(rawDate)
                                ? rawDate + "+02:00"
                                : rawDate;

                        const since = new Date(normalizedDate);

                        const now = new Date();

                        const diffMs = now - since;

                        const totalSeconds =
                            Math.floor(diffMs / 1000);

                        const hours =
                            Math.floor(totalSeconds / 3600);

                        const minutes =
                            Math.floor((totalSeconds % 3600) / 60);

                        const seconds =
                            totalSeconds % 60;

                        let timeString = "";

                        if (hours > 0) {

                            timeString =
                                `${hours}h ${minutes}m ${seconds}s`;

                        } else if (minutes > 0) {

                            timeString =
                                `${minutes}m ${seconds}s`;

                        } else {

                            timeString =
                                `${seconds}s`;
                        }

                        text +=
                            `\nBesetzt seit: ${timeString}`;
                        // const averageWaitingTime =
                        //     averageWaitingTimes[currentSeat.id];
                        //
                        // if (averageWaitingTime) {
                        //
                        //     const estimatedEnd =
                        //         new Date(
                        //             since.getTime() +
                        //             averageWaitingTime * 1000
                        //         );
                        //
                        //     const now = new Date();
                        //
                        //     if (now > estimatedEnd) {
                        //
                        //         text +=
                        //             `\nLänger als erwartet`;
                        //
                        //     } else {
                        //
                        //         const estimatedTime =
                        //             estimatedEnd.toLocaleTimeString(
                        //                 "de-DE",
                        //                 {
                        //                     hour: "2-digit",
                        //                     minute: "2-digit"
                        //                 }
                        //             );
                        //
                        //         text +=
                        //             `\nWahrscheinlich frei bis: ${estimatedTime}`;
                        //     }
                        // }

                    } else {

                        text +=
                            `\nGerade eben besetzt`;
                    }
                }

                tooltip.innerText = text;
            };

            updateTooltip();

            tooltip.style.display = "block";

            tooltip.style.left =
                el.offsetLeft +
                el.offsetWidth / 2 +
                "px";

            tooltip.style.top =
                el.offsetTop + "px";

            tooltipInterval =
                setInterval(updateTooltip, 1000);
        };

        el.onmouseleave = () => {

            activeSeatId = null;

            tooltip.style.display = "none";

            clearInterval(tooltipInterval);
        };
    });
}

function updateEntryClasses(seatData) {
    seatData.forEach(seat => {
        const num = seat.id;
        const el = seatDOM2[num];
        const name = nameDom[num];
        if (!el) return;

        name.textContent = seat.name;
        el.classList.remove("occupied", "unoccupied");
        el.classList.add(seat.status ? "unoccupied" : "occupied");
    });
}

function setMessage(count) {
    let message;
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

// async function getAverageWaitingTimesBySeat() {
//     try {
//         const res = await fetch('/api/dashboard/histories');
//
//         if (!res.ok) {
//             throw new Error(`HTTP ${res.status}`);
//         }
//
//         return await res.json();
//
//     } catch (err) {
//         console.error('Failed to load histories:', err);
//         return [];
//     }
// }
//
// async function loadAverageWaitingTimes() {
//
//     const histories = await getAverageWaitingTimesBySeat();
//
//     averageWaitingTimes = {};
//
//     histories.forEach(entry => {
//
//         averageWaitingTimes[entry.seat_id] =
//             entry.average;
//     });
// }

const protocol = window.location.protocol === "https:" ? "wss" : "ws";
const ws = new WebSocket(`${protocol}://${window.location.host}/ws/seats`);
ws.onopen = () => console.log("Verbunden!");

ws.onmessage = (e) => {
    let seats = JSON.parse(e.data);
    // loadAverageWaitingTimes();

    if (!Array.isArray(seats)) seats = [seats];
    seatsData = seats;

    loadChart();
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

    loadFloor(currentFloor);
};

ws.onerror = (err) => console.error("Fehler:", err);
ws.onclose = () => console.log("Verbindung geschlossen");

function loadView(view) {

    switch (view) {
        case 1:
            main1.style.display = 'flex';
            main2.style.display = 'none';
            main3.style.display = 'none';
            break;

        case 2:
            main1.style.display = 'none';
            main2.style.display = 'flex';
            main3.style.display = 'none';
            break;

        case 3:
            main1.style.display = 'none'
            main2.style.display = 'none'
            main3.style.display = 'flex'
            break;
    }

}

loadView(1)

let occupancyChart = null;

function loadChart() {

    const now = new Date();

    const date =
        now.getFullYear() + "-" +
        String(now.getMonth() + 1).padStart(2, "0") + "-" +
        String(now.getDate()).padStart(2, "0");

    fetch(`/api/dashboard/history/occupancy/${date}?t=${Date.now()}`)
        .then(res => res.json())
        .then(data => {

            const hours = [];

            const k1 = [];
            const k2 = [];
            const k3 = [];
            const k4 = [];
            const k5 = [];

            data.forEach(item => {

                if (item.seatId === 1) {

                    hours.push(item.hour);
                    k1.push(item.occupancy);

                } else if (item.seatId === 2) {

                    k2.push(item.occupancy);

                } else if (item.seatId === 3) {

                    k3.push(item.occupancy);

                } else if (item.seatId === 4) {

                    k4.push(item.occupancy);

                } else if (item.seatId === 5) {

                    k5.push(item.occupancy);
                }
            });

            const ctx =
                document.getElementById("myChart")
                    .getContext("2d");

            if (occupancyChart) {
                occupancyChart.destroy();
            }

            console.log(data)
            occupancyChart = new Chart(ctx, {

                type: "line",

                data: {

                    labels: hours,

                    datasets: [

                        {
                            label: "Koje 1",
                            data: k1,
                            borderColor: "#e74c3c",
                            tension: 0.4
                        },
                        {
                            label: "Koje 2",
                            data: k2,
                            borderColor: "#3498db",
                            tension: 0.4
                        },
                        {
                            label: "Koje 3",
                            data: k3,
                            borderColor: "#2ecc71",
                            tension: 0.4
                        },
                        {
                            label: "Koje 4",
                            data: k4,
                            borderColor: "#f39c12",
                            tension: 0.4
                        },
                        {
                            label: "Koje 5",
                            data: k5,
                            borderColor: "#9b59b6",
                            tension: 0.4
                        }
                    ]
                },

                options: {

                    responsive: true,

                    maintainAspectRatio: false,

                    plugins: {

                        title: {

                            display: true,

                            text: "Auslastung der Kojen",

                            color: "#ffffff",

                            font: {

                                family: "Poppins",

                                size: 24,

                                weight: "600"
                            }
                        },

                        legend: {

                            labels: {

                                color: "#ffffff"
                            }
                        }
                    },

                    scales: {

                        x: {

                            title: {

                                display: true,

                                text: "Uhrzeit",

                                color: "#ffffff"
                            },

                            ticks: {

                                color: "#ffffff"
                            },

                            grid: {

                                color: "rgba(255,255,255,0.15)"
                            }
                        },

                        y: {

                            beginAtZero: true,

                            max: 1,

                            title: {

                                display: true,

                                text: "Auslastung",

                                color: "#ffffff"
                            },

                            ticks: {

                                color: "#ffffff"
                            },

                            grid: {

                                color: "rgba(255,255,255,0.15)"
                            }
                        }
                    }
                }
            });

        })
        .catch(err => console.error(err));
}